package cn.edu.shou.missive.web.api;

import cn.edu.shou.bpmn.cmd.JumpCmd;
import cn.edu.shou.bpmn.cmd.RollbackTaskCmd;
import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.NextTask;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.FileUploadController;
import cn.edu.shou.missive.web.displayController;
//import com.sun.javafx.tk.Toolkit;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hy on 2014/11/24.
 */
@RestController
@RequestMapping(value = "/api")
public class MyMissiveController {
    @Autowired
    private ActivitiService actS;
    @Autowired
    private FileUploadController fileupc;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;

    @Autowired
    private UploadFlagRepository uploadFlagRepository;
    @Autowired
    private ActivitiService actR;
    @Autowired
    private UserRepository userDAO;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private FileUploadController fileUR;
    @Autowired
    private TaskFlowControlService tfcs;
    @Autowired
    private MissiveRepository missiveRepository;

    @Autowired
    private displayController displayController;
//    @Autowired
//    private Missive missive;

    private final static Logger logger = LoggerFactory.getLogger(MyMissiveController.class);


    public List<TaskForm> getTaskFormByTask(List<Task> tasks){
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        if (tasks != null&&tasks.size()!=0) {
            for (Task task : tasks) {
                TaskForm tf = new TaskForm();
                String processDeId = task.getProcessDefinitionId();
                String taskName = task.getName();
                Long instanceId = Long.parseLong(task.getProcessInstanceId());
                Long taskId = Long.parseLong(task.getId());
                String infoLink="/historyProcess/"+instanceId.toString();
                String rollbackLink="/api/taskRollBack/"+taskId+"/rollback";


                //获取前面的任务信息
                String taskState="";
                Boolean isBack = actS.isBack(String.valueOf(taskId));
                if(isBack){
                    taskState="修改";
                }
                else{
                    taskState="正常";
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskStartTime = String.valueOf(format.format(task.getCreateTime()));
                String nowTime=String.valueOf(format.format(new Date()));
                String intelTime="";

                try {


                    Date date = format.parse(taskStartTime);
                    Date now = format.parse(nowTime);
                    long l = now.getTime() - date.getTime();
                    long day = l / (24 * 60 * 60 * 1000);
                    long hour = (l / (60 * 60 * 1000) - day * 24);
                    long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    intelTime="" + day + "天" + hour + "小时" + min + "分" ;
                }
                catch (Exception ex){
                    logger.error("getTaskFormByTask exception:",ex);
                    ex.printStackTrace();
                }
                Long lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(taskId)));//上一个任务编号

                HistoricTaskInstance task2 = this.historyService.createHistoricTaskInstanceQuery().taskId(String.valueOf(lastTaskId)).singleResult();
                if(task2!=null) {
                    User assignUser = this.userDAO.findByUserName(task2.getAssignee());
                    if(assignUser!=null){
                        tf.setPreOwner(assignUser.getName());}else{
                        tf.setPreOwner("未指定");
                    }
                }else {
                    tf.setPreOwner("未指定");
                }



                //查询当前任务名称

                String taskUrl="";
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType;
                String verType="";
                String verTypeTitle="";
                String abbrevType="";

                String missiveTitle="";
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verType="missiveReceive";
                    verTypeTitle ="收文流程";
                    abbrevType="收文";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    if(mrsc!=null&&mrsc.getTitle()!=null) {
                        missiveTitle = mrsc.getTitle();
                    }
                    taskUrl = "/" + processDeId + "/" + instanceId + "/" + taskId;

                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verType="missivePublish";
                    verTypeTitle="发文流程";
                    abbrevType="发文";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                    taskUrl="/missive/missivePublish/"+instanceId+"/"+taskId;
                }

                else{
                    instanceType="";
                }
                String deletedLink=instanceId+","+missiveTitle+","+taskName;

                Schedule schedule= scheduleRepository.findByContent(lastTaskId.toString());
                if(schedule!=null){
                    tf.setUpdateFlag("1");

                }else{
                    tf.setUpdateFlag("0");
                }

                tf.setTaskState(taskState);
                tf.setIntelTime(intelTime);
                tf.setTaskUrl(taskUrl);
                tf.setName(taskName);
                tf.setTaskStartTime(taskStartTime);
                tf.setLastTaskId(lastTaskId);
                tf.setTypeTitle(verTypeTitle);
                tf.setAbbrevType(abbrevType);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                int versionNum = fileupc.getMaxMissiveVersion(instanceId,verType);
                tf.setVersionNum(versionNum);
                missiveTitle=missiveTitle==""?"暂无标题":missiveTitle;
                tf.setMissiveTitle(missiveTitle);
                tf.setInfoLink(infoLink);
                tf.setRollbackLink(rollbackLink);
                tf.setDeletedLink(deletedLink);
                tfs.add(tf);
            }
        }
        return  tfs;
    }

    public List<TaskForm> getTaskFormByTaskList(User currentUser){
        Long groupID=Long.valueOf(currentUser.getGroup().getId());
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        List tasklist =  jdbcTemplate.queryForList("SELECT * FROM oa4.act_ru_task where ASSIGNEE_ IN (SELECT a.username FROM oa4.users a where group_id ='"+groupID+"') order by CREATE_TIME_ desc ");
        if (tasklist != null&&tasklist.size()!=0) {

            Iterator it = tasklist.iterator();
            while(it.hasNext()) {
                Map tasklistMap = (Map) it.next();
                TaskForm tf = new TaskForm();
                String processDeId = tasklistMap.get("PROC_DEF_ID_").toString();
                String taskAssName1=tasklistMap.get("ASSIGNEE_").toString();
                String taskAssName = this.userDAO.findByUserName(taskAssName1).getName();
                Long instanceId =Long.valueOf(String.valueOf(tasklistMap.get("PROC_INST_ID_")));
                //String taskName = displayController.getMissiveTitles(processDeId,instanceId.toString());
                String taskName =tasklistMap.get("NAME_").toString();

                Long taskId =Long.valueOf(tasklistMap.get("ID_").toString());
                List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId.toString()).orderByHistoricTaskInstanceEndTime().desc().list();
                Long latestTaskId=0l;
                if(previousTasklist.size()!=0){
                    latestTaskId=Long.valueOf(previousTasklist.get(0).getId());
                }
//                Long latestTaskId=Long.valueOf(previousTasklist.get(0).getId());
                String infoLink="/historyProcess/"+instanceId.toString();
                String rollbackLink="/api/taskRollBack/"+taskId+"/rollback";


                //获取前面的任务信息
                String taskState="";
                Boolean isBack = actS.isBack(String.valueOf(taskId));
                if(isBack){
                    taskState="修改";
                }
                else{
                    taskState="正常";
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskStartTime = format.format(tasklistMap.get("CREATE_TIME_"));
                String nowTime=String.valueOf(format.format(new Date()));
                String intelTime="";

                try {


                    Date date = format.parse(taskStartTime);
                    Date now = format.parse(nowTime);
                    long l = now.getTime() - date.getTime();
                    long day = l / (24 * 60 * 60 * 1000);
                    long hour = (l / (60 * 60 * 1000) - day * 24);
                    long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    intelTime="" + day + "天" + hour + "小时" + min + "分" ;
                }
                catch (Exception ex){
                    logger.error("getTaskFormByTask exception:",ex);
                    ex.printStackTrace();
                }
                Long lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(taskId)));//上一个任务编号

                HistoricTaskInstance task2 = this.historyService.createHistoricTaskInstanceQuery().taskId(String.valueOf(lastTaskId)).singleResult();
                if(task2!=null) {
                    User assignUser = this.userDAO.findByUserName(task2.getAssignee());
                    if(assignUser!=null){
                        tf.setPreOwner(assignUser.getName());}else{
                        tf.setPreOwner("未指定");
                    }
                }else {
                    tf.setPreOwner("未指定");
                }



                //查询当前任务名称

                String taskUrl="";
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                tf.setTaskAssName(taskAssName);
                String instanceType;
                String verType="";
                String verTypeTitle="";
                String abbrevType="";

                String missiveTitle="";
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verType="missiveReceive";
                    verTypeTitle ="收文流程";
                    abbrevType="收文";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    if(mrsc!=null&&mrsc.getTitle()!=null) {
                        missiveTitle = mrsc.getTitle();
                    }
                    taskUrl = "/" + processDeId + "/" + instanceId + "/" + taskId;

                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verType="missivePublish";
                    verTypeTitle="发文流程";
                    abbrevType="发文";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                    taskUrl="/missive/missivePublish/"+instanceId+"/"+taskId;
                }


                else{
                    instanceType="";
                }
                String deletedLink=instanceId+","+missiveTitle+","+taskName;

                Schedule schedule= scheduleRepository.findByContent(lastTaskId.toString());
                if(schedule!=null){
                    tf.setUpdateFlag("1");

                }else{
                    tf.setUpdateFlag("0");
                }

                tf.setTaskState(taskState);
                tf.setIntelTime(intelTime);
                tf.setTaskUrl(taskUrl);
                tf.setName(taskName);
                tf.setTaskStartTime(taskStartTime);
                tf.setLastTaskId(lastTaskId);
                tf.setTypeTitle(verTypeTitle);
                tf.setAbbrevType(abbrevType);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                tf.setLastTaskId(latestTaskId);
                int versionNum = fileupc.getMaxMissiveVersion(instanceId,verType);
                tf.setVersionNum(versionNum);
                missiveTitle=missiveTitle==""?"暂无标题":missiveTitle;
                tf.setMissiveTitle(missiveTitle);
                tf.setInfoLink(infoLink);
                tf.setRollbackLink(rollbackLink);
                tf.setDeletedLink(deletedLink);
                tfs.add(tf);
            }
        }
        return  tfs;
    }

    public List<TaskForm> getTaskFormByHistroyTask(List<HistoricTaskInstance> tasks ){
        // logger.info("TransformHistoryTask page start");

        List<TaskForm> tfs =new ArrayList<TaskForm>();
        if (tasks != null) {
            for (HistoricTaskInstance task : tasks) {
                TaskForm tf = new TaskForm();
                String processDeId = task.getProcessDefinitionId();
                Long instanceId =Long.parseLong(task.getProcessInstanceId());
                String taskName = task.getName();
                Long taskId =Long.parseLong(task.getId());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskEndTime=String.valueOf(format.format(task.getEndTime()));
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType="";
                String verType="";
                String verTypeTitle="";
                String missiveTitle="";
                String urgency="";
                String htmlUrl="";
                String taskCondition="";
                String cardTitle="";
                Long lastTaskId=null;
                String missiveNum="";
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verType="missiveReceive";
                    verTypeTitle ="收文流程";
                    cardTitle="阅办稿纸";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    if(mrsc!=null&&mrsc.getTitle()!=null){
                        missiveTitle=mrsc.getTitle();


                    }
                    if(mrsc.getCode()!=null){
                        missiveNum=mrsc.getCode();
                    }
                    else {
                        missiveNum="未设定";
                    }
                    if(mrsc.getUrgencyLevel()!=null) {
                        urgency = mrsc.getUrgencyLevel().getValue();
                    }
                    htmlUrl="/html2pdf/missiveReceive/"+instanceId+"/1";
                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verType="missivePublish";
                    verTypeTitle="发文流程";
                    cardTitle="发文稿纸";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    Missive missive = mp.getMissiveInfo();
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();

                    }
                    if(mp.getMissiveInfo().getMissiveNum()!=null){
                        missiveNum=mp.getMissiveInfo().getMissiveNum();
                    }
                    else {
                        missiveNum="未设定";
                    }
                    if(missive.getUrgentLevel()!=null) {
                        urgency = missive.getUrgentLevel().getValue();
                    }
                    htmlUrl="/missive/missivePublishToPDF/"+instanceId+"/1";
                }


                boolean isFishedProcess=this.actR.isProcessFinished(Long.toString(instanceId));
                if(isFishedProcess){
                    taskCondition="流程已结束";
                    tf.setRollBackFlag("false");
                    List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId.toString()).taskDeleteReason("completed").orderByHistoricTaskInstanceEndTime().desc().list();

                    //return previousTasklist.get(0).getId();
                    if(!previousTasklist.isEmpty()) {
                        lastTaskId= Long.parseLong(previousTasklist.get(0).getId());
                        tf.setPreTaskId(lastTaskId);
                        tf.setLastTaskId(lastTaskId);
                    }

                }else{

//                    Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).singleResult();
                    List task3=this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list();
                    if(task3.size()>0){
                        Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
                        taskCondition=task2.getName();
                        tf.setNextTaskId(task2.getId());
                        lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(task2.getId())));//上一个任务编号 03-04
                        tf.setPreTaskId(lastTaskId);
                        tf.setLastTaskId(lastTaskId);
                        if(taskId.toString().equals(lastTaskId.toString())){
                            tf.setRollBackFlag("true");
                        }else {
                            tf.setRollBackFlag("false");


                        }
                    }
//                    Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
//                    taskCondition=task2.getName();
//                    tf.setNextTaskId(task2.getId());
//                    lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(task2.getId())));//上一个任务编号 03-04
//                    tf.setPreTaskId(lastTaskId);
//                    tf.setLastTaskId(lastTaskId);
//                    if(taskId.toString().equals(lastTaskId.toString())){
//                        tf.setRollBackFlag("true");
//                    }else {
//                        tf.setRollBackFlag("false");
//
//
//                    }

                }
                if(taskCondition.equals("分发阅办")){
                    tf.setRollBackFlag("false");
                }
                if(taskCondition.equals("承办处理")){
                    tf.setRollBackFlag("false");
                }

                List<UploadFlag> schedule= uploadFlagRepository.findListByPlace(instanceId.toString());
                if(schedule.size()>1){
                    tf.setUpdateFlag("1");

                }else{
                    tf.setUpdateFlag("0");
                }


                // String taskid =String.valueOf(taskId);//流程实例第一个任务编号   //0402注释
                // Task task2 = actR.getCurrentTasksByProcessInstanceId(instanceId);
//                if(task2!=null) {
//                    taskid = task2.getId();
                //Long lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(task2.getId())));//上一个任务编号 03-04
//                    if(taskId.toString().equals(lastTaskId.toString())){
//                        tf.setRollBackFlag("true");
//                    }else tf.setRollBackFlag("false");

                tf.setName(taskName);
                tf.setUrgencyLevel(urgency);
                tf.setTaskEndTime(taskEndTime);
                tf.setTypeTitle(verTypeTitle);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                tf.setCardTitle(cardTitle);
                int versionNum = fileupc.getMaxMissiveVersion(instanceId,verType);
                tf.setVersionNum(versionNum);
                tf.setMissiveTitle(missiveTitle);
//                    tf.setPreTaskId(lastTaskId);
//                    tf.setLastTaskId(lastTaskId);
                tf.setHtmlUrl(htmlUrl);
                tf.setTaskCondition(taskCondition);
                tf.setMissiveNum(missiveNum);
                tfs.add(tf);
//                }else{
//
////                    if(String.valueOf(instanceId)!=""){
////                    this.actR.delHistoryProcessInstance(String.valueOf(instanceId),"");
////                    }
//                }
                //  Long TASKID =Long.parseLong(taskid);

                //  logger.info("TransformHistoryTask page end");


            }
        }
        return  tfs;
    }

    public List<TaskForm> getUnfinishedTaskByUser(List<HistoricTaskInstance> tasks ){

        //logger.info("TransformUncompletedHistoryTask page start");

        List<TaskForm> tfs =new ArrayList<TaskForm>();
        if (tasks != null) {
            for (HistoricTaskInstance task : tasks) {
                TaskForm tf = new TaskForm();
                String processDeId = task.getProcessDefinitionId();
                Long instanceId =Long.parseLong(task.getProcessInstanceId());
                String taskName = task.getName();
                Long taskId =Long.parseLong(task.getId());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskEndTime=String.valueOf(format.format(task.getEndTime()));
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType="";
                String verType="";
                String verTypeTitle="";
                String missiveTitle="";
                String urgency="";
                String htmlUrl="";
                String taskCondition="";
                String cardTitle="";
                String taskAssignee="";
                Long lastTaskId=null;
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verType="missiveReceive";
                    verTypeTitle ="收文流程";
                    cardTitle="阅办稿纸";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    if(mrsc!=null&&mrsc.getTitle()!=null){
                        missiveTitle=mrsc.getTitle();

                    }
                    if(mrsc.getUrgencyLevel()!=null) {
                        urgency = mrsc.getUrgencyLevel().getValue();
                    }
                    htmlUrl="/html2pdf/missiveReceive/"+instanceId+"/1";
                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verType="missivePublish";
                    verTypeTitle="发文流程";
                    cardTitle="发文稿纸";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    Missive missive = mp.getMissiveInfo();
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                    if(missive.getUrgentLevel()!=null) {
                        urgency = missive.getUrgentLevel().getValue();
                    }
                    htmlUrl="/missive/missivePublishToPDF/"+instanceId+"/1";
                }

                boolean isFishedProcess=this.actR.isProcessFinished(Long.toString(instanceId));
                if(isFishedProcess){
                    taskCondition="流程已结束";
                    tf.setRollBackFlag("false");
                    List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId.toString()).orderByHistoricTaskInstanceEndTime().desc().list();

                    if(!previousTasklist.isEmpty()) {
                        lastTaskId= Long.parseLong(previousTasklist.get(0).getId());
                        tf.setPreTaskId(lastTaskId);
                        tf.setLastTaskId(lastTaskId);
                    }

                }else{

                   // Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).singleResult();

                    List task3=this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list();
                    if(task3.size()>0){
                        Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
                        taskCondition=task2.getName();
                        tf.setNextTaskId(task2.getId());
                        lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(task2.getId())));//上一个任务编号 03-04
                        tf.setPreTaskId(lastTaskId);
                        tf.setLastTaskId(lastTaskId);
                        if(taskId.toString().equals(lastTaskId.toString())){
                            tf.setRollBackFlag("true");
                        }else {
                            tf.setRollBackFlag("false");


                        }
                    }
//                    Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
//                    taskCondition=task2.getName();
//                    tf.setNextTaskId(task2.getId());
//                    lastTaskId=Long.parseLong(actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(task2.getId())));//上一个任务编号 03-04
//                    tf.setPreTaskId(lastTaskId);
//                    tf.setLastTaskId(lastTaskId);
//                    if(taskId.toString().equals(lastTaskId.toString())){
//                        tf.setRollBackFlag("true");
//                    }else {
//                        tf.setRollBackFlag("false");
//
//
//                    }

                }
                if(taskCondition.equals("分发阅办")){
                    tf.setRollBackFlag("false");
                }
                if(taskCondition.equals("承办处理")){
                    tf.setRollBackFlag("false");
                }

                List<Schedule> schedule= scheduleRepository.findListByPlace(instanceId.toString());
                if(schedule.size()>1){
                    tf.setUpdateFlag("1");

                }else{
                    tf.setUpdateFlag("0");
                }

                List task3=this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list();
                Task task2=null;
                if(task3.size()>0){
                   task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
                }
//                Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).list().get(0);
                if(task2!=null){
                    User user=userDAO.findByUserName(task2.getAssignee());
                    if(user!=null){
                        tf.setTaskAssName(user.getName());

                    }else{
                        tf.setTaskAssName("未指定");
                    }
                }

                tf.setName(taskName);
                tf.setUrgencyLevel(urgency);
                tf.setTaskEndTime(taskEndTime);
                tf.setTypeTitle(verTypeTitle);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                tf.setCardTitle(cardTitle);
                int versionNum = fileupc.getMaxMissiveVersion(instanceId,verType);
                tf.setVersionNum(versionNum);
                tf.setMissiveTitle(missiveTitle);

                tf.setHtmlUrl(htmlUrl);
                tf.setTaskCondition(taskCondition);
                if(!isFishedProcess){

                    tfs.add(tf);
                }


               // logger.info("TransformUncompletedHistoryTask page start");



            }
        }
        return  tfs;
    }


    public void DeleteByHistroyTask(List<HistoricTaskInstance> tasks){
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        if (tasks != null) {
            for (HistoricTaskInstance task : tasks) {
                TaskForm tf = new TaskForm();
                String processDeId = task.getProcessDefinitionId();
                Long instanceId =Long.parseLong(task.getProcessInstanceId());
                String taskName = task.getName();
                Long taskId =Long.parseLong(task.getId());

                ProcessInstance instance = this.runtimeService.createProcessInstanceQuery().processInstanceId(instanceId.toString()).singleResult();
                if(instance==null){
                    HistoricProcessInstance Historyinstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId.toString()).singleResult();
                   // Historyinstance.setreason
                    this.actService.delProcessInstance(instanceId.toString(),"");

                }
            }
        }
    }



    //查询对应的处理历史公文
    public List<TaskForm> filterTaskFormByHistroyTask(List<HistoricTaskInstance> tasks, String data){
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        if (tasks != null) {
            for (HistoricTaskInstance task : tasks) {
                TaskForm tf = new TaskForm();
                String processDeId = task.getProcessDefinitionId();
                Long instanceId =Long.parseLong(task.getProcessInstanceId());
                String taskName = task.getName();
                Long taskId =Long.parseLong(task.getId());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskEndTime=String.valueOf(format.format(task.getEndTime()));
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType="";
                String verType="";
                String verTypeTitle="";
                String missiveTitle="";
                String urgency="";
                String htmlUrl="";
                String cardTitle="";
                String taskCondition="";
                String missiveNum="";

                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verType="missiveReceive";
                    verTypeTitle ="收文流程";
                    cardTitle="阅办稿纸";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    if(mrsc!=null&&mrsc.getTitle()!=null){
                        missiveTitle=mrsc.getTitle();

                    }
                    if(mrsc.getCode()!=null){
                        missiveNum=mrsc.getCode();
                    }
                    else {
                        missiveNum="未设定";
                    }
                    if(mrsc.getUrgencyLevel()!=null) {
                        urgency = mrsc.getUrgencyLevel().getValue();
                    }
                    htmlUrl="/html2pdf/missiveReceive/"+instanceId+"/1";
                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verType="missivePublish";
                    verTypeTitle="发文流程";
                    cardTitle="发文稿纸";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    Missive missive = mp.getMissiveInfo();
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                    if(mp.getMissiveInfo().getMissiveNum()!=null){
                        missiveNum=mp.getMissiveInfo().getMissiveNum();
                    }
                    else {
                        missiveNum="未设定";
                    }
                    if(missive.getUrgentLevel()!=null) {
                        urgency = missive.getUrgentLevel().getValue();
                    }
                    htmlUrl="/missive/missivePublishToPDF/"+instanceId+"/1";
                }


                if(missiveTitle.contains(data)||missiveNum.contains(data)) {
                    Task task2 = this.taskService.createTaskQuery().active().processInstanceId(Long.toString(instanceId)).singleResult();
                    if(task2!=null){
                        taskCondition=task2.getName();
                        tf.setTaskCondition(taskCondition);

                        User user=userDAO.findByUserName(task2.getAssignee());
                        if(user!=null){
                            tf.setTaskAssName(user.getName());

                        }else{
                            tf.setTaskAssName("未指定");
                        }
                    }

//                    String taskid = String.valueOf(taskId);//流程实例第一个任务编号
//                    Task task2 = actR.getCurrentTasksByProcessInstanceId(instanceId);
//                    if (task2 != null) {
//                        taskid = task2.getId();
                        Long lastTaskId = taskId;//上一个任务编号 03-04
                        List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId.toString()).orderByHistoricTaskInstanceEndTime().desc().list();

                    //return previousTasklist.get(0).getId();
                        if(!previousTasklist.isEmpty()) {
                            lastTaskId= Long.parseLong(previousTasklist.get(0).getId());
                        }
                        tf.setName(taskName);
                        tf.setUrgencyLevel(urgency);
                        tf.setTaskEndTime(taskEndTime);
                        tf.setTypeTitle(verTypeTitle);
                        tf.setCardTitle(cardTitle);
                        tf.setProcessDefinitionId(processDeId);
                        tf.setMissiveType(instanceType);
                        int versionNum = fileupc.getMaxMissiveVersion(instanceId, verType);
                        tf.setVersionNum(versionNum);
                        tf.setMissiveTitle(missiveTitle);
                        tf.setPreTaskId(lastTaskId);
                        tf.setLastTaskId(lastTaskId);
                        tf.setHtmlUrl(htmlUrl);
                        tf.setMissiveNum(missiveNum);
                        tfs.add(tf);
//                    } else {
//
//                        //this.actR.delProcessInstance(String.valueOf(instanceId), "");
//                    }
                    //  Long TASKID =Long.parseLong(taskid);

                }

            }
        }
        return  tfs;
    }


    //删除公文
    @RequestMapping(value="/deleteMissive/{instanceId}")
    public void deleteMissive(@AuthenticationPrincipal User currentUser,@PathVariable long instanceId){
        this.actR.delProcessInstance(String.valueOf(instanceId),"");
        String taskComment=currentUser.getName();
        taskComment="删除by"+taskComment;
       // this.actService.addTaskComment(String.valueOf(instanceId),taskComment);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime=String.valueOf(format.format(new Date()));

        this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+instanceId+"','"+nowTime+"','"+currentUser.getUserName()+"','"+instanceId+"','delete','"+taskComment+"')");


    }

    @RequestMapping(value="/deleteMissiveByAdmin/{instanceId}/{type}")
    public void deleteMissiveByAuthority(@AuthenticationPrincipal User currentUser,@PathVariable long instanceId,@PathVariable String type){
        String ss="";
        if(type.equals("发文流程")){
            MissivePublish mp =mpr.findByProcessID(instanceId);
            Missive missive = mp.getMissiveInfo();
            missive.setMissiveNum("");
            missiveRepository.save(missive);

        }else if(type.equals("收文流程")){
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
            mrsc.setCode("");
            mrscr.save(mrsc);
        }
        this.actR.delProcessInstance(String.valueOf(instanceId),"deleteTask");
        String taskComment=currentUser.getName();
        taskComment="删除by"+taskComment;
        this.actService.addTaskComment(String.valueOf(instanceId),taskComment);
    }




    @RequestMapping(value="/searchByword/getMissiveList")
    public List<TaskForm> searchByKeyvalue(@AuthenticationPrincipal User currentUser,@RequestParam String data){

    List<HistoricTaskInstance> tasklist =  this.actService.getDoneProcessByUserName(currentUser.getUserName());
    List<TaskForm> lftDone=filterTaskFormByHistroyTask(tasklist,data);

    return lftDone;


    }

    @RequestMapping(value="/searchByword/getUnfinishedList")
    public List<TaskForm> searchByKeyValue(@AuthenticationPrincipal User currentUser,@RequestParam String data){

        List<HistoricTaskInstance> tasklist =  this.actService.getDoneProcessByUserName(currentUser.getUserName());
        List<TaskForm> lftDone=filterTaskFormByHistroyTask(tasklist,data);

        return lftDone;


    }


    @RequestMapping(value="/searchByword/getPublishList")
    public List<TaskForm> searchByKeyValue(@RequestParam String data,@AuthenticationPrincipal User currentUser){

        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery().sql("    select * from oa4.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa4.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa4.act_hi_procinst d where d.START_ACT_ID_='StartPublish' and (d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1')) order by f.END_TIME_ DESC").list();

//        List<TaskForm> publishDone=filterTaskFormByHistroyTask(historicTaskInstanceList,data);
        List<TaskForm> publishDone = filterTaskFormByHistroyTask(historicTaskInstanceList,data);


        return publishDone;


    }

    @RequestMapping(value="/searchByword/getHistoryMissiveList")
    public List<TaskForm> searchALLByKeyvalue(@AuthenticationPrincipal User currentUser,@RequestParam String data){



        List<HistoricTaskInstance> historicTaskInstanceList = this.historyService.createNativeHistoricTaskInstanceQuery().sql("select * from oa4.act_hi_taskinst f where f.ID_ IN (select max(e.ID_) from  oa4.act_hi_taskinst e group by PROC_INST_ID_) and f.PROC_INST_ID_  IN(  select  d.PROC_INST_ID_ from oa4.act_hi_procinst d where  d.END_ACT_ID_='End' or d.END_ACT_ID_='endevent1' or d.END_ACT_ID_='EndNewsReview') order by f.END_TIME_ DESC").list();


            List<TaskForm> lftDone = filterTaskFormByHistroyTask(historicTaskInstanceList,data);


        return lftDone;


    }


    @RequestMapping(value="/taskRollBack/{taskId}/{delegator}/{instantId}")
    public String executeTaskRollBack1(@AuthenticationPrincipal User currentUser,@PathVariable Long taskId,@PathVariable String delegator,@PathVariable String instantId){

        List<HistoricTaskInstance> taskList =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceStartTime().desc().list();

        String taskID=taskList.get(0).getId();
        this.managementService.executeCommand(new RollbackTaskCmd(String.valueOf(taskID)));
        String rollbackText="rollback:"+delegator;

        String userId=currentUser.getUserName();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String taskStartTime = String.valueOf(format.format(new Date()));
        String nowTime=String.valueOf(format.format(new Date()));

        this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+taskID+"','"+nowTime+"','"+userId+"','"+taskId+"','"+instantId+"','rollback','"+delegator+"')");

        return "true";


    }



    @RequestMapping(value="/taskRollBack/{taskId}/{delegator}")
    public String executeTaskRollBack(@AuthenticationPrincipal User currentUser,@PathVariable Long taskId,@PathVariable String delegator){
        String processid="";
        Task temptask = this.taskService.createTaskQuery().taskId(String.valueOf(taskId)).singleResult();

        if(temptask!=null) {
            processid = temptask.getProcessInstanceId();


        }

        this.managementService.executeCommand(new RollbackTaskCmd(String.valueOf(taskId)));
        String rollbackText="rollback:"+delegator;

        String userId=currentUser.getUserName();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String taskStartTime = String.valueOf(format.format(new Date()));
        String nowTime=String.valueOf(format.format(new Date()));

        this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+taskId+"','"+nowTime+"','"+userId+"','"+taskId+"','"+processid+"','rollback','"+delegator+"')");

        return "true";


    }


    @RequestMapping(value="/taskDelete2/{processId2}/{deleteReason2}/{taskId2}/{type2}")
    public String executeTaskDelete2(@AuthenticationPrincipal User currentUser,@PathVariable Long processId2,@PathVariable String deleteReason2,@PathVariable Long taskId2,@PathVariable String type2){


        String userId=currentUser.getUserName();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String taskStartTime = String.valueOf(format.format(new Date()));
        String nowTime=String.valueOf(format.format(new Date()));

        this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+processId2+"','"+nowTime+"','"+userId+"','"+taskId2+"','"+processId2+"','delete','"+deleteReason2+"')");

        String ss="";
        if(type2.equals("发文流程")){
            MissivePublish mp =mpr.findByProcessID(processId2);
            Missive missive = mp.getMissiveInfo();
            missive.setMissiveNum("");
            missiveRepository.save(missive);

        }else if(type2.equals("收文流程")){
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(processId2));
            mrsc.setCode("");
            mrscr.save(mrsc);
        }
        this.actR.delHistoryProcessInstance(String.valueOf(processId2),"");


        return "true";



    }

    @RequestMapping(value="/taskDelete/{processId}/{deleteReason}/{taskId1}/{type}")
    public String executeTaskDelete(@AuthenticationPrincipal User currentUser,@PathVariable Long processId,@PathVariable String deleteReason,@PathVariable Long taskId1,@PathVariable String type){


        String userId=currentUser.getUserName();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String taskStartTime = String.valueOf(format.format(new Date()));
        String nowTime=String.valueOf(format.format(new Date()));

        this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+processId+"','"+nowTime+"','"+userId+"','"+taskId1+"','"+processId+"','delete','"+deleteReason+"')");

        String ss="";
        if(type.equals("发文流程")){
            MissivePublish mp =mpr.findByProcessID(processId);
            Missive missive = mp.getMissiveInfo();
            missive.setMissiveNum("");
            missiveRepository.save(missive);

        }else if(type.equals("收文流程")){
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(processId));
            mrsc.setCode("");
            mrscr.save(mrsc);
        }
        this.actR.delProcessInstance(String.valueOf(processId),"");


        return "true";



    }



    @RequestMapping(value="/getCurrentNode/{taskId}/{instantId}/{delegator}/{jumpTo}/{taskName}/{jumpToNode}")
    public List<NextTask> getCurrentNode(@AuthenticationPrincipal User currentUser,@PathVariable Long taskId,@PathVariable Long instantId,@PathVariable String delegator,@PathVariable String jumpTo,@PathVariable String taskName,@PathVariable String jumpToNode){
//        String nodeName="";
//        String nodeId="";
//        List<NextTask> nodeData=null;
//
//        ProcessInstance process = this.runtimeService.createProcessInstanceQuery().processInstanceId(String.valueOf(instantId)).singleResult();
//        if(process!=null) {
//            BpmnModel model = this.repositoryService.getBpmnModel(process.getProcessDefinitionId());
//
//
//            Task task = this.actR.getCurrentTask(Integer.parseInt(String.valueOf(taskId)));
//            if (task != null) {
//                //FlowElement flowTask = model.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
//                FlowElement flowTask = model.getFlowElement(task.getTaskDefinitionKey());
//                 nodeId=flowTask.getId();
//            }
//        }
//
//
//        this.managementService.executeCommand(new JumpCmd(nodeId,delegator));
//
//        return nodeData;



        try {
            tfcs.jump(instantId.toString(),jumpTo);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTime=String.valueOf(format.format(new Date()));
            String task=jumpToNode+"|"+taskName;
           // String uu="INSERT INTO oa4.act_hi_comment(ID_,TYPE_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+taskId+"','"+task+"','"+nowTime+"','"+currentUser.getUserName()+"','"+taskId+"','"+instantId+"','delegator','"+delegator+"')";
           //SELECT ID_,EXECUTION_ID_ FROM oa4.act_ru_task where PROC_INST_ID_='362533';
            List resultList=this.jdbcTemplate.queryForList("SELECT oa4.act_ru_task.ID_ ,oa4.act_ru_task.PROC_INST_ID_  FROM oa4.act_ru_task where oa4.act_ru_task.PROC_INST_ID_="+instantId);
            //String runId= resultList.get(0);
            //String runProId=resultList.get(1);
            String runId;
            String runProId;
            Iterator it = resultList.iterator();
            while(it.hasNext()) {
                Map userMap = (Map) it.next();
                runId=userMap.get("ID_").toString();
                runProId=userMap.get("PROC_INST_ID_").toString();
                if(!runId.equals(runProId)){
                    this.jdbcTemplate.update("update oa4.act_ru_task SET oa4.act_ru_task.EXECUTION_ID_ = '"+instantId+"' where oa4.act_ru_task.PROC_INST_ID_="+instantId);
                }
            }



           this.jdbcTemplate.update("update oa4.act_ru_execution SET oa4.act_ru_execution.IS_ACTIVE_ = 0 where oa4.act_ru_execution.PROC_INST_ID_="+instantId);

           this.jdbcTemplate.update("update oa4.act_ru_execution SET oa4.act_ru_execution.ACT_ID_ = '"+jumpTo+"',oa4.act_ru_execution.IS_ACTIVE_ = 1 where oa4.act_ru_execution.PROC_INST_ID_=oa4.act_ru_execution.ID_ and oa4.act_ru_execution.PROC_INST_ID_="+instantId);

            this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TYPE_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+taskId+"','"+task+"','"+nowTime+"','"+currentUser.getUserName()+"','"+taskId+"','"+instantId+"','delegator','"+delegator+"')");

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<NextTask> nodeData=null;
        return  nodeData;

    }


    @RequestMapping(value="/getCurrentTaskFlag/{taskId}/{instantId}")
    public String getCurrentandLastTask(@AuthenticationPrincipal User currentUser,@PathVariable Long taskId,@PathVariable Long instantId){


        String NewTaskID="";
        String lastTaskId="";
        Task task2 = this.taskService.createTaskQuery().active().processInstanceId(String.valueOf(instantId)).singleResult();
        if(task2!=null) {
            NewTaskID=task2.getId().toString();
           lastTaskId=actS.getPreviousTaskIDByCurrentTaskID(String.valueOf(NewTaskID));//上一个任务编号 03-04

            }
        if(taskId.toString().equals(lastTaskId)){
            return "true";
        }
        else return "false";


    }


    @RequestMapping(value="/recoverTask/{instantId}")
    public String recoverTask(@AuthenticationPrincipal User currentUser,@PathVariable  String instantId){

        List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceEndTime().desc().list();
        if(!previousTasklist.isEmpty()) {
            HistoricTaskInstance historicTaskInstance=  previousTasklist.get(0);
            Task task=taskService.newTask();


            return "true";
        }else return "false";


    }




}
