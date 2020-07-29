package org.camunda.bpm.getstarted.loanapproval;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: garfield
 * @Date: 2020/7/21 10:27
 */
public class DMNTaskTest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("=====================================");
        System.out.println("DMN task run");

        Map<String,Object> map = new HashMap<>();
        map = execution.getVariables();
        map.forEach((k,v) -> {
            System.out.println("key:" + k + ",value:" + v);
        });
    }
}
