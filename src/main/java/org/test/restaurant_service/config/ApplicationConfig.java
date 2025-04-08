package org.test.restaurant_service.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AmazonS3 amazonS3;


    @Bean
    public TransferManager transferManager() {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);   // Больше потоков постоянно
        executor.setMaxPoolSize(50);   // Под нагрузкой до 100 потоков
        executor.setQueueCapacity(200); // Очередь на 200 сообщений перед отказами
        executor.setKeepAliveSeconds(60); // Потоки > corePoolSize живут 60 сек после пика
        executor.setThreadNamePrefix("MsgSender-"); // Удобнее в логах
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
