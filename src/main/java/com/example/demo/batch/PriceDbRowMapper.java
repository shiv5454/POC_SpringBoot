package com.example.demo.batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.entity.PriceEntity;

public class PriceDbRowMapper implements RowMapper<PriceEntity>{

	@Override
	public PriceEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		PriceEntity entity = new PriceEntity();
		entity.setProductId(rs.getString("PRODUCT_ID"));
		entity.setIndicator(rs.getInt("INDICATOR"));
		entity.setCurrencyCode(rs.getString("CURRENCY_CODE"));
		entity.setPriceType(rs.getString("PRICE_TYPE"));
		entity.setValidFrom(rs.getDate("VALID_FROM"));
		entity.setValidTo(rs.getDate("VALID_TO"));
		entity.setValue(rs.getDouble("VALUE"));
		 	
		return entity;
	}

}
