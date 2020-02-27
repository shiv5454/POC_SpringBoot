package com.example.demo.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.demo.entity.ProductEntity;

public class ProductModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String prodId;
	private String value;
	private String updatedOn;
		
	
	public String getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static ProductModel getModel(ProductEntity prodEntity) {
		ProductModel prodModel = new ProductModel();
		prodModel.setProdId(prodEntity.getProdId());
		prodModel.setValue(prodEntity.getValue());
		prodModel.setUpdatedOn(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return prodModel;
	}
	
}
