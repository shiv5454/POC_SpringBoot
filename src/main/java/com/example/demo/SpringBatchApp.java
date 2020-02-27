package com.example.demo;

import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.service.PriceFromCsvService;
import com.example.demo.service.PriceFromCsvServiceImpl;

@SpringBootApplication
@EnableScheduling
public class SpringBatchApp implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(SpringBatchApp.class);
	
	public static void main(String[] args){
		ConfigurableApplicationContext context=null;
		try {
		 context= SpringApplication.run(SpringBatchApp.class, args);
		 
		 try{
			 WatchService bean = (WatchService)context.getBean("watchService");
			  if(null!=bean)
			  {
				  PriceFromCsvService service = context.getBean(PriceFromCsvServiceImpl.class);
					 service.startWatcherService(); 
			  }
	        }catch(NoSuchBeanDefinitionException e){
	            System.out.println("No folder to watch...Shutting Down");
	            context.close();
	        }
		}finally {
			if(null!=context)
			context.close();
		}
	}

	@Override
	public void run(String... args) throws Exception {	
		log.info("Initialization done...");
	}

}
