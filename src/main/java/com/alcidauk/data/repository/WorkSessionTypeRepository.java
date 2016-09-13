package com.alcidauk.data.repository;

import com.alcidauk.data.bean.WorkSessionType;
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
public interface WorkSessionTypeRepository extends JpaRepository<WorkSessionType, Long> {

    WorkSessionType findByName(String name);

    @Query("select w from WorkSessionType w where w.system = false")
    List<WorkSessionType> findNotSystem();

    @Query("select count(w) from WorkSessionType w where w.system = false")
    long countNotSystem();

}
