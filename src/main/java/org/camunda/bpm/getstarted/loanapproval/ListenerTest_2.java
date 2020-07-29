package org.camunda.bpm.getstarted.loanapproval;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: garfield
 * @Date: 2020/7/21 14:24
 */
public class ListenerTest_2 implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {

        System.out.println(execution.getCurrentActivityId());
        System.out.println("==============================");
        System.out.println("listen_2 execution");
        List<String> assigneeList=new ArrayList<String>(); //分配任务的人员
        assigneeList.add("first");
        assigneeList.add("second");
        assigneeList.add("third");
        Map<String, Object> vars = new HashMap<String, Object>(); //参数
        vars.put("assigneeList", assigneeList);

        execution.setVariables(vars);
    }
}
