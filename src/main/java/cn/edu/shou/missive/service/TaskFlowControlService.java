package cn.edu.shou.missive.service;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TaskFlowControlService
{

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    //ProcessEngine _processEngine;

    private String _processId;


    public TaskFlowControlService()
    {
    }

    public TaskFlowControlService(String processId)
    {
        //_processEngine = processEngine;
        _processId = processId;
    }

    /**
     * 跳转至指定活动节点
     *
     * @param targetTaskDefinitionKey
     * @throws Exception
     */
    public void jump(String processId,String targetTaskDefinitionKey) throws Exception
    {
        TaskEntity currentTask = (TaskEntity) taskService.createTaskQuery()
                .processInstanceId(processId).singleResult();
        jump(currentTask, targetTaskDefinitionKey);
    }

    /**
     *
     * @param currentTaskEntity
     *            当前任务节点
     * @param targetTaskDefinitionKey
     *            目标任务节点（在模型定义里面的节点名称）
     * @throws Exception
     */
    private void jump(final TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception
    {
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());

//        final ActivityImpl activity = ActivityUtils.getActivity(_processEngine,
//                currentTaskEntity.getProcessDefinitionId(), targetTaskDefinitionKey);
        final ActivityImpl activity = pde.findActivity(targetTaskDefinitionKey);
        final ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(currentTaskEntity.getExecutionId()).singleResult();



        //包装一个Command对象
        ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(
                new Command<java.lang.Void>()
                {
                    @Override
                    public Void execute(CommandContext commandContext)
                    {
                        //创建新任务
                        execution.setActivity(activity);
                        execution.executeActivity(activity);

                        //删除当前的任务
                        //不能删除当前正在执行的任务，所以要先清除掉关联
                        currentTaskEntity.setExecutionId(null);
                        taskService.saveTask(currentTaskEntity);
                        taskService.deleteTask(currentTaskEntity.getId(), true);

                        return null;
                    }
                });
    }
}
