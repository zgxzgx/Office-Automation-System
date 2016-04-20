package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.api.MyMissiveController;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by seky on 14/12/9.
 */
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class MissiveDelContoller {

    @Autowired
    private ActivitiService actService;
    @Autowired
    private ActivitiService actS;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;

    @Autowired
    private FileUploadController fileupc;
    @Autowired
    private UserRepository usrR;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MyMissiveController mmc;
    @Autowired JdbcTemplate jdbcTemplate;


    private final static Logger logger = LoggerFactory.getLogger(MissiveDelContoller.class);


    @RequestMapping(value="/xtsz/{itemActive}/{pageNum}/{pageNum1}")
    public String MissiveDeletion(Model model,@AuthenticationPrincipal User currentUser,@PathVariable String itemActive,@PathVariable Integer pageNum,@PathVariable Integer pageNum1){

        model.addAttribute("user", currentUser);
        double pageTotal=1;
        if(pageNum==null){
            pageNum=1;
        }
        if (pageNum1== null)
        {
            pageNum1 = 1;
        }
        int taskDonePagesNum=1;
        int taskIngPagesNum=1;

        List<Task> result2= this.taskService.createNativeTaskQuery()
                .sql("SELECT * FROM oa4.act_ru_task t order by t.create_time_ desc " ).list();
       // PageableTaskList result = this.actService.getAllCurrentTaskList(10, pageNum);//待办的任务
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
        ltfIng = getTaskFormByTask(result2);

        PageableHistoryTaskList unfinishedTask =this.actService.getUncompletedTasksByUser(currentUser,13,pageNum);//未办任务

//        if(unfinishedTask.getTasklist()!=null){
//            ltfIng=mmc.getUnfinishedTaskByUser(unfinishedTask.getTasklist());
//        }
        taskIngPagesNum=unfinishedTask.getPageTotal()==0?1:unfinishedTask.getPageTotal();
        PageableHistoryTaskList historyTasks = this.actService.getAllProcessDone(currentUser.getUserName(), 13, pageNum1);//完成的任务
        List<TaskForm> lftDone=new ArrayList<TaskForm>();
        if (historyTasks.getTasklist() != null) {
            lftDone = mmc.getTaskFormByHistroyTask(historyTasks.getTasklist());
        }

        pageTotal=historyTasks.getPageTotal();



        taskDonePagesNum=historyTasks.getPageTotal()==0?1:historyTasks.getPageTotal();


//        String jumpReason="";
//        List resultList=this.jdbcTemplate.queryForList("SELECT a.ACTION_ FROM oa4.act_hi_comment as a where a.ACTION_ !='备注' and a.ACTION_ !='rollback'");
//
//        Iterator it = resultList.iterator();
//        while(it.hasNext()) {
//            Map userMap = (Map) it.next();
//
//            jumpReason+=userMap.get("NAME_").toString()+","+currentUser+":"+userMap.get("MESSAGE_").toString()+"\n";
//
//        }
//        model.addAttribute("jumpReason", jumpReason);


        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",3);
        model.addAttribute("LookPage",pageNum);
        model.addAttribute("activeitem",itemActive);
        model.addAttribute("tasksDone",lftDone);
        model.addAttribute("DonePage",pageNum1);
        model.addAttribute("PageTotal",pageTotal);
        //model.addAttribute("lastTaskId", ltfIng);


        model.addAttribute("taskDoneTotalNum",taskDonePagesNum);

        return "missiveDel";
    }

    public List<TaskForm> getTaskFormByTask(List<Task> tasks){
        List<TaskForm> tfs =new ArrayList<TaskForm>();
        User usrInfo=null;
        String assignee="";
        int delayNum=0;
        String delayWarm="";
        if (tasks != null) {
            for (Task task : tasks) {
                TaskForm tf = new TaskForm();
                usrInfo=usrR.getUserInfoByUserName(task.getAssignee());
                if(usrInfo!=null){
                    assignee=usrInfo.getName();//获取用户姓名
                    delayNum=usrInfo.getDelaynum();//获取延误设定值，单位小时
                    delayWarm=usrInfo.getDelayWarm();//获取延误设定类型
                }
                String processDeId = task.getProcessDefinitionId();
                String taskName = task.getName();
                String description=task.getDescription();     //01-12孙乐 新添加字段


                Long instanceId = Long.parseLong(task.getProcessInstanceId());

                Long taskId = Long.parseLong(task.getId());
                //获取前面的任务信息
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskStartTime = String.valueOf(format.format(task.getCreateTime()));
                String nowTime=String.valueOf(format.format(new Date()));
                String intelTime="";
                try {
                    Date date = format.parse(taskStartTime);
                    Date now = format.parse(nowTime);
                    long l = now.getTime() - date.getTime();
                    //long day = l / (24 * 60 * 60 * 1000);
                    long hour = l / (60 * 60 * 1000);//相差小时
                    //long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    //long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                    intelTime="" + hour + "小时";
                }
                catch (Exception ex){
                    logger.error("getTaskFormByTask exception:",ex);

                    ex.printStackTrace();
                }
                //查询当前任务名称
                tf.setId(taskId);
                tf.setProcessInstanceId(instanceId);
                String instanceType;
                String verType="";
                String verTypeTitle="";
                String missiveTitle="";
                if(processDeId.contains("ReceiptId")){
                    instanceType="missiveReceive";
                    verTypeTitle ="收文流程";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                    missiveTitle=mrsc.getTitle()==null?"":mrsc.getTitle();
                }
                else if(processDeId.contains("PublishMissiveId")){
                    instanceType="missivePublish";
                    verTypeTitle="发文流程";
                    MissivePublish mp =mpr.findByProcessID(instanceId);
                    if(mp!=null&&mp.getMissiveTittle()!=null){
                        missiveTitle=mp.getMissiveTittle();
                    }
                }

                else{
                    instanceType="";
                }
                tf.setIntelTime(intelTime);
                tf.setName(taskName);
                tf.setTaskAssName(assignee);
                tf.setDelayNum(delayNum+"小时");
                tf.setDelayWarm(getNameByDelayWarm(delayWarm));
                tf.setTaskStartTime(taskStartTime);
                tf.setTypeTitle(verTypeTitle);
                tf.setProcessDefinitionId(processDeId);
                tf.setMissiveType(instanceType);
                tf.setMissiveTitle(missiveTitle);
                tf.setDescription(description);
                missiveTitle=missiveTitle==""?"暂无标题":missiveTitle;
                tf.setMissiveTitle(missiveTitle);
                tfs.add(tf);
            }
        }
        return  tfs;
    }
    //根据延误类型返回延误类型名称
    private String getNameByDelayWarm(String delayWarm){
        if (delayWarm.equals("m")){
            return "短信提醒";
        }else if (delayWarm.equals("e")){
            return "邮件提醒";
        }else if (delayWarm.equals("n")){
            return "请电话联系";
        }
        return "";
    }



}
