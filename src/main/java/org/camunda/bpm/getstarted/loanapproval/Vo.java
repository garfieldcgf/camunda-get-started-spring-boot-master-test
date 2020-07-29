package org.camunda.bpm.getstarted.loanapproval;

import lombok.Data;

/**
 * @Author: garfield
 * @Date: 2020/7/20 16:17
 */
@Data
public class Vo {

    private String processDefinitionId;

    private String processInstanceId;

    private String businessKey;

    private String activityId;
    private String activityId_2;

    private String activityInstanceId;
    private String activityInstanceId_2;

    private String ancestorActivityInstanceId;

    private String taskId;

    private String parentTaskId;

    private String assignee;

    private String function;

    private String sourceProcessDefinitionId;

    private String targetProcessDefinitionId;
    private String sourceActivityId;
    private String targetActivityId;







}
