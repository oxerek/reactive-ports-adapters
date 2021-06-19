package pl.oxerek.reactiveportsadapters.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import reactor.blockhound.BlockHound;

@SpringBootApplication
@Import({
	AdaptersConfiguration.class,
	PortsConfiguration.class
})
public class ApplicationInitializer {

	static {
		//BlockHound.install();
	}

	public static void main(String[] args) {
		SpringApplication.run(ApplicationInitializer.class, args);
	}
}
