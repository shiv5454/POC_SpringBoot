package com.example.demo.batch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class MoveProcessedFilesTasklet implements Tasklet{

	private String successPath;

	public String getSuccessPath() {
		return successPath;
	}

	public void setSuccessPath(String successPath) {
		this.successPath = successPath;
	}

	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String file = (String)chunkContext.getStepContext().getJobParameters().get("path");
		System.out.println(file);

			
				
				Files.move(Paths.get(file), Paths.get(successPath+File.separator+Paths.get(file).getFileName()),
			            StandardCopyOption.REPLACE_EXISTING);
			

		return RepeatStatus.FINISHED;
	}

}
