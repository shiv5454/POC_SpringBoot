package com.example.demo.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Product  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ID;
	
    private List<Value> Values;
	
	public Product(String iD, List<Value> value) {
		super();
		ID = iD;
		Values = value;
	}
	public Product() {
		super();
	}
	@XmlAttribute
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	@XmlElementWrapper(name="Values")
	public List<Value> getValue() {
		return Values;
	}
	public void setValue(List<Value> value) {
		Values = value;
	}
	@Override
	public String toString() {
		return "Product [ID=" + ID + ", Value=" + Values + "]";
	}
	
	
}
