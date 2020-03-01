package com.example.demo.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;

@Configuration
public class XmlDomConfiguration {

	@Bean
	public DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder=null;
		try {
			icBuilder = icFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return  icBuilder;
	}
	
	@Bean
	public Transformer getTransformer() {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer=null;
		try {
			transformer= tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		return transformer;
	}
}
