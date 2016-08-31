package com.alcidauk.data.repository;

import com.alcidauk.data.bean.DefaultSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface DefaultSessionRepository extends JpaRepository<DefaultSession, Long> {


}
