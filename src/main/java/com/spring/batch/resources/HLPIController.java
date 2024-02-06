package com.spring.batch.resources;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hlpi")
public class HLPIController {
    private final Job importHLPIJob;
    private final Job importSeparateData;
    private final JobLauncher jobLauncher;

    @Autowired
    public HLPIController(@Qualifier("importHLPIJob") Job importHLPIJob,@Qualifier("importSeparateData") Job importSeparateData, JobLauncher jobLauncher) {
        this.importHLPIJob = importHLPIJob;
        this.importSeparateData = importSeparateData;
        this.jobLauncher = jobLauncher;
    }

    @PostMapping("loadData")
    public ResponseEntity<String> loadHLPIData() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(importHLPIJob,new JobParameters());

        return ResponseEntity.ok("Success!");
    }

    @PostMapping("loadSeparateData")
    public ResponseEntity<String> loadHLPISeparateData() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(importSeparateData,new JobParameters());

        return ResponseEntity.ok("Success!");
    }
}
