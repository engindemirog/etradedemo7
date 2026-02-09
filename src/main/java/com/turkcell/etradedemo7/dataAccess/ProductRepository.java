package com.turkcell.etradedemo7.dataAccess;

import com.turkcell.etradedemo7.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
