package com.example.demo.batch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class MoveErrorFilesTasklet implements Tasklet{

	private String errorPath;

	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String file = (String)chunkContext.getStepContext().getJobParameters().get("path");
		Files.move(Paths.get(file), Paths.get(errorPath+File.separator+Paths.get(file).getFileName()),
	            StandardCopyOption.REPLACE_EXISTING);

		return RepeatStatus.FINISHED;

	}

}
