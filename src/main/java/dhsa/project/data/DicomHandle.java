package dhsa.project.data;

import com.pixelmed.dicom.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

public class DicomHandle {

    private static class DicomFrame {
        private final int width;
        private final int height;
        byte[] data;

        public DicomFrame(int w, int h, boolean is_word, Object pxs) throws IOException {
            width = w;
            height = h;

            if (!is_word) {
                if (!(pxs instanceof byte[] bytes))
                    throw new IllegalArgumentException("Expected byte[] for byte data, got " + pxs.getClass().getName());

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                image.getRaster().setDataElements(0, 0, width, height, bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                data = baos.toByteArray();
            }

            else {
                if (!(pxs instanceof short[] shorts))
                    throw new IllegalArgumentException("Expected short[] for word data, got " + pxs.getClass().getName());

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                image.getRaster().setDataElements(0, 0, width, height, shorts);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                data = baos.toByteArray();
            }
        }

        public String toBase64() {
            return Base64.getEncoder().encodeToString(data);
        }

        public void saveTo(String path, String name) throws IOException {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            image.getRaster().setDataElements(0, 0, width, height, data);

            File outputfile = new File(path + "/" + name + ".png");
            ImageIO.write(image, "png", outputfile);
        }
    }

    private static final Logger lg = Logger.getLogger(DicomHandle.class.getName());

    private AttributeList attrs;

    private int numRows;
    private int numCols;
    private boolean isWord;
    private boolean isRGB;
    private short[] shortPixelData;
    private byte[] bytePixelData;

    private AttributeList read(String filePath) {
        File dicomFile = new File(filePath);
        DicomInputStream dicomInputStream;
        if (!dicomFile.exists()) {
            lg.severe("File not found: " + filePath);
            return null;
        }

        try {
            dicomInputStream = new DicomInputStream(dicomFile);
        } catch (IOException e) {
            lg.severe("Error reading file: " + filePath);
            return null;
        }
        AttributeList attributeList = new AttributeList();
        try {
            attributeList.read(dicomInputStream);
        } catch (DicomException | IOException e) {
            lg.severe("Error reading DICOM attribute list: " + filePath);
            return null;
        }

        try {
            dicomInputStream.close();
        } catch (IOException e) {
            lg.severe("Error closing DICOM input stream: " + filePath);
            return null;
        }

        return attributeList;
    }

    private void loadEssentialData() throws DicomException {
        assert attrs != null;

        numRows = attrs.get(TagFromName.Rows).getIntegerValues()[0];
        numCols = attrs.get(TagFromName.Columns).getIntegerValues()[0];

        Attribute photometricInterprAttr = attrs.get(TagFromName.PhotometricInterpretation);
        isRGB = photometricInterprAttr.getSingleStringValueOrEmptyString().equals("RGB");

        Attribute pixelDataAttr = attrs.get(TagFromName.PixelData);
        String pixelDataVR = pixelDataAttr.getVRAsString();
        isWord = pixelDataVR.equals("OW");

        if (isWord)
            shortPixelData = pixelDataAttr.getShortValues();
        else
            bytePixelData = pixelDataAttr.getByteValues();
    }

    public DicomHandle(File file) {
        try {
            attrs = read(file.getAbsolutePath());
            loadEssentialData();
        } catch (DicomException e) {
            lg.severe("Error reading DICOM attribute list");
        }
    }

    public DicomHandle(Path path) {
        this(path.toFile());
    }

    public DicomHandle(String filePath) {
        try {
            attrs = read(filePath);
            loadEssentialData();
        } catch (DicomException e) {
            lg.severe("Error reading DICOM attribute list");
        }
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
            lg.severe("Error reading DICOM attribute " + e.getMessage());
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
            lg.severe("Error reading DICOM attribute " + e.getMessage());
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

    public float[] getFloats(AttributeTag tag) {
        assert attrs != null;
        try {
            return attrs.get(tag).getFloatValues();
        } catch (DicomException e) {
            lg.severe("Error reading DICOM attribute " + e.getMessage());
            return null;
        }
    }

    public float getFloat(AttributeTag tag) {
        return getFloats(tag)[0];
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
            lg.severe("Error reading DICOM attribute list");
            return null;
        }
    }

    public String serveFrame(int index) {
        DicomFrame frame = getFrame(index);
        assert frame != null;
        return frame.toBase64();
    }

    public void exportFrames(String outputPath) {
        assert attrs != null;
        try {
            int numFrames = attrs.get(TagFromName.NumberOfFrames).getIntegerValues()[0];
            for (int i = 0; i < numFrames; i++) {
                DicomFrame frame = getFrame(i);
                assert frame != null;
                frame.saveTo(outputPath, "frame-" + i);
            }
        } catch (DicomException | IOException e) {
            lg.severe("Error reading DICOM attribute list");
        }
    }
}
