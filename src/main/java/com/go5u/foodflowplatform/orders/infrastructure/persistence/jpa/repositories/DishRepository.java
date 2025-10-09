package com.go4u.keepitfreshplatform.orders.infrastructure.persistence.jpa.repositories;

import com.go4u.keepitfreshplatform.orders.domain.model.aggregates.Dish;
import com.go4u.keepitfreshplatform.orders.domain.model.valueobjects.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    //Optional<Dish> findByTable(Table table);

    //boolean existsByTable(Table table);

    boolean existsByName(String name);

}
