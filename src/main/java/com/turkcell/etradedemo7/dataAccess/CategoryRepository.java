package com.turkcell.etradedemo7.dataAccess;

import com.turkcell.etradedemo7.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);
}
