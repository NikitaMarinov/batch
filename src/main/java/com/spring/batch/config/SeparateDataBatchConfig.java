package com.spring.batch.config;

import com.spring.batch.domain.HLPI;
import com.spring.batch.domain.HLPIindexes;
import com.spring.batch.domain.HLPIinfo;
import com.spring.batch.mappers.HJPIMapper;
import com.spring.batch.mappers.HLPIRowMapper;
import com.spring.batch.processors.HlpihlpIindexisItemProcessor;
import com.spring.batch.processors.HlpihlpIinfoItemProcessor;
import com.spring.batch.repository.HLPIindexisRepository;
import com.spring.batch.repository.HLPIinfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class SeparateDataBatchConfig {
    private final DataSource dataSource;
    private final HLPIindexisRepository hlpIindexisRepository;
    private final HLPIinfoRepository hlpIinfoRepository;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        return executor;
    }

    @Autowired
    public SeparateDataBatchConfig(DataSource dataSource, HLPIindexisRepository hlpIindexis, HLPIinfoRepository hlpIinfo) {
        this.dataSource = dataSource;
        this.hlpIindexisRepository = hlpIindexis;
        this.hlpIinfoRepository = hlpIinfo;
    }

    // READERS

    @Bean
    public FlatFileItemReader<HLPI> flatFilesSeparateItemReader() {
        DefaultLineMapper<HLPI> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(new HJPIMapper());
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));
        return new FlatFileItemReaderBuilder<HLPI>()
                .name("HLPISeparateReader")
                .resource(new ClassPathResource("csv-data.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .delimited()
                .names(new String[]{"hlpi_name", "series_ref", "quarter", "hlpi", "nzhec", "nzhec_name", "nzhec_short", "level", "index", "change.q", "change.a"})
                .targetType(HLPI.class)
                .build();
    }

    @Bean
    public ItemProcessor<HLPI, HLPIinfo> hlpihlpIinfoItemProcessor() {
        return new HlpihlpIinfoItemProcessor();
    }

    @Bean
    public ItemProcessor<HLPI, HLPIindexes> hlpihlpIindexisItemProcessor() {
        return new HlpihlpIindexisItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<HLPIinfo> hlpIinfoRepositoryItemWriter(JpaRepository<HLPIinfo, Long> repository) {
        return new RepositoryItemWriterBuilder<HLPIinfo>()
                .repository(repository)
                .methodName("save")
                .build();
    }


    @Bean
    public RepositoryItemWriter<HLPIindexes> hlpIindexisRepositoryItemWriter(JpaRepository<HLPIindexes, Long> repository) {
        return new RepositoryItemWriterBuilder<HLPIindexes>()
                .repository(repository)
                .methodName("save")
                .build();
    }




    @Bean
    public Step step1( PlatformTransactionManager transactionManager ,JobRepository jobRepository) {
        return new StepBuilder("step1",jobRepository)
                .<HLPI, HLPIinfo>chunk(3000,transactionManager)
                .reader(flatFilesSeparateItemReader())
                .processor(hlpihlpIinfoItemProcessor())
                .writer(hlpIinfoRepositoryItemWriter(hlpIinfoRepository))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step step2( PlatformTransactionManager transactionManager,JobRepository jobRepository){
        return new StepBuilder("step2", jobRepository)
                .<HLPI, HLPIindexes>chunk(3000,transactionManager)
                .reader(flatFilesSeparateItemReader())
                .processor(hlpihlpIindexisItemProcessor())
                .writer(hlpIindexisRepositoryItemWriter(hlpIindexisRepository))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step step3( PlatformTransactionManager transactionManager,JobRepository jobRepository){
       log.error(dataSource.toString());
        return new StepBuilder("step3", jobRepository)
                .<HLPI, HLPI>chunk(3000,transactionManager)
                .reader(unifiedDataReader(dataSource))
                .writer(unifiedDataWriter())
                .build();
    }



    @Bean
    public Job importSeparateData(JobRepository jobRepository, Step step1, Step step2,@Qualifier("step3") Step step3) {
        return new JobBuilder("importSeparateHLPI", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }




    @Bean
    public ItemReader<HLPI> unifiedDataReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<HLPI>()
                .name("unifiedDataReader")
                .dataSource(dataSource)
                .sql("SELECT hlpi_name, series_reference, quarter, hlpi, nzhec, nzhec_name, nzhec_short, level, index, quarterly_change, annual_change FROM hlpi_indexes t1 JOIN hlpi_info t2 ON t1.id = t2.id")
                .rowMapper(new HLPIRowMapper())
                .build();
    }

    @Bean
    public FlatFileItemWriter<HLPI> unifiedDataWriter() {
        return new FlatFileItemWriterBuilder<HLPI>()
                .name("invalidDataItemWriter")
                .resource(new FileSystemResource("src/main/resources/output.csv"))
                .delimited()
                .names(new String[]{"HLPIName", "seriesReference", "quarter", "HLPI", "NZHEC", "NZHECName", "NZHECShort", "level", "index", "quarterlyChange", "annualChange"})
                .headerCallback(writer -> writer.write("HLPIName,seriesReference,quarter,HLPI,NZHEC,NZHECName,NZHECShort,level,index,quarterlyChange,annualChange"))
                .build();
    }



}
