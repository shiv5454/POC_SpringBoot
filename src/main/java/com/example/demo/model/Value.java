package com.example.demo.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Value implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	private String AttributeID;
	@XmlValue()
	private String value;
	
	
	
	public Value(String attributeID, String value) {
		super();
		AttributeID = attributeID;
		this.value = value;
	}
	public Value() {
		super();
	}
	@Override
	public String toString() {
		return "Value [AttributeID=" + AttributeID + ", Value=" + value + "]";
	}
	
	
	
}
