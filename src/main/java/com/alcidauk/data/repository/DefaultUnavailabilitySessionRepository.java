package com.alcidauk.data.repository;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.data.bean.User;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface DefaultUnavailabilitySessionRepository extends JpaRepository<DefaultUnavailabilitySession, Long> {

    List<DefaultUnavailabilitySession> findByUser(User user);

    List<DefaultUnavailabilitySession> findByUserAndLastModificationGreaterThan(User user, Instant greaterThanLastModification);

}
