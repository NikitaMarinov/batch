package com.spring.batch.tasklets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
public class MoveFinishedFilesTasklet implements Tasklet {
    @Value("${file.path.initial}")
    private String initialFilePath;
    @Value("${file.path.final}")
    private String finalFilePath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        File before = new File(initialFilePath);
        File after = new File(finalFilePath);

        try {
            Files.move(before.toPath(), after.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error while moving the file!");
            e.printStackTrace();
        }

        return RepeatStatus.FINISHED;
    }

}
