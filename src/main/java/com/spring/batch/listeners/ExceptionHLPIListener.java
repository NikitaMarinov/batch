package com.spring.batch.listeners;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.validator.ValidationException;

import com.spring.batch.domain.HLPI;

public class ExceptionHLPIListener implements SkipListener<HLPI, HLPI> {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHLPIListener.class);

    private final FlatFileItemWriter<HLPI> invalidDataItemWriter;

    public ExceptionHLPIListener(FlatFileItemWriter<HLPI> invalidDataItemWriter) {
        this.invalidDataItemWriter = invalidDataItemWriter;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        System.out.println("Skipped item during reading: " + t.getMessage());

    }

    @Override
    public void onSkipInProcess(HLPI item, Throwable t) {
        logger.error("Skipped item during processing: " + item.toString() + ", Error: " + t.getMessage());
            writeSkippedItem(item);

    }

    @Override
    public void onSkipInWrite(HLPI item, Throwable t) {
        System.out.println("Skipped item during writing: " + item.toString() + ", Error: " + t.getMessage());
    }

    private void writeSkippedItem(HLPI item) {
        try {
            invalidDataItemWriter.open(new ExecutionContext());
            invalidDataItemWriter.write(new Chunk<>(Collections.singletonList(item)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}