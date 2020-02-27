package com.example.demo.common;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceComputeScheduler {

	@Autowired
	JobLauncher jobLauncher;
	@Autowired @Qualifier("cronJob")
	Job job;
	
	private static final Logger log = LoggerFactory.getLogger(PriceComputeScheduler.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//@Scheduled(fixedRate = 2000000)
	@Scheduled(cron = "${batch.job.cron.expression}")
	public void computePrice() {
		log.info("Starting Time is {}", dateFormat.format(new Date()));
		Map<String, JobParameter> params = new HashMap<>();
		params.put("timestamp", new JobParameter(System.currentTimeMillis()));
		System.out.println("**************     Starting CRON JOB		***************");
		JobParameters jobParameters = new JobParameters(params);
		try {
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);
			log.info("Job Execution: " + jobExecution.getStatus());
		} catch (JobExecutionAlreadyRunningException e) {
			log.error("JobExecutionAlreadyRunningException:"+e);
		} catch (JobRestartException e) {
			log.error("JobRestartException:"+e);
		} catch (JobInstanceAlreadyCompleteException e) {
			log.error("JobInstanceAlreadyCompleteException:"+e);
		} catch (JobParametersInvalidException e) {
			log.error("JobParametersInvalidException:"+e);
		}
		
	}
}
