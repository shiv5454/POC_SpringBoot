package com.example.demo.batch;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.example.demo.model.PriceModel;

public class PriceFileRowMapper implements FieldSetMapper<PriceModel>{

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public PriceModel mapFieldSet(FieldSet fieldSet) throws BindException {
		PriceModel priceModel = new PriceModel();
		priceModel.setProductId(fieldSet.readString("ProductID"));
		priceModel.setCurrencyCode(fieldSet.readString("Local Currency Code"));
		priceModel.setIndicator(Integer.parseInt(fieldSet.readString("Active Indicator")));
		priceModel.setPriceType(fieldSet.readString("Price Type"));
		priceModel.setValue(Double.parseDouble(fieldSet.readString("Price Value")));
		try {
			priceModel.setValidFrom(dateFormat.parse(fieldSet.readString("Valid From")));
			priceModel.setValidTo(dateFormat.parse(fieldSet.readString("Valid To")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return priceModel;
	}

}
