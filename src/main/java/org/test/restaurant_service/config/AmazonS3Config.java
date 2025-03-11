package org.test.restaurant_service.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.test.restaurant_service.util.KeyUtil;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Getter
public class AmazonS3Config {

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(KeyUtil.getAccessKey(), KeyUtil.getSecretAccessKey());

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration("https://s3.timeweb.cloud", "ru-1"))
                .withPathStyleAccessEnabled(true)
                .build();
    }

}