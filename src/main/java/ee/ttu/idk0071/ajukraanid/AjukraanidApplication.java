package ee.ttu.idk0071.ajukraanid;

import ee.ttu.idk0071.ajukraanid.testing.TestingLombok;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AjukraanidApplication {

	public static void main(String[] args) {
        TestingLombok lomb = new TestingLombok("a", false);
		SpringApplication.run(AjukraanidApplication.class, args);
	}
}
