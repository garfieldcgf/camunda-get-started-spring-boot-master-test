package org.camunda.bpm.getstarted.loanapproval;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @Author: garfield
 * @Date: 2020/7/21 10:27
 */
public class SendTaskTest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("=====================================");
        System.out.println("send task run");
    }
}
