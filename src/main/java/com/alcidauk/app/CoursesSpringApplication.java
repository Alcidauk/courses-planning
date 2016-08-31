package com.alcidauk.app;

import com.alcidauk.data.bean.*;
import com.alcidauk.data.repository.*;
import com.alcidauk.login.AccessControl;
import com.alcidauk.login.BasicAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.alcidauk.data.repository")
@EntityScan("com.alcidauk.data.bean")
@ComponentScan(basePackages = { "com.alcidauk.*" })
public class CoursesSpringApplication {

	private static final Logger log = LoggerFactory.getLogger(CoursesSpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CoursesSpringApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(UserRepository repository, WorkSessionRepository workSessionRepository,
									  WorkSessionTypeRepository coursesTypeRepo, PlanningPeriodRepository planningPeriodRepository,
									  PlanningPeriodEventTypeRepository periodEventTypeRepository, DefaultSessionRepository defaultSessionRepository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new User("Titine", "Totot"));
			repository.save(new User("John", "Doe"));

			WorkSessionType classes = new WorkSessionType("cours");
			WorkSessionType diploma = new WorkSessionType("concours");
			WorkSessionType available = new WorkSessionType("Disponible");
			coursesTypeRepo.save(Arrays.asList(classes, diploma, available));

			workSessionRepository.save(new WorkSession(getInstantFromStringDate("22/08/2016 05:00"), getInstantFromStringDate("22/08/2016 08:00"), classes, true));
			workSessionRepository.save(new WorkSession(getInstantFromStringDate("24/08/2016 10:00"), getInstantFromStringDate("24/08/2016 18:00"), diploma, true));
			workSessionRepository.save(new WorkSession(getInstantFromStringDate("24/08/2016 10:00"), getInstantFromStringDate("24/08/2016 18:00"), available, true));


			PlanningPeriodEventType coursesPeriod = new PlanningPeriodEventType(Duration.ofHours(10), classes);
			PlanningPeriodEventType concoursPeriod = new PlanningPeriodEventType(Duration.ofHours(15), diploma);
			periodEventTypeRepository.save(coursesPeriod);
			periodEventTypeRepository.save(concoursPeriod);

			List<PlanningPeriodEventType> planningPeriodEventTypes = new ArrayList<>();
			planningPeriodEventTypes.add(coursesPeriod);
			planningPeriodEventTypes.add(concoursPeriod);

			planningPeriodRepository.save(new PlanningPeriod(getInstantFromStringDateWithSeconds("29/08/2016 00:00:00"),
					getInstantFromStringDateWithSeconds("04/09/2016 23:59:59"),
					planningPeriodEventTypes, false)
			);

			defaultSessionRepository.save(new DefaultSession(1, 7, Duration.ofHours(3)));
			defaultSessionRepository.save(new DefaultSession(1, 18, Duration.ofHours(2)));
			defaultSessionRepository.save(new DefaultSession(2, 7, Duration.ofHours(3)));
			defaultSessionRepository.save(new DefaultSession(2, 18, Duration.ofHours(3)));
		};
	}

	private Instant getInstantFromStringDateWithSeconds(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		TemporalAccessor temporalAccessor = formatter.parse(date);
		LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		Instant it = Instant.from(zonedDateTime);
		log.info(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(it.atZone(ZoneId.systemDefault())) + " " + it.toEpochMilli());
		return it;
	}

	private Instant getInstantFromStringDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		TemporalAccessor temporalAccessor = formatter.parse(date);
		LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		return Instant.from(zonedDateTime);
	}

	@Bean
	public AccessControl accessControl() {
		return new BasicAccessControl();
	}
}

