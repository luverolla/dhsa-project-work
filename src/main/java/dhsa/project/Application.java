package dhsa.project;

import dhsa.project.dataset.DatasetLoader;
import dhsa.project.dataset.Loader;
import dhsa.project.service.FhirWrapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	private final static String basePath = "src/main/resources/coherent-11-07-2022/csv";

	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		return (args) -> {
			Path path = Paths.get("ds_loaded_ok");
			if (!Files.exists(path)) {
				Loader ldr = new DatasetLoader();
				//ldr.set(FhirWrapper.getClient(), basePath);
				ldr.load();
				Files.createFile(path);
			}

		};
	}

}
