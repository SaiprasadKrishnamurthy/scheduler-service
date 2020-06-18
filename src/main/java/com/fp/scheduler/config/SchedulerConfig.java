package com.fp.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 * <p>Scheduler configuration.</p>
 *
 * @author Sai.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Value("${org.quartz.jobStore.class}")
    private String quartzJobStoreClass;

    @Value("${org.quartz.jobStore.mongoUri}")
    private String quartzJobStoreMongoUri;

    @Value("${org.quartz.jobStore.dbName}")
    private String quartzJobStoreDbName;

    @Value("${org.quartz.jobStore.collectionPrefix}")
    private String quartzJobStoreCollectionPrefix;

    @Value("${org.quartz.threadPool.threadCount}")
    private String quartzThreadPoolThreadCount;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(final ApplicationContext applicationContext) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.jobStore.class", quartzJobStoreClass);
        properties.setProperty("org.quartz.jobStore.mongoUri", quartzJobStoreMongoUri);
        properties.setProperty("org.quartz.jobStore.dbName", quartzJobStoreDbName);
        properties.setProperty("org.quartz.jobStore.collectionPrefix", quartzJobStoreCollectionPrefix);
        properties.setProperty("org.quartz.threadPool.threadCount", quartzThreadPoolThreadCount);
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.afterPropertiesSet();
        return schedulerFactoryBean;
    }
}

