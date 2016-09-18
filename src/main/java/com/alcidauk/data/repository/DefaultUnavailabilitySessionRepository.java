package com.alcidauk.data.repository;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface DefaultUnavailabilitySessionRepository extends JpaRepository<DefaultUnavailabilitySession, Long> {

    @Query("select s from DefaultUnavailabilitySession s where s.user.username = ?1")
    List<DefaultUnavailabilitySession> findByUsername(String username);

}
