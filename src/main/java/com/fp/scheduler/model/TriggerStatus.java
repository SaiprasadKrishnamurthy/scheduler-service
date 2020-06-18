package com.fp.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriggerStatus {

    private String jobtype;
    private String jobId;
    private String triggerId;
    private String TriggerStatus;
    private String NextFireTime;
}
