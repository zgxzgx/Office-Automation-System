package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.*;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.api.MyMissiveController;
import jodd.util.Base64;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by TISSOT on 2014/7/31.
 */
@RequestMapping(value="")
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class templatesControl {

    @Value("${spring.main.receiptid}")
    int receiptid;
    @Autowired
    private MissiveFieldRepository mfr;
    @Autowired
    private TaskNameRepository tnr;
    @Autowired
    private ProcessTypeRepository ptr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private MissiveRecSeeCardRepository misssCardR;
    @Autowired
    private MyMissiveController mmc;
    @Autowired
    private UserDAO ud;
    @Autowired
    private MissiveReceiveFunction missF;
    @Autowired
    private MissiveReceiveTaskDealerRepository mrtdr;
    @Autowired
    private DIYphaseController diyPh;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private SendConfigRepository sendService;
    @Autowired
    private AuthoritiesRepository authorService;
    @Autowired
    private FileUploadController fileupc;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;




    MissivePublishFunction mpf=new MissivePublishFunction();
    private final static Logger logger = LoggerFactory.getLogger(templatesControl.class);

    @ModelAttribute(value = "userbase64")
    public String addUserBase64toPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()== org.springframework.security.core.userdetails.User.class )
        {
            User myUser = (User) temp;
            String base64Code = "Basic " + Base64.encodeToString(myUser.getUsername() + ":" + myUser.getUsername());
            return base64Code;
        }
        else
            return "";

    }

    @ModelAttribute(value = "user")
    public User addUsertoPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()==User.class )
        {
            return (User)temp;
        }
        else
            return null;

    }

    @RequestMapping(value="/{processDeID:[A-Za-z]*:[0-9]*:[0-9]*}/{instanceid:[0-9]*}/{taskid:[0-9]*}")
    public String newIn2ReceiptId(@PathVariable String processDeID,@PathVariable int instanceid,@PathVariable int taskid,Model model,@AuthenticationPrincipal User currentUser)
    {
        if(processDeID.contains("Receipt")) {
            //UserFrom userFrom=mpf.userToUserForm(currentUser);
            model.addAttribute("ud", currentUser);


            model.addAttribute("processDeId", processDeID);
            model.addAttribute("instanceId", instanceid);
            model.addAttribute("taskId", taskid);


            String name = actService.getCurrentTask(taskid).getName();//获取当期任务名称
            String assignVal = actService.getCurrentTask(taskid).getAssignee();
            model.addAttribute("currentTaskDealer",assignVal);
            List<Map<String, Object>> userll=ud.getAllUserNameAndGroupName();
            model.addAttribute("userDataSource",mpf.userToUserForm_Name_UserName(userll));

            //通过任务名称找到可编辑控件
            ProcessType pt = ptr.findByName("收文流程");
            TaskName tn = tnr.findByName(name, pt);
            List<MissiveField> lmf = mfr.getEditControl(tn);

            List controls = new ArrayList();
            for (MissiveField mf : lmf) {
                controls.add(mf.getInputId());
            }

            String remarkList="";
            //List resultList=this.jdbcTemplate.queryForList("SELECT USER_ID_,MESSAGE_ FROM oa3.act_hi_comment where ACTION_='备注' and PROC_INST_ID_="+instanceid);
            //List resultList=this.jdbcTemplate.queryForList("SELECT a.MESSAGE_,a.USER_ID_ , b.NAME_,a.TIME_ FROM oa3.act_hi_comment as a,oa3.act_hi_taskinst as b where a.PROC_INST_ID_=b.PROC_INST_ID_ and a.ACTION_='备注' and a.PROC_INST_ID_="+instanceid);
            List resultList=this.jdbcTemplate.queryForList("SELECT a.MESSAGE_,a.TASK_ID_,a.USER_ID_ , b.NAME_,a.TIME_ FROM oa3.act_hi_comment as a,oa3.act_hi_taskinst as b where a.Task_ID_=b.ID_ and a.ACTION_='备注' and a.PROC_INST_ID_="+instanceid);

            Iterator it = resultList.iterator();
            while(it.hasNext()) {
                Map userMap = (Map) it.next();
//                String userName= ud.findByUserName(userMap.get("USER_ID_").toString()).getName();
//                remarkList+=userName+":"+userMap.get("MESSAGE_").toString()+"\n";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
                String nowTime=format.format(userMap.get("TIME_"));
                String userName=ud.findByUserName(userMap.get("USER_ID_").toString()).getName();
                remarkList+=userMap.get("NAME_").toString()+","+nowTime+","+userName+":"+userMap.get("MESSAGE_").toString()+"\n"+"+";

            }
            model.addAttribute("remarkList", remarkList);

            MissiveRecSeeCard missCard = misssCardR.getMissData(String.valueOf(instanceid));
            model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
            List<String> missiveCodes = misssCardR.getMissiveCode(String.valueOf(instanceid));//获取收文号
            model.addAttribute("exsistCodes",missiveCodes);

            model.addAttribute("taskName",name);//任务名称
            MissiveReceiveTaskDealer mrtd = mrtdr.getTaskDealer(Long.parseLong(String.valueOf(instanceid)));
            MissiveReceiveTaskDealerData mrtdd =missF.missiveDealerToDealerData(mrtd);
            model.addAttribute("taskAssignee",mrtdd);
//            if(!name.equals("承办处理")) {
                List<Map<String, ? extends Object>> actVars = actService.getNextTaskInfo(String.valueOf(instanceid), taskid);//获取下一步任务
                model.addAttribute("nextAvailableTasks", actVars);
//           }
            model.addAttribute("editControl", controls);

            //自定义短语
            List<String> phases =diyPh.getPhaseByUserId(currentUser.getId());
            model.addAttribute("phases",phases);

            //任务执行人表



            MissiveReceiveRender missiveReceiveForm=missF.MissiveReceive2MissiveReceiveRender(missCard,name);

            int maxMV=0;
            if(missiveReceiveForm.getMissiveInfo()!=null&&missiveReceiveForm.getMissiveInfo().versions!=null){
                List<MissiveVersionFrom> mvfs=missiveReceiveForm.getMissiveInfo().versions;
                for(int i=0;i<mvfs.size()-1;i++){
                    if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                        maxMV=i;
                    }
                    else maxMV=i+1;
                }
                List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
                mvform.add(mvfs.get(maxMV));
                missiveReceiveForm.getMissiveInfo().setVersions(mvform);
            }
            model.addAttribute("missiveReceiveForm", missiveReceiveForm);
            if (missiveReceiveForm.getLeaderInstruct()!=null)
                model.addAttribute("isNewImgsize",missiveReceiveForm.getLeaderInstruct().id>this.receiptid);
            else
                model.addAttribute("isNewImgsize",true);

            logger.info(currentUser.userName + " visited missive receive page PROC_INST_ID="+instanceid+" TASKID="+taskid);
            return "ReceiptId";
        }

        else if(processDeID.contains("Cable")){
            return "";
        }
        else{
            return "";
        }
    }



    //新建流程
    @RequestMapping(value="/xjlc")
    public String html2xjlc(Model model,@AuthenticationPrincipal User currentUser)
    {

        User usr = this.ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(usr);
        List<Map<String, Object>> userConfigList=jdbcTemplate.queryForList("SELECT value FROM oa3.user_config where name='ExternalNewsReceiver' ");
        String userNames=userConfigList.get(0).get("value").toString();
        model.addAttribute("userNames",userNames);
        model.addAttribute("ud", userFrom);
        return "newInstance";
    }

    //我的任务
    @RequestMapping(value="/dbzx/{pageNum}")
    public String html2dbzx(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum){

        String authorflag="false";
        User usr = this.ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(usr);
        model.addAttribute("ud", userFrom);

        if(pageNum==null||pageNum<1){
            pageNum=1;
        }
//        PageableTaskList result = this.actService.getCurrentTasksByUser(currentUser, 18, pageNum);//待办的任务
//        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
//        if (result.getTasklist() != null) {
//            ltfIng = mmc.getTaskFormByTask(result.getTasklist());
//        }

        List<Task> tasklist =  this.actService.getCurrentTasksByUser(currentUser);
        List<TaskForm> ltfIng=mmc.getTaskFormByTask(tasklist);


        List<Authorities> authorities=authorService.getAuthoritiesListByuserID(currentUser.getId());
        if(authorities!=null){
             authorflag="true";
        }


        //int taskIngPagesNum=result.getPageTotal()==0?1:result.getPageTotal();
        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",3);
        model.addAttribute("LookPage",pageNum);
        model.addAttribute("AuthorityFlag",authorflag);

        return "taskList";
    }


//    //处室任务
//    @RequestMapping(value="/csrw/{pageNum}")
//    public String html2csrw(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum){
//
//        String authorflag="false";
//        User usr = this.ud.findById(currentUser.getId());
//        UserFrom userFrom=mpf.userToUserForm(usr);
//        model.addAttribute("ud", userFrom);
//
//        if(pageNum==null||pageNum<1){
//            pageNum=1;
//        }
//
//
////        List<Task> tasklist =  this.actService.getCurrentTasksByUser(currentUser);
////        List tasklist =  jdbcTemplate.queryForList("SELECT * FROM oa3.act_ru_task where ASSIGNEE_ IN (SELECT a.username FROM oa3.users a where group_id =2)");
//        List<TaskForm> ltfIng=mmc.getTaskFormByTaskList(currentUser);
//
//
//        List<Authorities> authorities=authorService.getAuthoritiesListByuserID(currentUser.getId());
//        if(authorities!=null){
//            authorflag="true";
//        }
//
//
//        //int taskIngPagesNum=result.getPageTotal()==0?1:result.getPageTotal();
//        model.addAttribute("tasksIng", ltfIng);
//        model.addAttribute("taskIngTotalNum",3);
//        model.addAttribute("LookPage",pageNum);
//        model.addAttribute("AuthorityFlag",authorflag);
//
//        return "depTaskList";
//    }

    //主页
    @RequestMapping(value="/index")
    public String html2Index(Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("ud",currentUser);
        PageableTaskList result = this.actService.getCurrentTasksByUser(currentUser, 10, 1);//待办的任务
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
        if (result!=null&&result.getTasklist().size()!=0&&result.getTasklist() != null) {
            ltfIng = mmc.getTaskFormByTask(result.getTasklist());
        }
        model.addAttribute("tasksIng", ltfIng);
        String authorflag="false";
        List<Authorities> authorities=authorService.getAuthoritiesListByuserID(currentUser.getId());
        if(authorities!=null){
            authorflag="true";
        }
        model.addAttribute("AuthorityFlag",authorflag);

        return "index";
    }
    @RequestMapping("/")
    public String index(Model model,@AuthenticationPrincipal User currentUser)
    {
        model.addAttribute("ud",currentUser);
        PageableTaskList result = this.actService.getCurrentTasksByUser(currentUser, 10, 1);//待办的任务
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
        if (result!=null&&result.getTasklist().size()!=0&&result.getTasklist() != null) {
            ltfIng = mmc.getTaskFormByTask(result.getTasklist());
        }
        model.addAttribute("tasksIng", ltfIng);
        String authorflag="false";
        List<Authorities> authorities=authorService.getAuthoritiesListByuserID(currentUser.getId());
        if(authorities!=null){
            authorflag="true";
        }
        model.addAttribute("AuthorityFlag",authorflag);
        return "index";
    }

    @RequestMapping(value="/test")
    public String html2test(){
        return "missivePublish";
    }
    @RequestMapping(value="testFax")
    public String html2fax()
    {
        return "faxCablePublish";
    }
    @RequestMapping(value="/upload/{file}")
    public String downFile(@PathVariable String file){
        return "upload/"+file;
    }
   //静态页面
   @RequestMapping(value="/html2pdf/missiveReceive/{instanceId}")
    public String html2Pdf(@PathVariable String instanceId,Model model){
        MissiveRecSeeCard missCard = misssCardR.getMissData(instanceId);
        MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard,"");
        model.addAttribute("missiveReceiveForm",missRender);
        model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
        model.addAttribute("instanceid",instanceId);
       if (missRender.getLeaderInstruct()!=null)
           model.addAttribute("isNewImgsize",missRender.getLeaderInstruct().id>this.receiptid);
       else
           model.addAttribute("isNewImgsize",true);






        return "MissiveReceivePDF";
    }

    @RequestMapping(value="/html2pdf/missiveReceive/{instanceId}/1")   //带附件的静态页面
    public String Receivehtml2Pdf(@PathVariable String instanceId,Model model){
        MissiveRecSeeCard missCard = misssCardR.getMissData(instanceId);
        MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard,"");
        model.addAttribute("missiveReceiveForm",missRender);
        model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
        model.addAttribute("instanceid",instanceId);

        String taskid="";
        Task task2 = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(instanceId));
        if(task2!=null) {
            taskid = task2.getId();
        }else{
            List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceId).orderByHistoricTaskInstanceEndTime().desc().list();

            //return previousTasklist.get(0).getId();
            if(!previousTasklist.isEmpty()) {
                taskid= previousTasklist.get(0).getId();
            }
        }
        //  Long TASKID =Long.parseLong(taskid);
//        Long lastTaskId=Long.parseLong(actService.getPreviousTaskIDByCurrentTaskID(taskid));//上一个任务编号 03-04
//        model.addAttribute("lastTaskId",lastTaskId);

        int versionNum = fileupc.getMaxMissiveVersion(Long.parseLong(instanceId),"missiveReceive");
        model.addAttribute("newVersionId",versionNum);

        Task task = this.taskService.createTaskQuery().taskId(String.valueOf(taskid)).singleResult();
        String name="";
        if(task!=null){
            name=task.getName();
        }else{
            HistoricTaskInstance task3 =  this.historyService.createHistoricTaskInstanceQuery().taskId(taskid).singleResult();
            name=task3.getName();

        }
        if (missRender.getLeaderInstruct()!=null)
            model.addAttribute("isNewImgsize",missRender.getLeaderInstruct().id>this.receiptid);
        else
            model.addAttribute("isNewImgsize",true);

        //String name = actService.getCurrentTask(Integer.valueOf(taskid)).getName();//获取当期任务名称
        ProcessType pt = ptr.findByName("收文流程");
        TaskName tn = tnr.findByName(name, pt);
        List<MissiveField> lmf = mfr.getEditControl(tn);

        List controls = new ArrayList();
        for (MissiveField mf : lmf) {
            controls.add(mf.getInputId());
        }

        model.addAttribute("editControl",controls);




        return "missiveReceivePDF2";
    }

    //zgx 01-08
    //根据流程定义ID和流程ID获取公文名称并设置公文类型
    public String getMissiveTitle(String processDeId,String instanceId){

        String missiveTitle="无标题";
        String  missiveType="";

        if(processDeId.contains("ReceiptId")){
            missiveType="收文流程";
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
            if(mrsc!=null&&mrsc.getTitle()!=null){
                missiveTitle=mrsc.getTitle();

            }

        }
        else if(processDeId.contains("PublishMissiveId")){
            missiveType="发文流程";
            MissivePublish mp =mpr.findByProcessID(Long.parseLong(instanceId));
            if(mp!=null&&mp.getMissiveTittle()!=null){
                missiveTitle=mp.getMissiveTittle();
            }

        }

        return  missiveTitle+"|"+missiveType;
    }

    //我的日程
    @RequestMapping(value="/mydate")
    public String html2Schedule(Model model,@ModelAttribute("user") User currentUser){
        User cu = ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(cu);
        String currentUser1=currentUser.getUserName();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String nowTime1 = format1.format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String nowTime2 = format.format(new Date());
        String taskList="";
        String  processDeId="";
        String missiveTitle="无标题";
        String  missiveType="";
        String instanceId="";
        int tasksNum=jdbcTemplate.queryForInt("SELECT count(*)  FROM oa3.act_hi_taskinst where ASSIGNEE_="+"'"+ currentUser1 +"'"+" and END_TIME_ >="+"'"+ nowTime1 +"'"+"and END_TIME_  <="+"'"+ nowTime2+"'");
        List taskLists=jdbcTemplate.queryForList("SELECT * FROM oa3.act_hi_taskinst where ASSIGNEE_="+"'"+ currentUser1 +"'"+" and END_TIME_ >="+"'"+ nowTime1 +"'"+"and END_TIME_  <="+"'"+ nowTime2+"'");
        Iterator it=taskLists.iterator();
        while (it.hasNext()){
            Map<String,Object> taskMap=(Map) it.next();
            processDeId=taskMap.get("PROC_DEF_ID_").toString();
            instanceId=taskMap.get("PROC_INST_ID_").toString();
            missiveTitle=getMissiveTitle(processDeId,instanceId).split("\\|")[0];
            missiveType=getMissiveTitle(processDeId,instanceId).split("\\|")[1];

            taskList+="公文类型："+missiveType+"， "+"任务名称："+taskMap.get("NAME_")+"， "+"任务标题："+missiveTitle+"<br/>";
        }
        model.addAttribute("ud", userFrom);
        model.addAttribute("tasksNum",tasksNum);
        model.addAttribute("taskList",taskList);
        return "newSchedule";
    }
    //通知通告
    @RequestMapping(value="/tzgg")
    public String html2Notication(Model model,@AuthenticationPrincipal User currentUser){
        User cu = ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(cu);
        model.addAttribute("ud", userFrom);
        return "notification";
    }
    @RequestMapping(value="indexOfDb")
    public String html2indexOfDb(Model model,@AuthenticationPrincipal User currentUser){
        User cu = ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(cu);
        model.addAttribute("user", userFrom);
        return "dbzxOfIndex";
    }
    @RequestMapping(value="/pzzx")//配置中心
    public String html2config(Model model, @AuthenticationPrincipal User currentUser){

        SendConfig messageSend =sendService.findByName("messageSend");
        SendConfig emailSend =sendService.findByName("emailSend");
        model.addAttribute("MessageSend",messageSend);
        model.addAttribute("EmailSend",emailSend);
        model.addAttribute("ud",currentUser);

        return "config";
    }

    @RequestMapping(value="/help")//配置中心
    public String getHelpDoc(Model model, @AuthenticationPrincipal User currentUser){


        model.addAttribute("user",currentUser);

        return "help";
    }

    //我的公文
    Integer missiveMainSendNum=0;//刚刚送达的主送公文数量初始为0；
    Integer missiveCopyToNum=0;//刚刚送达的抄送公文数量初始为0
    @RequestMapping(value="/wdgw/{itemActive}/{pageNum1}/{pageNum2}/{pageNum3}/{pageNum4}/{pageNum5}")
    public String html2wdgw(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum1,@PathVariable Integer pageNum2,@PathVariable Integer pageNum5,@PathVariable Integer pageNum3,@PathVariable Integer pageNum4,@PathVariable String itemActive)
    {
        logger.info("wdgw page start");
        missiveMainSendNum=0;
        missiveCopyToNum=0;
        User usr = this.ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(usr);
        if (pageNum1== null)
        {
            pageNum1 = 1;
        }
        if(pageNum2==null){
            pageNum2=1;
        }
        if(pageNum3==null){
            pageNum3=1;
        }
        if(pageNum5==null){
            pageNum5=1;
        }

        int taskDonePagesNum=1;
        int taskIngPagesNum=1;
        int fwPagesNum=1;

        PageableHistoryTaskList historyTasks = this.actService.getProcessDoneByUserName(currentUser.getUserName(), 13, pageNum1);//完成的任务
        List<TaskForm> lftDone=new ArrayList<TaskForm>();
        if (historyTasks.getTasklist() != null) {
            lftDone = mmc.getTaskFormByHistroyTask(historyTasks.getTasklist());
        }
        taskDonePagesNum=historyTasks.getPageTotal()==0?1:historyTasks.getPageTotal();





        PageableHistoryTaskList unfinishedTask =this.actService.getUncompletedTasksByUser(currentUser,13,pageNum2);
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();
        if(unfinishedTask.getTasklist()!=null){
            ltfIng=mmc.getUnfinishedTaskByUser(unfinishedTask.getTasklist());
        }
        taskIngPagesNum=unfinishedTask.getPageTotal()==0?1:unfinishedTask.getPageTotal();


       //主送的公文

        logger.info("getTasklistIng page start");

        int currentPageSend,currentPageCopy;
        currentPageSend=pageNum3;
        currentPageCopy=pageNum4;
        int taskCopyPagesNum=1;
        int taskSendPageNum=1;
        int NUM_PER_PAGE=13;
        List<MissivePublishForm> mainSend= new ArrayList<MissivePublishForm>();
        List<MissivePublishForm> copyTo= new ArrayList<MissivePublishForm>();
       Group group = usr.getGroup();
        if(group!=null) {
            Long groupid = group.getId();
//            String sql="select * from missive_publish , missive_publish_main_send_groups  where missive_publish.id=missive_publish_main_send_groups.missive_publish_id and missive_publish.processid not in (  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c))  and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_main_send_groups.group_id="+groupid+" order by missive_publish.created_date desc  limit ?,?";

            String sql="select * from missive_publish , missive_publish_main_send_groups  where missive_publish.id=missive_publish_main_send_groups.missive_publish_id and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_main_send_groups.group_id="+groupid+" order by missive_publish.created_date desc  limit ?,?";
//            List<Map<String,Object>> listMainSend= jdbc.queryForList("select * from missive_publish , missive_publish_main_send_groups  where missive_publish.id=missive_publish_main_send_groups.missive_publish_id and missive_publish.processid not in (  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c))  and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_main_send_groups.group_id="+groupid+
//                            " order by missive_publish.created_date desc limit ?,?",
//                    new Object[] { (currentPageSend * NUM_PER_PAGE - NUM_PER_PAGE), NUM_PER_PAGE });
            List<Map<String,Object>> listMainSend= jdbc.queryForList(sql,
                    new Object[] { (currentPageSend * NUM_PER_PAGE - NUM_PER_PAGE), NUM_PER_PAGE });

            logger.info("getTasklistIng page end");
            logger.info("getTasklistIng2 page start");


            //抄送
            String copySql="select * from missive_publish , missive_publish_copyto_groups  where missive_publish.id=missive_publish_copyto_groups.missive_publish_id and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_copyto_groups.group_id="+groupid+" order by missive_publish.created_date desc limit ?,?";
            List<Map<String,Object>> listCopyTo= jdbc.queryForList(copySql,
                    new Object[] { (currentPageCopy * NUM_PER_PAGE - NUM_PER_PAGE), NUM_PER_PAGE });
//            List<Map<String,Object>> listCopyTo= jdbc.queryForList("select * from missive_publish , missive_publish_copyto_groups  where missive_publish.id=missive_publish_copyto_groups.missive_publish_id and missive_publish.processid not in (  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_copyto_groups.group_id="+groupid+
//                            " order by missive_publish.created_date desc limit ?,?",
//                    new Object[] { (currentPageCopy * NUM_PER_PAGE - NUM_PER_PAGE), NUM_PER_PAGE });
            logger.info("getTasklistIng2 page end");
//            int totalRowsMainSend = jdbc.queryForInt("select count(*) from missive_publish , missive_publish_main_send_groups  where missive_publish.id=missive_publish_main_send_groups.missive_publish_id and missive_publish.processid not in (  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c))   and  missive_publish_main_send_groups.group_id="+groupid);
//            int totalRowsCopyTo = jdbc.queryForInt("select count(*) from missive_publish , missive_publish_copyto_groups  where missive_publish.id=missive_publish_copyto_groups.missive_publish_id and missive_publish.processid not in (  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_copyto_groups.group_id="+groupid);


            int totalRowsMainSend = jdbc.queryForInt("select count(*) from missive_publish , missive_publish_main_send_groups  where missive_publish.id=missive_publish_main_send_groups.missive_publish_id and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_main_send_groups.group_id="+groupid);
            int totalRowsCopyTo = jdbc.queryForInt("select count(*) from missive_publish , missive_publish_copyto_groups  where missive_publish.id=missive_publish_copyto_groups.missive_publish_id  and missive_publish.processid in (select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_   in(  SELECT  f.PROC_INST_ID_ from oa3.act_hi_actinst f where f.ACT_NAME_='文印室套红排版打印' or f.ACT_NAME_='拟稿处室分发' ))  and  missive_publish_copyto_groups.group_id="+groupid);
            if (totalRowsMainSend % NUM_PER_PAGE == 0) {
                taskSendPageNum=totalRowsMainSend / NUM_PER_PAGE;
            } else {
                taskSendPageNum= totalRowsMainSend / NUM_PER_PAGE + 1;
            }
            if (totalRowsCopyTo % NUM_PER_PAGE == 0) {
                taskCopyPagesNum=totalRowsCopyTo / NUM_PER_PAGE;
            } else {
                taskCopyPagesNum= totalRowsCopyTo / NUM_PER_PAGE + 1;
            }


            taskSendPageNum=taskSendPageNum==0?1:taskSendPageNum;
            taskCopyPagesNum=taskCopyPagesNum==0?1:taskCopyPagesNum;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String systemTime = sdf .format(new Date()).toString();//系统当前时间
            logger.info("getmainSend  start");


            if(listMainSend!=null&&listMainSend.size()!=0) {
                for (int i = 0; i < listMainSend.size(); i++) {
                    MissivePublishForm mpf3 = new MissivePublishForm();
                    mpf3.setMissiveTittle(listMainSend.get(i).get("missive_tittle").toString());
                    String sendTime =listMainSend.get(i).get("created_date").toString();
                    mpf3.setMissiveFlag(computeDateSpan(sendTime,systemTime,sdf,"mainSend"));
                    mpf3.setCreatedDate(sendTime);
                    mpf3.setProcessID(Long.parseLong(listMainSend.get(i).get("processid").toString()));

                    mainSend.add(mpf3);//添加到List<MissivePublishForm>
                }
            }

            logger.info("getmainSend  end");

            logger.info("getcopySend  start");

            if(listCopyTo!=null&&listCopyTo.size()!=0){
                for (int i = 0; i < listCopyTo.size(); i++) {
                    MissivePublishForm mpf4 = new MissivePublishForm();
                    String sendTime = listCopyTo.get(i).get("created_date").toString();
                    mpf4.setMissiveFlag(computeDateSpan(sendTime,systemTime,sdf,"copyTo"));
                    mpf4.setMissiveTittle(listCopyTo.get(i).get("missive_tittle").toString());
                    mpf4.setCreatedDate(sendTime);
                    mpf4.setProcessID(Long.parseLong(listCopyTo.get(i).get("processid").toString()));
                    copyTo.add(mpf4);//添加到List<MissivePublishForm>
                }
            }

            logger.info("getcopySend  end");

        }
        //主送

        /*String taskId="";//流程实例第一个任务编号
        Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(lftDone.get));
        taskId = task.getId();*/

        model.addAttribute("mainSend",mainSend);
        model.addAttribute("copyTo",copyTo);
        model.addAttribute("mainSendTotalNum",taskSendPageNum);
        model.addAttribute("copyToTotalNum",taskCopyPagesNum);
        model.addAttribute("taskIngPagesNum",taskIngPagesNum);


        if(group.getGroupName().equals("分局领导"))
        {
            PageableHistoryTaskList publishTasks = this.actService.getPublishMissiveList(currentUser.getUserName(), 13, pageNum5);//完成的任务
            List<TaskForm> fwDone=new ArrayList<TaskForm>();
            if (publishTasks.getTasklist() != null) {
                fwDone = mmc.getTaskFormByHistroyTask(publishTasks.getTasklist());
            }
            fwPagesNum=publishTasks.getPageTotal()==0?1:publishTasks.getPageTotal();
            model.addAttribute("publishList",fwDone);
            model.addAttribute("PublishPageNum",fwPagesNum);
        }


       int startPage=(pageNum1-2<1)?1:pageNum1-2;
       int endPage=(pageNum1+2>taskDonePagesNum)?taskDonePagesNum:pageNum1+2;


        int startPage2=(pageNum2-2<1)?1:pageNum2-2;
        int endPage2=(pageNum2+2>taskIngPagesNum)?taskIngPagesNum:pageNum2+2;



        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("startPage2",startPage2);
        model.addAttribute("endPage2",endPage2);
        model.addAttribute("tasksDone",lftDone);
        model.addAttribute("taskDoneTotalNum",taskDonePagesNum);
        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",taskIngPagesNum);
        model.addAttribute("activeitem",itemActive);
        model.addAttribute("DonePage",pageNum1);
        model.addAttribute("IngPage",pageNum2);
        model.addAttribute("mainSendPage",pageNum3);
        model.addAttribute("copyToPage",pageNum4);
        model.addAttribute("PublishPage",pageNum5);
        Group userGr = currentUser.getGroup();//当前用户所在组别
        model.addAttribute("ud", userFrom);
        model.addAttribute("userName", currentUser.getUserName());
        model.addAttribute("missiveMainSendDatest",missiveMainSendNum);
        model.addAttribute("missiveCopyToDatest",missiveCopyToNum);

        logger.info("wdgw page end");
        return "myMissive";
    }





    public String computeDateSpan(String beginDate,String endDate,SimpleDateFormat sdf,String index){
        try {
            Date begin = sdf.parse(beginDate);
            Date end = sdf.parse(endDate);
            long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒

            long day=between/(24*3600);
            long hour=between%(24*3600)/3600;
            long minute=between%3600/60;
            long second=between%60/60;

            if(day>=3&&day<7){
                return ("三天之前送达");
            }
            else if(day<3&&day>=1){
                return("三天内送达");
            }
            else if(day<1&&hour>=1){
                if(index.equals("mainSend")){
                    missiveMainSendNum++;
                }
                else if(index.equals("copyTo"))
                {
                    missiveCopyToNum++;
                }
                return("当日送达");

            }
            else if(day<1&&hour<1&&minute>=10){
                if(index.equals("mainSend")){
                    missiveMainSendNum++;
                }
                else if(index.equals("copyTo"))
                {
                    missiveCopyToNum++;
                }
                return("一小时内送达");
            }
            else if(day<1&&hour<1&&minute<10){
                if(index.equals("mainSend")){
                    missiveMainSendNum++;
                }
                else if(index.equals("copyTo"))
                {
                    missiveCopyToNum++;
                }
                return("刚刚送达");
            }
            else if(day>=7&&day<30){
                return("一周前送达");
            }
            else if(day>=30&&day<360){
                return ("一月前送达");
            }
            else if(day>=365&&day<730){
                return ("一年前送达");
            }
            else{
                return ("公文已经很久远");
            }
        }
        catch (Exception ex){
            logger.error("date parse exception:", ex);
            return "";
        }
    }






}
