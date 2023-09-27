package pes.CultureFinderBackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pes.CultureFinderBackend.domain.services.IDomainEventService;
import pes.CultureFinderBackend.domain.services.INotificationService;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	/*
	* Descripció: Instància del servei de domini que interactúa amb els esdeveniments
	*/
	@Autowired
	private IDomainEventService iDomainEventService;

	/*
	 * Descripció: Instància del servei de domini que interactúa amb Google Firebase
	 */
	@Autowired
	private INotificationService iNotificationService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		iNotificationService.initializeFirebase();
		iDomainEventService.updateDatabase();
	}
}
