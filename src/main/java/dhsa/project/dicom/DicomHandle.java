package dhsa.project.dicom;

import com.pixelmed.dicom.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class DicomHandle {

    private static final Logger logger = Logger.getLogger(DicomHandle.class.getName());
    private final AttributeList attrs;
    private int numRows;
    private int numCols;
    private boolean isWord;
    private boolean isRGB;
    private short[] shortPixelData;
    private byte[] bytePixelData;

    public DicomHandle(File file) throws IOException, DicomException {
        attrs = read(file.getAbsolutePath());
        loadEssentialData();
    }

    private AttributeList read(String filePath) throws IOException, DicomException {
        File dicomFile = new File(filePath);
        DicomInputStream dicomInputStream;
        if (!dicomFile.exists())
            throw new FileNotFoundException("File not found: " + filePath);

        dicomInputStream = new DicomInputStream(dicomFile);
        AttributeList attributeList = new AttributeList();
        attributeList.read(dicomInputStream);
        dicomInputStream.close();
        return attributeList;
    }

    private void loadEssentialData() throws DicomException {
        assert attrs != null;

        numRows = attrs.get(TagFromName.Rows).getIntegerValues()[0];
        numCols = attrs.get(TagFromName.Columns).getIntegerValues()[0];

        Attribute photometricAttr = attrs.get(TagFromName.PhotometricInterpretation);
        isRGB = photometricAttr.getSingleStringValueOrEmptyString().equals("RGB");

        Attribute pixelDataAttr = attrs.get(TagFromName.PixelData);
        String pixelDataVR = pixelDataAttr.getVRAsString();
        isWord = pixelDataVR.equals("OW");

        if (isWord)
            shortPixelData = pixelDataAttr.getShortValues();
        else
            bytePixelData = pixelDataAttr.getByteValues();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        assert attrs != null;
        attrs.forEach((key, value) -> {
            DicomDictionary dict = AttributeList.getDictionary();
            sb.append(key);
            sb.append(" ");
            sb.append(dict.getNameFromTag(key));
            sb.append(": [");
            sb.append(value.getVRAsString());
            sb.append(", ");
            sb.append(value.getVL());
            sb.append("] ");
            sb.append(value.getSingleStringValueOrEmptyString());
            sb.append("\n");
        });

        return sb.toString();
    }

    public String[] getStrings(AttributeTag tag) {
        assert attrs != null;
        try {
            Attribute attr = attrs.get(tag);
            if (attr == null)
                return null;
            return attr.getStringValues();
        } catch (DicomException e) {
            logger.severe("Error reading DICOM attribute " + e.getMessage());
            return null;
        }
    }

    public String getString(AttributeTag tag) {
        String[] strings = getStrings(tag);
        if (strings == null)
            return null;
        return strings[0];
    }

    public int[] getInts(AttributeTag tag) {
        assert attrs != null;
        try {
            return attrs.get(tag).getIntegerValues();
        } catch (DicomException e) {
            logger.severe("Error reading DICOM attribute " + e.getMessage());
            return null;
        }
    }

    public int getInt(AttributeTag tag) {
        return getInts(tag)[0];
    }

    public boolean hasAttr(AttributeTag tag) {
        assert attrs != null;
        return attrs.get(tag) != null;
    }

    private DicomFrame getFrame(int index) {
        try {
            int start, end;
            Object pxs;

            if (isRGB) {
                start = index * numRows * numCols * 3;
                end = start + numRows * numCols * 3;
            } else {
                start = index * numRows * numCols;
                end = start + numRows * numCols;
            }

            if (isWord)
                pxs = Arrays.copyOfRange(shortPixelData, start, end);
            else
                pxs = Arrays.copyOfRange(bytePixelData, start, end);

            return new DicomFrame(numCols, numRows, isWord, pxs);
        } catch (IOException e) {
            logger.severe("Error reading DICOM attribute list");
            return null;
        }
    }

    public String serveFrame(int index) {
        DicomFrame frame = getFrame(index);
        assert frame != null;
        return frame.toBase64();
    }

    public List<String> serveFrames(int start, int end) {
        List<String> frames = new ArrayList<>();
        for (int i = start; i < end; i++) {
            frames.add(serveFrame(i));
        }
        return frames;
    }

    public List<String> serveAllFrames() {
        int numFrames = getInt(TagFromName.NumberOfFrames);
        return serveFrames(0, numFrames);
    }

    private static class DicomFrame {
        byte[] data;

        public DicomFrame(int w, int h, boolean is_word, Object pxs) throws IOException {

            if (!is_word) {
                if (!(pxs instanceof byte[] bytes))
                    throw new IllegalArgumentException("Expected byte[] for byte data, got " + pxs.getClass().getName());

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
                image.getRaster().setDataElements(0, 0, w, h, bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                data = baos.toByteArray();
            } else {
                if (!(pxs instanceof short[] shorts))
                    throw new IllegalArgumentException("Expected short[] for word data, got " + pxs.getClass().getName());

                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_GRAY);
                image.getRaster().setDataElements(0, 0, w, h, shorts);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                data = baos.toByteArray();
            }
        }

        public String toBase64() {
            return Base64.getEncoder().encodeToString(data);
        }
    }
}
