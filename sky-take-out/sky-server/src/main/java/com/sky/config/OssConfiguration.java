package com.sky.config;

import com.sky.properties.TencentOssProperties;
import com.sky.utils.TencentOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    @ConditionalOnMissingBean
    TencentOssUtil tencentOssUtil(TencentOssProperties tencentOssProperties) {
        log.info("开始创建TencentOssUtil对象，配置信息：{}", tencentOssProperties);
        return new TencentOssUtil(tencentOssProperties.getSecretId(), tencentOssProperties.getSecretKey(), tencentOssProperties.getRegion(), tencentOssProperties.getBucketName());
    }

}
