package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.entity.PriceEntity;
import com.example.demo.model.PriceModel;

public class PriceItemProcessor implements ItemProcessor<PriceModel, PriceEntity>{

	private static final Logger log = LoggerFactory.getLogger(PriceItemProcessor.class);
	
	@Override
	public PriceEntity process(final PriceModel item) throws Exception {
		log.info("Processor: "+item.toString());
		return item.getEntity(item);
	}

}
