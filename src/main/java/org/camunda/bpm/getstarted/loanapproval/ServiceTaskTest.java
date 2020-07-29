package org.camunda.bpm.getstarted.loanapproval;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;

import java.util.List;

/**
 * @Author: garfield
 * @Date: 2020/7/21 10:27
 */
public class ServiceTaskTest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("=====================================");
        System.out.println("service task run");
        String processInstanceId = execution.getProcessInstanceId();
        System.out.println("processInstanceId:" + processInstanceId);


        HistoryService historyService = execution.getProcessEngine().getHistoryService();
        HistoricActivityInstanceQuery historicActivityInstanceQuery =  historyService.createHistoricActivityInstanceQuery();
        historicActivityInstanceQuery = historicActivityInstanceQuery.processInstanceId(processInstanceId).finished();
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.list();

        list.forEach(historicActivityInstance -> {
            System.out.println("activityId:" + historicActivityInstance.getActivityId());
        });
        System.out.println("=====================================");

    }
}
