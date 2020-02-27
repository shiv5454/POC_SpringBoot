package com.example.demo.config;

import java.nio.file.Paths;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.example.demo.batch.ActivePriceProcessor;
import com.example.demo.batch.ActivePriceWriter;
import com.example.demo.batch.MoveErrorFilesTasklet;
import com.example.demo.batch.MoveProcessedFilesTasklet;
import com.example.demo.batch.PriceDbRowMapper;
import com.example.demo.batch.PriceFileRowMapper;
import com.example.demo.batch.PriceItemProcessor;
import com.example.demo.batch.PriceItemWriter;
import com.example.demo.entity.PriceEntity;
import com.example.demo.model.PriceModel;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public DataSource dataSource;

	@Autowired
	JobExecutionListener listener;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${files.error.path}")
	private String errorPath;
	@Value("${files.success.path}")
	private String sucessPath;
	@Value("${batch.chunk.config}")
	private int batchConfig;

	@Bean 
	public PriceItemProcessor priceProcessor() {
		return new PriceItemProcessor();
	}
	@Bean 
	public PriceItemWriter priceWriter() {
		return new PriceItemWriter();
	}

	@Bean 
	public ActivePriceProcessor dbProcessor() {
		return new ActivePriceProcessor();
	}

	@Bean 
	public ActivePriceWriter dbWriter() {
		return new ActivePriceWriter();
	}

	@Bean
	@StepScope
	public JdbcCursorItemReader<PriceEntity> dbReader(){
		JdbcCursorItemReader<PriceEntity> reader = new JdbcCursorItemReader<PriceEntity>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT * FROM Price p WHERE p.Indicator = 1");
		reader.setRowMapper(new PriceDbRowMapper());
		return reader;
	}

	@Bean
	@StepScope
	public Resource inputFileResource(@Value("#{jobParameters[path]}") final String filename) {
		System.out.println("********************   Resource  Path  *****************************");
		System.out.println(filename);
		return resourceLoader.getResource("file:"+filename);
	}

	@Bean
	@StepScope
	public FlatFileItemReader<PriceModel> reader() {
		System.out.println("*******************  File Reader  ******************************");
		FlatFileItemReader<PriceModel> flatFileItemReader = new FlatFileItemReader<>();

		flatFileItemReader.setResource(inputFileResource(null));
		flatFileItemReader.setName("priceItemReader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(new DefaultLineMapper<PriceModel>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] { "ProductID", "Price Type", "Active Indicator", "Local Currency Code",
						"Price Value", "Valid From", "Valid To" });
			}});
			setFieldSetMapper(new PriceFileRowMapper());
		}});


		return flatFileItemReader;
	}

	@Bean
	public PriceItemProcessor processor() {
		return new PriceItemProcessor();
	}

	@Bean("fileReadJob")
	public Job job() {
		return jobBuilderFactory.get("importCSVFile").incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(readCsvFile())
				.on("FAILED")
				.to(moveErrorFiles())
				.from(readCsvFile())
				.on("*")
				.to(moveFiles()).next(checkActivePrices())
				.end().build();
	}

	@Bean("cronJob")
	public Job cronJob() {
		return jobBuilderFactory.get("cronJob").incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(checkActivePrices()).end().build();
	}


	public Step checkActivePrices() {
		return stepBuilderFactory.get("checkActivePrices").<PriceEntity,PriceModel>chunk(Integer.MAX_VALUE).reader(dbReader())
				.processor(dbProcessor()).writer(dbWriter()).build();
	}

	public Step readCsvFile() {
		return stepBuilderFactory.get("readCsvFile").<PriceModel, PriceEntity>chunk(batchConfig).reader(reader())
				.processor(priceProcessor()).writer(priceWriter()).build();
	}

	@Bean
	protected Step moveFiles() {
		MoveProcessedFilesTasklet moveFilesTasklet = new MoveProcessedFilesTasklet();

		moveFilesTasklet.setSuccessPath(sucessPath);

		return stepBuilderFactory.get("moveSuccessFiles").tasklet(moveFilesTasklet).build();
	}

	@Bean
	protected Step moveErrorFiles() {
		MoveErrorFilesTasklet moveFilesTasklet = new MoveErrorFilesTasklet();

		moveFilesTasklet.setErrorPath(errorPath);

		return stepBuilderFactory.get("moveErrorFiles").tasklet(moveFilesTasklet).build();
	}



}
