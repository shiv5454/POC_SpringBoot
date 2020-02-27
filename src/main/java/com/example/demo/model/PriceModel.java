package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import com.example.demo.entity.PriceEntity;

public class PriceModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String priceType;
	private int indicator;
	private String currencyCode;
	private double value;
	private Date validFrom;
	private Date validTo;
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public int getIndicator() {
		return indicator;
	}

	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public PriceEntity getEntity(PriceModel model) {
		PriceEntity entity = new PriceEntity();
		entity.setProductId(model.getProductId());
		entity.setIndicator(model.getIndicator());
		entity.setCurrencyCode(model.getCurrencyCode());
		entity.setPriceType(model.getPriceType());
		entity.setValidFrom(model.getValidFrom());
		entity.setValidTo(model.getValidTo());
		entity.setValue(model.getValue());
		return entity;
	}
	
	public static PriceModel getModel(PriceEntity entity){
		PriceModel model = new PriceModel();
		model.setProductId(entity.getProductId());
		model.setIndicator(entity.getIndicator());
		model.setCurrencyCode(entity.getCurrencyCode());
		model.setPriceType(entity.getPriceType());
		model.setValidFrom(entity.getValidFrom());
		model.setValidTo(entity.getValidTo());
		model.setValue(entity.getValue());
		return model;
	}

	@Override
	public String toString() {
		return "PriceModel [productId=" + productId + ", priceType=" + priceType + ", indicator=" + indicator
				+ ", currencyCode=" + currencyCode + ", value=" + value + ", validFrom=" + validFrom + ", validTo="
				+ validTo + "]";
	}
	
	
	
}
