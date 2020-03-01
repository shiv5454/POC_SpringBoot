package com.example.demo.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Price")
public class PriceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String productId;
	private String priceType;
	private Integer indicator;
	private String currencyCode;
	private BigDecimal value;
	@Temporal(TemporalType.DATE)
	private Date validFrom;
	@Temporal(TemporalType.DATE)
	private Date validTo;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public Integer getIndicator() {
		return indicator;
	}
	public void setIndicator(Integer indicator) {
		this.indicator = indicator;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
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
	@Override
	public String toString() {
		return "PriceEntity [id=" + id + ", productId=" + productId + ", priceType=" + priceType + ", indicator="
				+ indicator + ", currencyCode=" + currencyCode + ", value=" + value + ", validFrom=" + validFrom
				+ ", validTo=" + validTo + "]";
	}
	
	
}
