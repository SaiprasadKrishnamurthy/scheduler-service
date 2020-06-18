package com.fp.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduledJobInfo<T extends Trigger> {
    private JobDetail jobDetail;
    private List<T> trigger;
}
