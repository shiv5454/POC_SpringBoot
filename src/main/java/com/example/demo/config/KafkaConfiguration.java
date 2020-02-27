package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {
	
	@Value("${BOOTSTRAP_SERVERS_CONFIG}")
	private String serverConfig;
	
	@Value("${CLIENT_ID_CONFIG}")
	private String clientIdConfig;
	
	@Bean
	public ProducerFactory<String, Object> produceFactory(){
		Map<String, Object> configs=new HashMap<>();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverConfig);
		configs.put(ProducerConfig.CLIENT_ID_CONFIG, clientIdConfig);
		configs.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configs);
	}
	
	@Bean
	public KafkaTemplate<String, Object> getTemplate(){
		return new KafkaTemplate<>(produceFactory());
	}
}
