package org.camunda.bpm.getstarted.loanapproval;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: garfield
 * @Date: 2020/7/20 10:46
 */

@RestController
@RequestMapping("/process")
public class ProcessController {


    /**
     * Query on deployments and process definitions known to the engine.
     *
     * Suspend and activate process definitions. Suspending means no further operations
     * can be done on them, while activation is the opposite operation.
     *
     * Retrieve various resources such as files contained within the deployment or
     * process diagrams that were automatically generated by the engine.
     */
    @Autowired
    private RepositoryService repositoryService;


    @Autowired
    private RuntimeService runtimeService;



    /**
     * Querying tasks assigned to users or groups.
     *
     * Creating new standalone tasks. These are tasks that are not related to a process instances.
     *
     * Manipulating to which user a task is assigned or which users are in some way involved with the task.
     *
     * Claiming and completing a task. Claiming means that someone decided to be
     * the assignee for the task, meaning that this user will complete the task.
     * Completing means ‘doing the work of the tasks’. Typically this is filling in a form of sorts.
     */
    @Autowired
    private TaskService taskService;


    @PostMapping(value = "/execute",produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public void execute(@RequestBody Vo vo){

        Class clazz = Class.forName("org.camunda.bpm.getstarted.loanapproval.ProcessController");
        Method method = clazz.getDeclaredMethod(vo.getFunction(),Vo.class);
        method.invoke(this,vo);
    }



    private void deleteAllProcessInstance(Vo vo){
        runtimeService.createProcessInstanceQuery().list().forEach(processInstance -> {
            runtimeService.deleteProcessInstance(processInstance.getRootProcessInstanceId(),"hello");
        });

    }

    private void deleteProcessInstance(Vo vo){
        String processInstanceId = vo.getProcessInstanceId();
        runtimeService.deleteProcessInstance(processInstanceId,"hello");
    }

    private void complete(Vo vo){
        taskService.complete(vo.getTaskId());
    }

    private void getTask(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        Task task = taskService.createTaskQuery()
                .taskAssignee(assignee) //当前登录用户的id
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println(task.getId());

    }

    private void addAnActivity(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .startAfterActivity(activityId)
                .setVariable("assignee","demo")
                .execute();
    }


    private void stopProcess(Vo vo){
        String processDefinitionId = vo.getProcessDefinitionId();
        runtimeService.suspendProcessInstanceByProcessDefinitionId(processDefinitionId);
    }


    /**
     * cancel one activity instance
     * @param vo
     */
    private void cancelAnActivity(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityInstanceId = vo.getActivityInstanceId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(activityInstanceId)
                .execute();
    }

    /**
     * cancel all the activity instances in the process instance(@processInstanceId)
     * @param vo
     */
    private void cancelAll(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelAllForActivity(activityId)
                .execute();
    }


    /**
     * 有些场景下需要后跳转，比如需要前者再执行一遍监听
     * @param vo
     */

    private void modifyAfter(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityInstanceId = vo.getActivityInstanceId();

        String ancestorActivityInstanceId = vo.getAncestorActivityInstanceId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .startAfterActivity(activityId) //two
                .cancelActivityInstance(activityInstanceId) //six
                .execute();
    }

    private void modifyMulti(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityId_2 = vo.getActivityId_2();
        String activityInstanceId = vo.getActivityInstanceId();
        String activityInstanceId_2 = vo.getActivityInstanceId_2();

        String ancestorActivityInstanceId = vo.getAncestorActivityInstanceId();
        ProcessInstanceModificationBuilder processInstanceModification = runtimeService.createProcessInstanceModification(processInstanceId);

        processInstanceModification.startBeforeActivity(activityId);
        processInstanceModification.startBeforeActivity(activityId_2);
        processInstanceModification.cancelActivityInstance(activityInstanceId);
        processInstanceModification.execute();
    }


    private void modifyBefore(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityInstanceId = vo.getActivityInstanceId();

        String ancestorActivityInstanceId = vo.getAncestorActivityInstanceId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .startBeforeActivity(activityId)
                .cancelActivityInstance(activityInstanceId)
                .execute();
    }


    private void multiInstanceAdd(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityInstanceId = vo.getActivityInstanceId();

        String ancestorActivityInstanceId = vo.getAncestorActivityInstanceId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .startBeforeActivity(activityId)
                .setVariable("assignee","demo") //可以设置变量
                .execute();
    }

    private void multiInstanceDel(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String activityInstanceId = vo.getActivityInstanceId();

        String ancestorActivityInstanceId = vo.getAncestorActivityInstanceId();
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(activityInstanceId)
                .execute();
    }




    /**
     * restart from completed or ended process
     */
    private void jump(Vo vo){
        String assignee = vo.getAssignee();
        String processDefinitionId = vo.getProcessDefinitionId();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String processDefinitionKey = vo.getProcessDefinitionId();
        String businessKey = vo.getBusinessKey();
//
//        ProcessInstantiationBuilder processInstantiationBuilder = runtimeService.createProcessInstanceByKey(processDefinitionKey);
//        ProcessInstance processInstance = processInstantiationBuilder.businessKey(businessKey).startBeforeActivity(activityId).execute();

        runtimeService.restartProcessInstances(processDefinitionId)
                .processInstanceIds(processInstanceId).
                startBeforeActivity(activityId)
                .execute();
    }

    /**
     * restart from completed or deleted process
     */
    private void restart(Vo vo){
        String assignee = vo.getAssignee();
        String processDefinitionId = vo.getProcessDefinitionId();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String processDefinitionKey = vo.getProcessDefinitionId();
        String businessKey = vo.getBusinessKey();

        runtimeService.restartProcessInstances(processDefinitionId)
                .processInstanceIds(processInstanceId).
                startBeforeActivity(activityId)
                .initialSetOfVariables() // 复制第一版变量
                .withoutBusinessKey() //不改变业务秘钥
//                .executeAsync() //可以选择异步批处理，需要监控失败
                .execute();
    }

    private void deleteTask(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String processDefinitionKey = vo.getProcessDefinitionId();
        String businessKey = vo.getBusinessKey();
        String taskId = vo.getTaskId();
        taskService.deleteTask(taskId);
    }

    private void createTask(Vo vo){
        String assignee = vo.getAssignee();
        String processInstanceId = vo.getProcessInstanceId();
        String activityId = vo.getActivityId();
        String processDefinitionKey = vo.getProcessDefinitionId();
        String businessKey = vo.getBusinessKey();
        String parentTaskId = vo.getParentTaskId();
        Task task = taskService.newTask();
        task.setAssignee(assignee);
        task.setName("new task");
        task.setParentTaskId(parentTaskId);
        taskService.saveTask(task);
    }




    private void migrate(Vo vo){
        String sourceProcessDefinitionId = vo.getSourceProcessDefinitionId();
        String targetProcessDefinitionId = vo.getTargetProcessDefinitionId();

        String sourceActivityId = vo.getSourceActivityId();
        String targetActivityId = vo.getTargetActivityId();

        MigrationPlan migrationPlan = runtimeService
                .createMigrationPlan(sourceProcessDefinitionId,targetProcessDefinitionId)
                .mapEqualActivities()//表示相同的活动全部迁移
//                .updateEventTriggers() //全部升级触发器,未定义触发器会报错
                .mapActivities(sourceActivityId,targetActivityId) //单独映射
//                .updateEventTrigger() //选择升级触发器,未定义触发器会报错
                .build();

        List<String> processInstanceIds = new ArrayList<>();
        processInstanceIds.add(vo.getProcessInstanceId());

        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(processInstanceIds) //可以用一个查询代替ProcessInstanceQuery
                .skipCustomListeners() //当原本的流程定义里面缺少监听器里面需要的变量时
                .skipIoMappings() //可以用这两个方法
//                .executeAsync(); //异步批量执行
                .execute();
    }

    private void migrate_2(Vo vo){
        String sourceProcessDefinitionId = vo.getSourceProcessDefinitionId();
        String targetProcessDefinitionId = vo.getTargetProcessDefinitionId();
        String sourceActivityId = vo.getSourceActivityId();
                String targetActivityId = vo.getTargetActivityId();
        MigrationPlan migrationPlan = runtimeService.createMigrationPlan(sourceProcessDefinitionId,targetProcessDefinitionId)
                .mapEqualActivities()//表示相同的活动全部迁移
//                .updateEventTriggers() //全部升级触发器
        .build();
        List<String> processInstanceIds = new ArrayList<>();
        processInstanceIds.add(vo.getProcessInstanceId());
        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(processInstanceIds) //可以用一个查询代替ProcessInstanceQuery
                .skipCustomListeners() //当原本的流程定义里面缺少监听器里面需要的变量时
                .skipIoMappings() //可以用这两个方法
//                .executeAsync(); //异步批量执行
                .execute();
    }

    /**
     * 回退
     *      1.取消当前节点
     *      2.在指定节点启动
     * 跳转
     *      1.取消当前节点
     *      2.在指定节点启动
     */



    @Autowired
    private DecisionService decisionService;

    private void dmn(Vo vo){
        VariableMap variables = Variables.createVariables()
                .putValue("season", "Spring")
                .putValue("guestCount", 10)
                .putValue("guestsWithChildren", false);

        DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("dish", variables);
        String desiredDish = dishDecisionResult.getSingleEntry();

        System.out.println("Desired dish: " + desiredDish);

        DmnDecisionTableResult beveragesDecisionResult = decisionService.evaluateDecisionTableByKey("beverages", variables);
        List<Object> beverages = beveragesDecisionResult.collectEntries("beverages");

        System.out.println("Desired beverages: " + beverages);
    }

}
