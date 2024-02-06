package com.spring.batch.listeners;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.*;
import org.springframework.batch.item.file.FlatFileItemWriter;

import com.spring.batch.domain.HLPI;
@Slf4j
public class StepHLPIListener implements StepExecutionListener {

    private final FlatFileItemWriter<HLPI> invalidDataItemWriter;

    public StepHLPIListener(FlatFileItemWriter<HLPI> invalidDataItemWriter) {
        this.invalidDataItemWriter = invalidDataItemWriter;
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        invalidDataItemWriter.close();
        log.info("The invalid item writer is closed!");
        return ExitStatus.COMPLETED;
    }
}
