package id.cs.ui.advprog.inthecost.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Profile("test")
public class SyncTaskExecutorConfig {

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        return Runnable::run;  // menjalankan runnable langsung di thread yang sama
    }
}
