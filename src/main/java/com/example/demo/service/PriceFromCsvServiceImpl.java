package com.example.demo.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PriceFromCsvServiceImpl implements PriceFromCsvService{

	private static final Logger log = LoggerFactory.getLogger(PriceFromCsvServiceImpl.class);


	@Autowired
	JobLauncher jobLauncher;
	@Autowired @Qualifier("fileReadJob")
	Job job;
	@Autowired
	WatchService watchService;
	@Value("${source.path}")
	private String sourcePath;


	//WatchService for continuous watching directory for csv file upload
	@Override
	public void startWatcherService(){

		Path path = Paths.get(sourcePath);
		log.info("Watching path: " + path);

		WatchKey watchKey =null;

		try {
			while ((watchKey = watchService.take()) != null) {				
				watchKey.pollEvents().stream().filter(event->event.kind().name()=="ENTRY_CREATE").forEach(temp->
				createJobLauncher(path, temp));
				watchKey.reset();
			}
		} catch (InterruptedException e) {
			log.error("Error : " + e);
		}

	}

	private void createJobLauncher(Path path, WatchEvent<?> watchEvent) {
		Map<String, JobParameter> params = new HashMap<>();
		params.put("path", new JobParameter(path.resolve((Path) watchEvent.context()).toString()));
		params.put("timestamp", new JobParameter(System.currentTimeMillis()));
		System.out.println("**************     Starting File Read JOB		***************");
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
