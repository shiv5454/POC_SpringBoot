package com.example.demo.batch;

import org.springframework.batch.item.ItemProcessor;
import com.example.demo.entity.PriceEntity;
import com.example.demo.model.PriceModel;


public class ActivePriceProcessor implements ItemProcessor<PriceEntity, PriceModel>{

	@Override
	public PriceModel process(PriceEntity item) throws Exception {
		
		return PriceModel.getModel(item);
	}


}
