package com.alcidauk.data.repository;

import com.alcidauk.data.bean.User;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface UserRepository extends JpaRepository<User, Long> {

        User findByUsername(String username);

        List<User> findByPasswordAndUsername(String username, String password);
}
