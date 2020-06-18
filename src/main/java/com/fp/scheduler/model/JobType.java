package com.fp.scheduler.model;

import com.fp.scheduler.job.DataReportsTriggerJob;
import lombok.Getter;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Job Type.
 *
 * @author Sai.
 */
@Getter
public enum JobType {
    GenerateReport("Generate a Report", DataReportsTriggerJob.class), SendSms("Send SMS", QuartzJobBean.class), SendEmail("Send Email", QuartzJobBean.class);

    private final String description;
    private final Class<? extends QuartzJobBean> quartzJobBeanClass;

    JobType(final String description, final Class<? extends QuartzJobBean> quartzJobBeanClass) {
        this.description = description;
        this.quartzJobBeanClass = quartzJobBeanClass;
    }
}
