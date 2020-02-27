package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PriceEntity;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, String>{

//	@Query("FROM PriceEntity WHERE Indicator = ?1 ")
//	public List<PriceEntity> findByIndicator(Integer indicator);
}
