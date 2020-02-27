package com.example.demo.batch;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
@Component
public class JobListner implements JobExecutionListener{
 
	private static final Logger log = LoggerFactory.getLogger(JobListner.class);
    private Date startTime, stopTime;
    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = new Date();
        log.info("Job starts at :"+startTime);
    }
    @Override
    public void afterJob(JobExecution jobExecution) {
        stopTime = new Date();
        log.info("Job stops at :"+stopTime);
        log.info("Total time take in millis :"+getTimeInMillis(startTime , stopTime));
 
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
        	log.info(jobExecution.getId()+ " Job completed successfully");
        }else if(jobExecution.getStatus() == BatchStatus.FAILED){
        	log.info(jobExecution.getId()+ "Job failed with following exceptions ");
            List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
            for(Throwable th : exceptionList){
            	log.error("exception :" +th.getLocalizedMessage());
            }
        }
    }
 
    private long getTimeInMillis(Date start, Date stop){
        return stop.getTime() - start.getTime();
    }
}
