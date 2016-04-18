package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.web.api.MyMissiveController;
import com.google.common.collect.ImmutableMap;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by sqhe on 14-8-15.
 * The helper Class to access Activiti
 */
@Component
public class ActivitiService {

    //郑小罗 2014/12/7 9:58:42
    @Value("${spring.main.missiveUrl}")
    String missiveUrl;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private SimpleMailSender mailSender;
    @Autowired
    private SendMsgService msgS;

    @Autowired
    private  UserRepository usrR;

    @Autowired
    private MissivePublishRepository misR;

    @Autowired
    private MissiveRecSeeCardRepository misRecR;
    @Autowired
    private SendConfigRepository sendService;
    @Autowired
    private MyMissiveController mmc;

    private final static Logger logger = LoggerFactory.getLogger(CommonFunction.class);


    public void delegateTask(Long taskID,String tagetUserName)
    {
        taskService.delegateTask(taskID.toString(),tagetUserName);
    }


    /**
    * start a activiti process by ProcessDefId and BusinessId
    */
    public String startProcess(String porcessDefId,Long businessId,User user)
    {
        identityService.setAuthenticatedUserId(user.getUserName());
        ImmutableMap<String,Object> var = ImmutableMap.of(getProcessFirstAssignee(porcessDefId),(Object)user.getUsername());
        ProcessInstance process  = this.runtimeService.startProcessInstanceById(porcessDefId,businessId.toString(),var);
        if (process!=null)
            return process.getId();
        else
            return null;
    }

    /**
     * start a activiti process by ProcessDefId and BusinessId
     */
    public String startProcess(String porcessDefId,Long businessId,User user,String isPass,String assignees,String userName)
    {
        identityService.setAuthenticatedUserId(user.getUserName());
        ImmutableMap<String,Object> var = ImmutableMap.of(getProcessFirstAssignee(porcessDefId),(Object)user.getUsername(),"IsPass",isPass,assignees,userName);
        ProcessInstance process  = this.runtimeService.startProcessInstanceById(porcessDefId,businessId.toString(),var);
        if (process!=null)
            return process.getId();
        else
            return null;
    }

    public String getPreviousTaskIDByCurrentTaskID(String taskID)
    {
        String processid="";
        String TASKID=taskID;
        Task temptask = this.taskService.createTaskQuery().taskId(taskID).singleResult();

        if(temptask!=null) {
            processid = temptask.getProcessInstanceId();


        }
        List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(processid).orderByHistoricTaskInstanceEndTime().desc().list();

            //return previousTasklist.get(0).getId();
        if(!previousTasklist.isEmpty()) {
            return previousTasklist.get(0).getId();
        }
        else return TASKID;
    }


    /**
     * add new user to activiti
     * @param userId
     */
    public void addNewUser(String userId)
    {
        //this.identityService.newUser(userId);
        org.activiti.engine.identity.User user=identityService.newUser(userId);
        user.setPassword("shou123456");
        this.identityService.saveUser(user);

    }





    public String get(String porcessDefId,Long businessId,User user)
    {
        identityService.setAuthenticatedUserId(user.getUserName());
        ImmutableMap<String,Object> var = ImmutableMap.of(getProcessFirstAssignee(porcessDefId),(Object)user.getUsername());
        ProcessInstance process  = this.runtimeService.startProcessInstanceById(porcessDefId,businessId.toString(),var);
        if (process!=null)
            return process.getId();
        else
            return null;
    }


    public String getProcessFirstAssignee(String porcessDefId){
        BpmnModel model= this.repositoryService.getBpmnModel(porcessDefId);
        List<FlowElement> flowElementList = (List<FlowElement>)model.getMainProcess().getFlowElements();
        if (flowElementList !=null && flowElementList.size()>=2 && flowElementList.get(1).getClass()==UserTask.class)
        {
            String assigneeName =  ((UserTask)flowElementList.get(1)).getAssignee();
            return assigneeName.substring(2,assigneeName.length()-1);

        }else
            return null;
    }
    //获取最新流程定义
    public List<ProcessDefinition> getAllProcessD()
    {
        List<ProcessDefinition> result = this.repositoryService.createProcessDefinitionQuery().latestVersion().list();

        return result;
    }


    public List<Map<String,? extends Object>> getNextTaskInfo(String processIstanceId,int taskId){
        ProcessInstance process = this.runtimeService.createProcessInstanceQuery().processInstanceId(processIstanceId).singleResult();
        if(process!=null){
            BpmnModel model= this.repositoryService.getBpmnModel(process.getProcessDefinitionId());
            Task task = this.getCurrentTask(taskId);
            if(task!=null){
                //FlowElement flowTask = model.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
                FlowElement flowTask= model.getFlowElement(task.getTaskDefinitionKey());

                if (flowTask !=null &&  flowTask.getClass()==UserTask.class)
                {
                    UserTask ut = ((UserTask)flowTask);
                    String nextElementName = ut.getOutgoingFlows().get(0).getTargetRef();
                    //FlowElement nextElement = model.getMainProcess().getFlowElement(nextElementName);
                    FlowElement nextElement = model.getFlowElement(nextElementName);
                    if (nextElement !=null &&  nextElement.getClass()== ExclusiveGateway.class)
                    {
                        ExclusiveGateway gateway = (ExclusiveGateway)nextElement;
                        List<SequenceFlow> flowList = gateway.getOutgoingFlows();
                        List<Map<String,? extends Object>> resultList = new ArrayList<Map<String,? extends Object>>();
                        for(SequenceFlow flow :flowList)
                        {
                            if (model.getFlowElement(flow.getTargetRef()).getClass()!=EndEvent.class&&model.getFlowElement(flow.getTargetRef()).getClass()!=SubProcess.class)   //
                            {
                                ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", flow.getName(),
                                        "condition", flow.getConditionExpression().split("\"")[1],
                                        "assignee", ((UserTask) model.getFlowElement(flow.getTargetRef())).getAssignee().replace("${","").replace("}",""),    //
                                       // "assignee", ((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef())).getAssignee().replace("${","").replace("}",""),
                                        "multi", haveMultiInstanceTask((Activity) model.getFlowElement(flow.getTargetRef())),
                                        "multiAssignee", getMultiInstanceAssignee((Activity) model.getFlowElement(flow.getTargetRef()))
                                );
                                resultList.add(temp);
                            }else if(model.getFlowElement(flow.getTargetRef()).getClass()==SubProcess.class){   //

                                ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", flow.getName(),
                                        "condition", flow.getConditionExpression().split("\"")[1],
                                        //      "assignee", ((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef())).getAssignee().replace("${","").replace("}",""),
                                        "multi", haveMultiInstanceTask((Activity) model.getMainProcess().getFlowElement(flow.getTargetRef())),
                                        "multiAssignee", getMultiInstanceAssignee((Activity) model.getMainProcess().getFlowElement(flow.getTargetRef()))
                                );
                                resultList.add(temp);
                            }
                            else
                            {
                                ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", flow.getName(),
                                        "condition", flow.getConditionExpression().split("\"")[1]
                                );
                                resultList.add(temp);
                            }




                        }

                        return resultList;
                    }else if(nextElement !=null &&  nextElement.getClass()== UserTask.class){

                        UserTask nextTask =(UserTask)nextElement;
                        List<Map<String,? extends Object>> resultList = new ArrayList<Map<String,? extends Object>>();
//                        ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", nextTask.getName(),
//                                "condition", "",
//                                "assignee", nextTask.getAssignee().replace("${","").replace("}",""),
//                                "multi", false,
//                                "multiAssignee", "");
//                        resultList.add(temp);
                        ImmutableMap<String, ? extends Object> temp = ImmutableMap.of("name", nextTask.getName(),
                                "condition","",
                                "assignee",  nextTask.getAssignee().replace("${","").replace("}",""),    //
                                // "assignee", ((UserTask) model.getMainProcess().getFlowElement(flow.getTargetRef())).getAssignee().replace("${","").replace("}",""),
                                "multi", haveMultiInstanceTask(nextTask),
                                "multiAssignee", getMultiInstanceAssignee(nextTask)
                        );
                        resultList.add(temp);
                        return resultList;
                    }
                    //return ((UserTask)flowTask).getAssignee();
                    return null;
                }else
                    return null;

            }else
                return null;

        }else
            return null;

        // return flowElementList.size();
    }
    public void deleteUser(String userId)
    {
        this.identityService.deleteUser(userId);

    }

    public Task getCurrentTask(int taskId)
    {
        Task task = this.taskService.createTaskQuery().taskId(String.valueOf(taskId)).singleResult();
        return task;
    }



    public boolean haveMultiInstanceTask(Activity task)
    {
        MultiInstanceLoopCharacteristics loop = task.getLoopCharacteristics();
        if (loop==null)
            return false;
            else
            return true;
    }



    public String getMultiInstanceAssignee(Activity task)
    {

        MultiInstanceLoopCharacteristics loop = task.getLoopCharacteristics();
        if (loop==null)
            return "";
        else
            return loop.getInputDataItem();
    }


    public List<Task> getCurrentTasksByUser(User user)
    {
        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).orderByTaskCreateTime().desc().list();
        return tasklist;
    }




    public Task getCurrentTasksByProcessInstanceId(Long id)
    {
//        Task task = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).singleResult();
        List tasks = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).list();
        Task task;
        if(tasks.size()>=1){
             task = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).list().get(0);
            return task;
        }
        else {
            task=null;
        }
//        Task task = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).list().get(0);
        return task;
    }

    public List<Task> getCurrentTaskListByProcessInstanceId(Long id)
    {
        List<Task> task = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(id)).list();
        return task;
    }

    /**
        获取所有最新的流程定义
     */
    public List<ProcessDefinition> getAllLastestProcessDefinition()
    {
        List<ProcessDefinition> result = this.repositoryService.createProcessDefinitionQuery().latestVersion().list();
        return result;
    }


    /**
     获取所有历史流程定义,不包含exclusiveGateway
     */
    public List<HistoricActivityInstance> getHistoryByProcessId(Long id)
    {
        List<HistoricActivityInstance> result =this.historyService.createHistoricActivityInstanceQuery().processInstanceId(id.toString()).list();
        /*for (HistoricActivityInstance instance : result)
        {
            if (instance.getActivityType().equals("exclusiveGateway"))
                result.remove(instance);
        }*/
        Iterator<HistoricActivityInstance> activityListIterator = result.iterator();
        while(activityListIterator.hasNext()){
            HistoricActivityInstance instance = activityListIterator.next();
            if (instance.getActivityType().equals("exclusiveGateway"))
                activityListIterator.remove();
        }
        return result;
    }


    /*10:56:15
    何盛琪 2014/11/24 10:56:15*/
    //分页获取正在进行的任务
    public PageableTaskList getProcessIngByUserName(User user,int pageSize,int pageNum)
    {
        List<Task> taskList = this.taskService.createTaskQuery().taskAssignee(user.getUserName()).orderByTaskCreateTime().desc()
                .listPage((pageNum - 1) * pageSize, pageSize);
        long taskTotal = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).count();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,taskList,pageNum,pageSize,(int)pageTotal);
        return result;
    }
    //获取完成的任务
//    public PageableHistoryTaskList getProcessDoneByUserName(User user,int pageSize,int pageNum){
//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(user.getUserName()).finished().orderByTaskDueDate().desc()
//                .listPage((pageNum - 1) * pageSize, pageSize);
//        long taskTotal = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).count();
//        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
//        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
//        return result;
//    }


    //获取完成的任务并按照流程归并
    public PageableHistoryTaskFilterList geFiltertProcessDoneByUserName(String userName,int pageSize,int pageNum){
//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
//
//                .taskAssignee(user.getUserName()).finished().orderByTaskDueDate().desc()
//                .listPage((pageNum - 1) * pageSize, pageSize);

        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+userName+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) order by END_TIME_ desc")
                .listPage((pageNum - 1) * pageSize, pageSize);
        List<TaskForm> lftDone=new ArrayList<TaskForm>();

        lftDone = mmc.getTaskFormByHistroyTask(historicTaskInstanceList);


        long taskTotal=lftDone.size();
        //long taskTotal = historicTaskInstanceList.size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskFilterList result = new PageableHistoryTaskFilterList((int)taskTotal,lftDone,pageNum,pageSize,(int)pageTotal);
        return result;
    }

    //获取完成的任务并按照流程归并
//    public PageableHistoryTaskList getProcessDoneByUserName(String userName,int pageSize,int pageNum){
////        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
////
////                .taskAssignee(user.getUserName()).finished().orderByTaskDueDate().desc()
////                .listPage((pageNum - 1) * pageSize, pageSize);
//
//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+userName+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) order by END_TIME_ desc")
//                .listPage((pageNum - 1) * pageSize, pageSize);
//        long taskTotal=this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+userName+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) order by END_TIME_ desc").list().size();
//        //long taskTotal = historicTaskInstanceList.size();
//        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
//        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
//        return result;

//    select * from oa3.act_hi_taskinst f where f.ID_ IN (select MAX(e.ID_) from  oa3.act_hi_taskinst e where e.ASSIGNEE_='xulinlin'  and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ )

    //    }
    public PageableHistoryTaskList getProcessDoneByUserName(String userName,int pageSize,int pageNum){
        logger.info("getHistoryTask page start");

//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
//
//                .taskAssignee(user.getUserName()).finished().orderByTaskDueDate().desc()
//                .listPage((pageNum - 1) * pageSize, pageSize);

//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select MAX(e.ID_) from  oa3.act_hi_taskinst e where e.ASSIGNEE_='"+userName+"'   and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ ) order by f.END_TIME_ DESC")
//                .listPage((pageNum - 1) * pageSize, pageSize);
//        long taskTotal=this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select MAX(e.ID_) from  oa3.act_hi_taskinst e where e.ASSIGNEE_='"+userName+"'   and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ ) order by f.END_TIME_ DESC").list().size();
//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("SELECT * FROM oa3.act_hi_taskinst e where e.ASSIGNEE_='"+userName+"'  and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ order by e.END_TIME_ desc")
//                .listPage((pageNum - 1) * pageSize, pageSize);
//        long taskTotal=this.historyService.createNativeHistoricTaskInstanceQuery()
//                .sql("SELECT * FROM oa3.act_hi_taskinst e where e.ASSIGNEE_='"+userName+"'  and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ order by e.END_TIME_ desc").list().size();
        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+userName+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) and PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束' or b.ACT_NAME_='End' ) and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) order by END_TIME_ desc")
                .listPage((pageNum - 1) * pageSize, pageSize);
        long taskTotal=this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+userName+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) and PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束' or b.ACT_NAME_='End') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) order by END_TIME_ desc").list().size();
        //long taskTotal = historicTaskInstanceList.size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
        logger.info("getHistoryTask page end");

        return result;
    }

    public PageableHistoryTaskList getAllProcessDone(String userName,int pageSize,int pageNum){
        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where  d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1' or d.END_ACT_ID_='EndNewsReview') order by f.END_TIME_ DESC" )
                .listPage((pageNum - 1) * pageSize, pageSize);


        long taskTotal= this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where  d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1' or d.END_ACT_ID_='EndNewsReview' ) order by f.END_TIME_ DESC").list().size();

        //long taskTotal = historicTaskInstanceList.size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
        return result;
    }
    public PageableHistoryTaskList getAllDepProcessDone(Long groupID,int pageSize,int pageNum){
        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_) and f.ASSIGNEE_ IN (SELECT a.username FROM oa3.users a where group_id ='"+groupID+"') and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where  d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1' or d.END_ACT_ID_='EndNewsReview') order by f.END_TIME_ DESC" )
                .listPage((pageNum - 1) * pageSize, pageSize);


        long taskTotal= this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_)  and f.ASSIGNEE_ IN (SELECT a.username FROM oa3.users a where group_id ='"+groupID+"')  and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where  d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1' or d.END_ACT_ID_='EndNewsReview' ) order by f.END_TIME_ DESC").list().size();

        //long taskTotal = historicTaskInstanceList.size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
        return result;
    }


    public PageableHistoryTaskList getPublishMissiveList(String userName,int pageSize,int pageNum){
        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery().sql("    select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where d.START_ACT_ID_='StartPublish' and (d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1')) order by f.END_TIME_ DESC")
                .listPage((pageNum - 1) * pageSize, pageSize);


        long taskTotal= this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa3.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa3.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa3.act_hi_procinst d where d.START_ACT_ID_='StartPublish' and (d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1')) order by f.END_TIME_ DESC").list().size();

        //long taskTotal = historicTaskInstanceList.size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);
        return result;
    }



   //获取父任务
    public String getParentFlow(String taskID)
    {
        Task currentTask = this.taskService.createTaskQuery().taskId(taskID).active().singleResult();
        return currentTask.getParentTaskId();
    }

    //判断是否是返回的任务
    public boolean isBack(String taskID)
    {
        Task temptask = this.taskService.createTaskQuery().taskId(taskID).singleResult();
        String processid = temptask.getProcessInstanceId();
        HistoricVariableInstance isPass = this.historyService.createHistoricVariableInstanceQuery().processInstanceId(processid).variableName("IsPass").singleResult();
        if(isPass!=null && isPass.getValue() !=null) {
            String isPassValue = isPass.getValue().toString();
            return isPassValue.contains("Back");
        }
        else{
            return false;
        }
    }


    //分页获取指定用户当前任务
    public PageableTaskList getCurrentTasksByUser(User user,int pageSize,int pageNum)
    {

        List<Task> tasklist = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).orderByTaskCreateTime().desc()
                .listPage((pageNum - 1) * pageSize, pageSize) ;
        long taskTotal = this.taskService.createTaskQuery().active().taskAssignee(user.getUserName()).count();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,tasklist,pageNum,pageSize,(int)pageTotal);
        return result;
    }

    public PageableHistoryTaskList getUncompletedTasksByUser(User user,int pageSize,int pageNum)
    {

        logger.info("getUncompletedHistoryTask page start");

        List<HistoricTaskInstance> historicTaskInstanceList=null;


         historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+user.getUserName()+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) and PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束' or b.ACT_NAME_='End') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c))   and  PROC_INST_ID_  IN (  SELECT  b.PROC_INST_ID_ from oa3.act_hi_procinst b where b.END_ACT_ID_  IS NULL) order by END_TIME_ desc")
                .listPage((pageNum - 1) * pageSize, pageSize);

        long taskTotal = this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='"+user.getUserName()+"'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) and PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束' or b.ACT_NAME_='End') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c))   and  PROC_INST_ID_  IN (  SELECT  b.PROC_INST_ID_ from oa3.act_hi_procinst b where b.END_ACT_ID_  IS NULL) order by END_TIME_ desc")
                .list().size();
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableHistoryTaskList result = new PageableHistoryTaskList((int)taskTotal,historicTaskInstanceList,pageNum,pageSize,(int)pageTotal);


//        List<TaskForm> ltfIng=null;
//
//
//        if(historicTaskInstanceList!=null){
//            ltfIng=mmc.getUnfinishedTaskByUser(historicTaskInstanceList);
//        }





        logger.info("getUncompletedHistoryTask page end");

        return result;
    }

    //获取指定用户的当前任务数量
    public Long getCurrentTasksByUser(String userName){
        Long taskCount = this.taskService.createTaskQuery().active().taskAssignee(userName).count();
        return taskCount;
    }



    /**
     获取流程定义中的说明
     */
    public String getDocumentationByProcessId(Long id)
    {
        String processDefId = "";
        if (this.runtimeService.createProcessInstanceQuery().processInstanceId(id.toString()).count()>0) {
            ProcessInstance instance = this.runtimeService.createProcessInstanceQuery().processInstanceId(id.toString()).singleResult();
            processDefId = instance.getProcessDefinitionId();
        }else
        {
            HistoricProcessInstance instance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(id.toString()).singleResult();
            processDefId = instance.getProcessDefinitionId();
        }

        BpmnModel model= this.repositoryService.getBpmnModel(processDefId);
        return model.getMainProcess().getDocumentation();

    }


    /**
     获取流程定义中task是否是能够在pad端处理的
     */
    public boolean isPadTask(String processDefId,String taskId)
    {

        BpmnModel model= this.repositoryService.getBpmnModel(processDefId);
        FlowElement element = model.getFlowElement(taskId);
        if (element!=null && element.getClass()==UserTask.class)
        {
            if (((UserTask)element).getDocumentation().equals("pdf"))
                return true;
        }
        return false;
    }

    public void completeTask(Long taskID,Map<String,Object> vars)
    {
        logger.info("complete task "+taskID.toString());
        this.taskService.complete(taskID.toString(),vars);
    }

    //by zxl 2014-12-07
    public void completeTask(Long taskID,Map<String,Object> vars,String type,Long processID)
    {
        logger.info("complete "+type+" task "+taskID.toString()+" process id "+processID.toString());
        this.taskService.complete(taskID.toString(),vars);
        //完成每一步的时候执行插入全文检索
//        boolean isFishedProcess=isProcessFinished(Long.toString(processID));
//        //isFishedProcess=true;//调试全文索引使用，后面一定要去掉
//        if (isFishedProcess) {
//            fullsearchR.setSearchContent(type, Long.toString(taskID), Long.toString(processID));
//        }
    }
    //获取完成的任务
    public List<HistoricTaskInstance> getDoneProcessByUserName(String username){
//        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
//                .taskAssignee(username).taskDeleteReason("completed").orderByProcessInstanceId().desc().list();



        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery()
                .sql("SELECT * FROM oa3.act_hi_taskinst e where e.ASSIGNEE_='"+username+"'  and e.END_TIME_!='' and e.DELETE_REASON_='completed' and e.PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) group by e.PROC_INST_ID_ ").list();
//        List<String> result = new ArrayList<String>();
//        for(HistoricTaskInstance task:historicTaskInstanceList)
//        {
//            result.add(task.getProcessDefinitionId()+","+task.getProcessInstanceId()+","+task.getId());
//
//        }

        return historicTaskInstanceList;
    }
    //根据processID获取到所有经手人
    public List<String> getProcessUsersByProcessID(String processID){

        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processID).processFinished().orderByProcessInstanceId().desc().list();
        List<String> result = new ArrayList<String>();
        for(HistoricTaskInstance task:historicTaskInstanceList)
        {
            result.add(task.getAssignee());//获取流程参与者
        }
        return result;
    }
    //根据processID获取该流程是不是结束
    public boolean isProcessFinished(String ProcessId)
    {
        long result= this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(ProcessId).finished().count();
        if(result==0)
        {
            return false;
        }else
        {
            return true;
        }
    }

    public boolean isProcessFinishedByTaskID(String taskID)
    {
       HistoricTaskInstance task =  this.historyService.createHistoricTaskInstanceQuery().taskId(taskID).singleResult();
        if (task==null)
            return false;
        long countOfProcess = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).finished().count();
        if (countOfProcess == 0)
            return false;
        return true;
    }
    //发送邮件提醒
    public boolean emailSender(String type,boolean isSend,String emailAddress,String missiveTitle,String taskID,String processID){

            SendConfig flag=sendService.findByName("emailSend");  //03-04孙乐
            String sendFlag=flag.getValue();
            String missiveUrlStr="";
            emailAddress=emailAddress==null?"":emailAddress;//判断用户是否存在
            String[] nextUsers={emailAddress};
            missiveUrlStr=getMissiveUrlStr(type,taskID,processID);
            if (isSend) {                               //如果需要发送邮件，调用发送函数
                mailSender.HTMLMailSend(missiveTitle, missiveUrlStr, nextUsers);//发送邮件
                return true;
            }
            else return false;
    }
   /* public boolean emailSender(String type,User usr,String missiveTitle,String taskID,String processID){
        if (usr!=null) {
            boolean isSend = usr.getEmailSend();//获取是否需要发送邮件
            String missiveUrlStr="";
            String emailAddress=usr.getEmail()==null?"":usr.getEmail();//判断用户是否存在
            String[] nextUsers={emailAddress};
            missiveUrlStr=getMissiveUrlStr(type,taskID,processID);
            if (isSend) {//如果需要发送邮件，调用发送函数
                mailSender.HTMLMailSend(missiveTitle, missiveUrlStr, nextUsers);//发送邮件
                return true;
            }
        }
        return  false;
    }*/
    //发送短信提醒
    public boolean msgSender(User usr,String missiveTitle,String lastTasker ) throws IOException {
        String smsMobNum="";
        SendConfig flag=sendService.findByName("messageSend");
        String sendFlag=flag.getValue();
        if (usr!=null){
            if (usr.getMsgSend()){
                smsMobNum=usr.getTel();//获取手机号码
                msgS.sendMsg(smsMobNum,missiveTitle,lastTasker);
                return true;
            }
        }
        return false;
    }
    public boolean msgSenderbyCount(User usr,String missiveCount) throws IOException {
        String smsMobNum="";
        SendConfig flag=sendService.findByName("messageSend");
        String sendFlag=flag.getValue();
        if (usr!=null){
           // if (usr.getMsgSend()&&!missiveCount.equals("0")){
            if (!missiveCount.equals("0")){

                    smsMobNum=usr.getTel();//获取手机号码
                msgS.sendMsgbyCount(smsMobNum,missiveCount);
                return true;
            }
        }
        return false;
    }
    //根据公文类型，返回公文类别字符串
    public String getMissiveUrlStr(String type,String taskID,String processID){
        String missiveUrlStr=missiveUrl;
        if (type.equals("faxCable")){
            missiveUrlStr=missiveUrlStr+"FaxCable/"+taskID+"/"+processID;
        }else if (type.equals("missivePublish")){

            missiveUrlStr=missiveUrlStr+"/missive/missivePublish/"+processID+"/"+taskID;//missiveUrlStr+"MissivePublish/"+taskID+"/"+processID;

        }else if (type.equals("missiveSign")){

            missiveUrlStr=missiveUrlStr+"/missiveSign/SignMissive/"+processID+"/"+taskID;//missiveUrlStr+"MissiveSign/"+taskID+"/"+processID;

        }else if (type.contains("ReceiptId")){

            missiveUrlStr=missiveUrlStr+type+"/"+processID+"/"+taskID;
        }

        return missiveUrlStr;
    }

    //获取所有的当前任务
    public PageableTaskList getAllCurrentTaskList(int pageSize,int pageNum){

        /*List<Task>taskList=this.taskService.createNativeTaskQuery()
                .sql("SELECT * FROM oa3.act_ru_task t order by t.create_time_ desc "
                ).listPage((pageNum - 1) * pageSize, pageNum * pageSize);*/
        long taskTotal = this.taskService.createTaskQuery().active().count();
        List<Task> taskList;
       /* if(pageNum * pageSize>=taskTotal) {
             taskList = this.taskService.createTaskQuery().active().orderByTaskCreateTime().desc()
                    .list().subList((pageNum - 1) * pageSize,new Long(taskTotal).intValue());
        }else{
             taskList = this.taskService.createTaskQuery().active().orderByTaskCreateTime().desc()
                    .list().subList((pageNum - 1) * pageSize, pageNum * pageSize);
        }*/

        if(pageNum * pageSize>=taskTotal) {
            taskList=this.taskService.createNativeTaskQuery()
                    .sql("SELECT * FROM oa3.act_ru_task t order by (t.description_+0) desc, t.create_time_ desc "
                    ).list().subList((pageNum - 1) * pageSize,new Long(taskTotal).intValue());
        }else{
            taskList=this.taskService.createNativeTaskQuery()
                    .sql("SELECT * FROM oa3.act_ru_task t order by (t.description_+0) desc , t.create_time_ desc "
                    ).list().subList((pageNum - 1) * pageSize, pageNum * pageSize);
        }
        double pageTotal = Math.ceil((double)taskTotal/(double)pageSize);
        PageableTaskList result = new PageableTaskList((int)taskTotal,taskList,pageNum,pageSize,(int)pageTotal);

        return result;
    }


    //郑小罗 2014/12/11 11:32:04
    //对当前的所有任务进行催办督办处理
    public void dealCurrentAllTasksList() throws IOException {
        List<Task> tasklist = this.taskService.createTaskQuery().active().list();
        for (Task tsk:tasklist)
        {
            dealSupervision(tsk);//对当前的任务进行催办督办处理
        }
    }


    public List<String> getNextTasksUserByTaskID(long taksid)
    {
        HistoricTaskInstance task = this.historyService.createHistoricTaskInstanceQuery().finished().taskId(String.valueOf(taksid)).singleResult();
        String currentProcessID = task.getProcessInstanceId();
        List<Task> nextTasks = this.taskService.createTaskQuery().processInstanceId(currentProcessID).active().list();
        List<String> userlist = new ArrayList<String>();
        for (Task temptask : nextTasks)
        {
            userlist.add(temptask.getAssignee());
        }
        return userlist;
    }

    private void dealSupervision(Task task) throws IOException {
        if (!task.equals(null)) {
            String taskType = task.getProcessDefinitionId();//获取任务类别
            Date missiveCreateTime = task.getCreateTime();//获取任务创建时间
            String taskName = task.getName();
            String taskID=task.getId();//任务ID
            String processID = task.getProcessInstanceId();//获取流程processID
            String assine = task.getAssignee();//获取下一步用户
            //根据用户名获取用户信息
            User usr=usrR.getUserInfoByUserName(assine);
            //设定提醒值
            int delayNum=usr.getDelaynum();
            String delayWarm=usr.getDelayWarm();//发送消息形式，m表示短信，e表示邮件,n表示不提醒
            String tel=usr.getTel();//获取用户电话号码
            //获取当前系统时间
            Date currentDate=new Date(System.currentTimeMillis());//获取当前时间
            //根据创建时间与现在时间差
            boolean isPhase=this.isExcee(missiveCreateTime,currentDate,delayNum);//判断是否超过限定值
            if (isPhase) {
                //公文标题及类别提示语
                String missiveTitleAndType = getMissiveTypeByProcessDefID(taskType, processID);
                String[] titleAndType = missiveTitleAndType.split(",");//拆分字符串
                if (titleAndType.length==3) {//严格等于3，超过或是少于3都不正常
                    String missiveTitle = titleAndType[2];//公文标题
                    String missiveTypeName = titleAndType[0];//公文类别中文名称
                    String missiveType = titleAndType[1];//公文类别
                    if (delayWarm.equals("m")){
                        //String sendMsgTxt=missiveTypeName+"："+missiveTitle+"需要"+taskName+"，请您及时处理。";
                        String sendMsgTxt="您有一份公文需要处理，请注意及时处理。";//由于测试，所有短信内容使用统一格式，后面需要使用正式的短信内容
                        this.msgSuperSender(tel,sendMsgTxt);//发送短信
                    }else if (delayWarm.equals("e")){
                        this.emailSuperSender(missiveType,usr,missiveTitle,taskID,processID);//发送邮件
                    }
                }
            }


        }
    }

    //催办督办发送邮件提醒
    public boolean emailSuperSender(String type,User usr,String missiveTitle,String taskID,String processID){
        if (usr!=null) {
            String missiveUrlStr="";
            String emailAddress=usr.getEmail()==null?"":usr.getEmail();//判断用户是否存在
            String[] nextUsers={emailAddress};
            missiveUrlStr=getMissiveUrlStr(type,taskID,processID);
            mailSender.HTMLMailSend(missiveTitle, missiveUrlStr, nextUsers);//发送邮件
            return true;
        }
        return  false;
    }

    //催办督办发送短信提醒
    public boolean msgSuperSender(String tel,String smsTextStr ) throws IOException {
        if (!tel.equals("") && !smsTextStr.equals("")){
            //msgS.sendMsg(tel,smsTextStr);
            return true;
        }
        return false;
    }

    //根据公文类型，返回字符串
    private String getMissiveTypeByProcessDefID(String processDefID,String processID){
        String faxCabel="FaxId:1:";//传真电报
        String missivePub="PublishMissiveId:1:";//发文
        String missiveSign="SignId:1";//签报
        String missiveRec="ReceiptId:1";//收文
        String missiveTitle="";//公文标题
        if (!processDefID.equals("") && !processID.equals("")){

            if (processDefID.contains(missivePub)){
                MissivePublish misPub=misR.findByProcessID(Long.parseLong(processID));
                missiveTitle=misPub.getMissiveTittle();
                return "发文,missivePublish,"+missiveTitle;
            }else if (processDefID.contains(missiveRec)){
                MissiveRecSeeCard misCard=misRecR.getMissData(processID);
                missiveTitle=misCard.getTitle();
                return "收文,missiveReceive,"+missiveTitle;
            }
        }
        return "";
    }


    //获取当前时间与创建时间进行对照，是否超出设定时间，如果超出则发短信或是邮件提醒
    private boolean isExcee(Date createDate,Date currentDate,int delayNum){
        long l=currentDate.getTime()-createDate.getTime();
        long day=l/(24*60*60*1000);//相差的天数
        long hour=l/(60*60*1000);//相差的小时
        long phase=hour-delayNum;//相差的小时与设定值相减，如果大于0，说明超过设定值，否还没有到达设定值
        if (phase>0){
            return true;
        }else {
            return false;
        }
    }






    public void delProcessInstance(String ProcessInstanceID,String deleteReason)
    {

        this.runtimeService.deleteProcessInstance(ProcessInstanceID,deleteReason);
//        this.historyService.deleteHistoricProcessInstance(ProcessInstanceID);
    }

    public void delHistoryProcessInstance(String ProcessInstanceID,String deleteReason)
    {

        //this.runtimeService.deleteProcessInstance(ProcessInstanceID,deleteReason);
        this.historyService.deleteHistoricProcessInstance(ProcessInstanceID);
    }

    //委托办理

    //异步转换发pdf


    public void addTaskComment(String taskid,String comment)
    {
        this.taskService.addComment(taskid,"delete",comment);
    }


}
