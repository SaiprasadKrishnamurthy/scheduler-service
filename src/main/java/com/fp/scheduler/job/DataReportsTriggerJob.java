package com.fp.scheduler.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sonus21.rqueue.core.RqueueMessageTemplate;
import com.github.sonus21.rqueue.producer.RqueueMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Job that models whenever a report needs to be triggered.
 *
 * @author Sai.
 */
@Slf4j
@Component
public class DataReportsTriggerJob extends QuartzJobBean {

    @Override
    protected void executeInternal(final JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        try {
            log.info(" Reports Triggered JobType: {}, JobSourceId: {}  ", jobDataMap.get("jobType"), jobDataMap.get("jobSourceId"));
            ApplicationContext applicationContext = (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContext");
            RedisConnectionFactory redisConnectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
            ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
            Environment environment = applicationContext.getBean(Environment.class);
            RqueueMessageTemplate rqueueMessageTemplate = new RqueueMessageTemplate(redisConnectionFactory);
            RqueueMessageSender rqueueMessageSender = new RqueueMessageSender(rqueueMessageTemplate);
            rqueueMessageSender.put(environment.getProperty("reports.generation.trigger.queue"), objectMapper.writeValueAsString(jobDataMap));
        } catch (Exception ex) {
            log.info(jobDataMap.get("jobType").toString());
            log.info(jobDataMap.get("jobSourceId").toString());
            log.error("Error while sending a trigger event to Redis Queue: ", ex);
        }
    }
}
