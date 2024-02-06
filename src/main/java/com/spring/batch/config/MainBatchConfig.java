package com.spring.batch.config;

import com.spring.batch.listeners.StepHLPIListener;
import com.spring.batch.tasklets.MoveFinishedFilesTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import com.spring.batch.domain.HLPI;
import com.spring.batch.listeners.ExceptionHLPIListener;
import com.spring.batch.mappers.HJPIMapper;
import com.spring.batch.repository.HLPIRepository;
import com.spring.batch.validators.HLPIValidator;

@Configuration
@Import(SeparateDataBatchConfig.class)
public class MainBatchConfig {
    private final HLPIRepository hlpiRepository;

    @Autowired
    public MainBatchConfig(HLPIRepository hlpiRepository ) {
        this.hlpiRepository = hlpiRepository;
    }

    // READERS

    @Bean
    public FlatFileItemReader<HLPI> flatFileItemReader() {
        DefaultLineMapper<HLPI> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(new HJPIMapper());
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));
        return new FlatFileItemReaderBuilder<HLPI>()
                .name("HLPIReader")
                .resource(new ClassPathResource("csv-data.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .delimited()
                .names(new String[]{"hlpi_name", "series_ref", "quarter", "hlpi", "nzhec", "nzhec_name", "nzhec_short", "level", "index", "change.q", "change.a"})
                .targetType(HLPI.class)
                .build();
    }

    // PROCESSOR && VALIDATOR FOR IT

    @Bean
    public HLPIValidator validator() {
        return new HLPIValidator();
    }

    @Bean
    public ValidatingItemProcessor<HLPI> itemProcessor() {
        ValidatingItemProcessor<HLPI> processor = new ValidatingItemProcessor<>();
        processor.setValidator(validator());
        return processor;
    }

    // WRITERS

    @Bean
    public RepositoryItemWriter<HLPI> repositoryItemWriter(JpaRepository<HLPI, Long> repository) {
        return new RepositoryItemWriterBuilder<HLPI>()
                .repository(repository)
                .methodName("save")
                .build();
    }


    @Bean
    public FlatFileItemWriter<HLPI> invalidDataItemWriter() {
        return new FlatFileItemWriterBuilder<HLPI>()
                .name("invalidDataItemWriter")
                .resource(new FileSystemResource("src/main/resources/static/invalid-data.csv"))
                .delimited()
                .names(new String[]{"HLPIName", "seriesReference", "quarter", "HLPI", "NZHEC", "NZHECName", "NZHECShort", "level", "index", "quarterlyChange", "annualChange"})
                .headerCallback(writer -> writer.write("HLPIName,seriesReference,quarter,HLPI,NZHEC,NZHECName,NZHECShort,level,index,quarterlyChange,annualChange"))
                .shouldDeleteIfExists(true)
                .build();
    }

    // STEP AND EXCEPTION TASKLET & LISTENER FOR IT

    @Bean
    public MoveFinishedFilesTasklet moveFinishedFilesTasklet(){
        return new MoveFinishedFilesTasklet();
    }

    @Bean
    public ExceptionHLPIListener exceptionSkipListener() {
        return new ExceptionHLPIListener(invalidDataItemWriter());
    }

    @Bean
    public StepHLPIListener stepExecutionListener(){
        return new StepHLPIListener(invalidDataItemWriter());
    }

    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        return new StepBuilder("step", jobRepository)
                .<HLPI, HLPI>chunk(3000, transactionManager)
                .allowStartIfComplete(true)
                .listener(stepExecutionListener())
                .reader(flatFileItemReader())
                .processor(itemProcessor())
                .faultTolerant()
                .skipPolicy((Throwable t, long skipCount) -> t instanceof ValidationException)
                .skip(ValidationException.class)
                .listener(exceptionSkipListener())
                .writer(repositoryItemWriter(hlpiRepository))
                .build();
    }
    @Bean
    public Step secondStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        return new StepBuilder("step", jobRepository)
                .tasklet(moveFinishedFilesTasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    //JOB

    @Bean
    public Job importHLPIJob(JobRepository jobRepository,
                             @Qualifier("firstStep") Step firstStep,
                             @Qualifier("secondStep") Step secondStep) {
        return new JobBuilder("importHLPI", jobRepository)
                .start(firstStep)
                .next(secondStep)
                .build();
    }


}