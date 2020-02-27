package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, String>{

}
