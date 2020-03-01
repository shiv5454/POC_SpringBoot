package com.example.demo.batch;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.example.demo.entity.ProductEntity;
import com.example.demo.model.PriceModel;
import com.example.demo.model.ProductModel;
import com.example.demo.repository.ProductRepository;

public class ActivePriceWriter implements ItemWriter<PriceModel>{

	@Value("${euro.conversion.rate}")
	private double euroConversionRate;
	@Value("${kafka.topic.name}")
	private String topic;
	@Autowired
	private DocumentBuilder docBuilder;
	@Autowired
	Transformer transformer;
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemp;
	@Autowired
	private ProductRepository productRepo;
	@Value("${xml.storage.path}")
	private String xmlStoragePath;
	private Integer productSequence=100;
	DecimalFormat df = new DecimalFormat(".##");
	
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
							findAny().map(p->Double.parseDouble(df.format(p.getValue()*euroConversionRate)));
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

			try {
				generateXmlThroughDomParser(model);
			} catch (TransformerFactoryConfigurationError e) {
				log.error("Error:"+e);
			} catch (TransformerException e) {
				log.error("Error:"+e);
			}
		});
		
	}
	
	private void generateXmlThroughDomParser(ProductModel model) throws TransformerFactoryConfigurationError, TransformerException {
		
		Document doc=docBuilder.newDocument();

		Element root = doc.createElement("Products");
        doc.appendChild(root);
        
        Element product = doc.createElement("Product");
        root.appendChild(product);

        product.setAttribute("id",model.getProdId());
        Element values = doc.createElement("Values");
        values.appendChild(getValueElements(doc,model.getValue(),"ListPrice"));
        values.appendChild(getValueElements(doc,model.getUpdatedOn(),"LastUpdated"));
        product.appendChild(values);
        DOMSource source = new DOMSource(doc);
		StringWriter stringWriter = new StringWriter();
        transformer.transform(source, new StreamResult(stringWriter));
        kafkaTemp.send(topic,stringWriter.toString());
		
	}
	
	private Node getValueElements(Document doc, String eleValue, String attName) {
        Element value = doc.createElement("Value");
        value.setAttribute("attributeID", attName);
        value.appendChild(doc.createTextNode(eleValue));
        return value;
    }

}
