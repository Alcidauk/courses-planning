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

			repository.save(userTitine);

			WorkSessionType classes = new WorkSessionType("course", false);
			WorkSessionType diploma = new WorkSessionType("exam", false);
			WorkSessionType unavailable = new WorkSessionType("unavailable", true);
			coursesTypeRepo.save(Arrays.asList(classes, diploma, unavailable));
		};
	}

	@Bean
	public AccessControl accessControl() {
		return new BasicAccessControl();
	}
}

