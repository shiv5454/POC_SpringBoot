package com.example.demo.batch;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.demo.entity.ProductEntity;
import com.example.demo.model.PriceModel;
import com.example.demo.model.Product;
import com.example.demo.model.ProductModel;
import com.example.demo.model.Products;
import com.example.demo.repository.ProductRepository;

public class ActivePriceWriter implements ItemWriter<PriceModel>{

	@Value("${euro.conversion.rate}")
	private double euroConversionRate;
	@Value("${kafka.topic.name}")
	private String topic;
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemp;
	@Autowired
	private ProductRepository productRepo;
	@Value("${xml.storage.path}")
	private String xmlStoragePath;
	private Integer productSequence=100;
	
	private static final Logger log = LoggerFactory.getLogger(ActivePriceWriter.class);
	@Override
	public void write(List<? extends PriceModel> items) throws Exception {
		System.out.println("**********    XML Conversion Start  **************");
		System.out.println(items.toString());
		Map<String, String> productDetails = getPriceIndicator(items);
		if(!productDetails.isEmpty()) {
			storeProductDetails(productDetails);
		}
		System.out.println("**********    XML Conversion Completed  **************");
		
		

	}
	
	private Map<String,String> getPriceIndicator(List<? extends PriceModel> items) {
		Map<String,String> productMap=new HashMap<>();
		
		if(!items.isEmpty()) {
			
			Map<String, List<PriceModel>> productMapById = items.stream().collect(Collectors.groupingBy(PriceModel::getProductId));

			productMapById.forEach((k,v)->{
				log.info("Filtering for Product Id : {}" , k);		
				Optional<Double> convertedValueForCp=getConvertedValue(v,"Cost Price");
				
				if(convertedValueForCp.isPresent()) {
					productMap.put(k, String.valueOf(convertedValueForCp.get()));
				}else {
					Optional<Double> convertedValueForMsrp = getConvertedValue(v,"MSRP");
					if(convertedValueForMsrp.isPresent()) {
						productMap.put(k, String.valueOf(convertedValueForMsrp.get()));
					}else log.info("No Product matched for Product ID : {}", k);
				}
			});
		}
		log.info("Product Details : "+productMap);
		return productMap;
	}
	
	private Optional<Double> getConvertedValue(List<PriceModel> v, String priceType) {
		log.info("Filtering for Price type : {}",priceType);
		Predicate<PriceModel> isValidToCurrentDate= p->p.getValidTo().compareTo(new Date())>=0;
		Predicate<PriceModel> isValidFromCurrentDate= p->p.getValidFrom().compareTo(new Date())<=0;

		return v.stream().filter(isValidFromCurrentDate.and(isValidToCurrentDate)).
							filter(p->p.getPriceType().equalsIgnoreCase(priceType)).
							findAny().map(p->p.getValue()*euroConversionRate);
	}
	
	private void storeProductDetails(Map<String, String> productDetails) {
		productDetails.forEach((k,v)->{
			log.info("Persisting Product Detail for : {}",k);;
			ProductEntity prodEntity= new ProductEntity();
			prodEntity.setId(productSequence);
			prodEntity.setProdId(k);
			prodEntity.setValue(v);
			
			productRepo.save(prodEntity);
			productSequence++;
			ProductModel model = ProductModel.getModel(prodEntity);
			generateXmlForProducts(model);
		});
		
	}
	
	private void generateXmlForProducts(final ProductModel model){

		com.example.demo.model.Value val = new com.example.demo.model.Value("ListPrice",model.getValue());
		com.example.demo.model.Value val1 = new com.example.demo.model.Value("LastUpdated",model.getUpdatedOn());
		List<com.example.demo.model.Value> valueList = new ArrayList<>();
		valueList.add(val);
		valueList.add(val1);
		
		Product prod = new Product(model.getProdId(), valueList);
			
		List<Product> productList= new ArrayList<>();
		productList.add(prod);
		Products prodd = new Products();
		prodd.setProducts(productList);

		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext jaxbContext = JAXBContext.newInstance(Products.class);
	        Marshaller marshaller = jaxbContext.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        //marshaller.marshal(prodd, new File(xmlStoragePath+File.separator+model.getProdId()+"_product.xml"));
	        marshaller.marshal(prodd, System.out);
	        marshaller.marshal(prodd, stringWriter);
	        
	        kafkaTemp.send(topic,stringWriter);
		}catch (Exception e) {
			 log.error("Error:"+e);
		}
	}
	

}
