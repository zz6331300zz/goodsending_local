package com.goodsending.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String accessSecret;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean
  public AmazonS3 s3Client() {
    // AWS 자격 증명 생성
    AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);

    // AmazonS3ClientBuilder 를 사용해 Amazon S3 셍상
    return AmazonS3ClientBuilder.standard() // 표준 빌더 인스턴스 생성
        .withCredentials(new AWSStaticCredentialsProvider(credentials)) // 자격 증명 설정
        .withRegion(region).build(); // region 설정
  }

}
