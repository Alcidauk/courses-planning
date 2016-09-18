package com.alcidauk.app;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.data.bean.User;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.DefaultUnavailabilitySessionRepository;
import com.alcidauk.data.repository.UserRepository;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
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
import java.util.Arrays;

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
									  WorkSessionTypeRepository coursesTypeRepo,
									  DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository) {
		return (args) -> {
			// save a couple of customers
			User userTitine = new User("Titine", "Totot");
			User userJohn = new User("John", "Doe");

			repository.save(userTitine);
			repository.save(userJohn);

			WorkSessionType classes = new WorkSessionType("course", false);
			WorkSessionType diploma = new WorkSessionType("exam", false);
			WorkSessionType unavailable = new WorkSessionType("unavailable", true);
			coursesTypeRepo.save(Arrays.asList(classes, diploma, unavailable));

			workSessionRepository.save(new WorkSession(getInstantFromStringDate("22/08/2016 05:00"), getInstantFromStringDate("22/08/2016 08:00"), classes, true));
			workSessionRepository.save(new WorkSession(getInstantFromStringDate("24/08/2016 10:00"), getInstantFromStringDate("24/08/2016 18:00"), diploma, true));

			defaultUnavailabilitySessionRepository.save(new DefaultUnavailabilitySession(1, 7, Duration.ofHours(3), userTitine));
			defaultUnavailabilitySessionRepository.save(new DefaultUnavailabilitySession(1, 18, Duration.ofHours(2), userTitine));
			defaultUnavailabilitySessionRepository.save(new DefaultUnavailabilitySession(2, 7, Duration.ofHours(3), userJohn));
			defaultUnavailabilitySessionRepository.save(new DefaultUnavailabilitySession(2, 18, Duration.ofHours(3), userJohn));
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

