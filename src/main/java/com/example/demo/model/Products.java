package com.example.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Products")
public class Products implements Serializable {

    private static final long serialVersionUID = 22L;
   
    private List<Product> Product = new ArrayList<>();
    
	public List<Product> getProducts() {
		return Product;
	}
	@XmlElement
	public void setProducts(List<Product> products) {
		Product = products;
	}
    
    
}
