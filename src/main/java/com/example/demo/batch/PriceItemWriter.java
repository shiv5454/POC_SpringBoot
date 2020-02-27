package com.example.demo.batch;


import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.entity.PriceEntity;
import com.example.demo.repository.PriceRepository;

public class PriceItemWriter implements ItemWriter<PriceEntity>{

	@Autowired
	private PriceRepository priceRepo;
	@Override
	public void write(List<? extends PriceEntity> items) throws Exception {
		System.out.println(items.toString());
		if(!items.isEmpty())
			items.stream().forEach(item->priceRepo.save(item));
		
	}

}
