package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.MissiveRecSeeCard;
import cn.edu.shou.missive.domain.missiveDataForm.*;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.ActivitiController;
import cn.edu.shou.missive.web.FileOperate;
import cn.edu.shou.missive.web.FileUploadController;
import cn.edu.shou.missive.web.MissiveReceiveFunction;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by TISSOT on 2014/7/23.
 */
@RestController
@RequestMapping(value = "/api")
public class MissiveReceiveController {
    @Value("${spring.main.homedir}")
    String homedir;
    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Value("${spring.main.htmlUrlBase}")
    String htmlUrlBase;
    @Value("${spring.main.htmlUrlBaseFax}")
    String htmlUrlBaseFax;
    @Value("${server.port}")
    String hostport;
    @Autowired
    private MissiveFieldRepository mfr;
    @Autowired
    private TaskNameRepository tnr;
    @Autowired
    private ProcessTypeRepository ptr;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissiveRepository mr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private UrgentLevelRepository urgentR;
    @Autowired
    private CommentContentRepository commentR;
    @Autowired
    private MissiveRepository misssR;
    @Autowired
    private MissiveVersionRepository mVerR;
    @Autowired
    private MissiveTypeRepository mTypeR;
    @Autowired
    private AttachmentRepository actchR;
    @Autowired
    private MissiveReceiveTaskDealerRepository mTaskDealerR;
    @Autowired
    private MissivePublishRepository mPublishR;

    @Autowired
    private ACT_RU_TASK_Repository actR;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commF;
    @Autowired
    private FileUploadController fileupc;
    @Autowired
    private MissiveReceiveTaskDealerRepository mrtdr;
    @Autowired
    private MissiveReceiveFunction mrf;
    @Autowired
    private ActivitiController actCont;
    @Autowired
    private UserDAO ud;
    @Autowired
    private DaoService service;
    @Autowired
    private UserConfigRepository userConR;
    @Autowired
    private MyMissiveController mmc;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ConvertService convertService;
    @Autowired
    private UserRepository ur;
//    @Autowired
//    private NewsPublish newsPublish;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final static Logger logger = LoggerFactory.getLogger(MissiveReceiveController.class);


    //String uploadPath="C:/Users/hy/Desktop/esicmissive_springboot117/esicmissive_springboot924/esicmissive_springboot_1/upload/missiveReceive/";
    //String convertTool="C:/Users/hy/mrfDesktop/esicmissive_springboot117/esicmissive_springboot924/esicmissive_springboot_1/word2pdf816.exe";


   /* //新建流程表单，返回编号
    @RequestMapping(value="/createForm")
    public Long createMissForm()
    {
        MissiveRecSeeCard mrsc2 = new MissiveRecSeeCard();
        mrsc2.setCode((long) 0);
        mrsc2=mrscr.save(mrsc2);
        return mrsc2.getId();
    }*/
   MissivePublishFunction mpf=new MissivePublishFunction();
    @Autowired
    private ScheduleRepository sDAO;
    //新建数据库流程实例对象，获取对象编号
    /*@RequestMapping(value="/newYBK")
    public Long newYBK()
    {
        Schedule s = new Schedule();
        s.setTitle("123");
        this.sDAO.save(s);
        MissiveRecSeeCard mrsc2 = new MissiveRecSeeCard();
        mrsc2.setCode((long) 0);
        mrscr.save(mrsc2);
        return mrsc2.getId();

    }*/

    CommonFunction cf= new CommonFunction();
    //MissiveReceiveFunction mrf=new MissiveReceiveFunction();

    //获取紧急程度信息
    @RequestMapping(value="/getUrgencyLevel", method=RequestMethod.GET)
    public List<UrgentLevelForm> getUrgencyLevel(){

        List<UrgentLevel> uls = urgentR.findAll();
        List<UrgentLevelForm>ulfs =new ArrayList();
        for(UrgentLevel ul:uls){
            UrgentLevelForm ulf =new UrgentLevelForm();
            ulf.setId(ul.getId());
            ulf.setValue(ul.getValue());
            ulfs.add(ulf);
        }
        //String urgency = cf.getJsonDataByObject(ulfs);

        return  ulfs;
    }
    //获取公文种类信息
    @RequestMapping(value="/getMissiveTypes",method = RequestMethod.GET)
    public List<MissiveTypeFrom> getMissiveTypes(){
        List<MissiveType> mts = mTypeR.findAll();
        List<MissiveTypeFrom>mtfs =new ArrayList();
        for(MissiveType mt:mts){
            MissiveTypeFrom mtf =new MissiveTypeFrom();
            mtf.setId(mt.getId());
            mtf.setTypeName(mt.getTypeName());
            mtfs.add(mtf);
        }
        return  mtfs;
    }

    //获取最新流程定义编号
    public String getCurrentDeId(String processType){
        String proDeId="";
        List<ProcessDefinition> prcesses =this.actService.getAllProcessD();
        for (ProcessDefinition process : prcesses) {
            String processName = process.getName();
            if (processName.equals(processType)) {
                proDeId = process.getId();
            }
        }
        return proDeId;
    }


    //点击创建流程按钮后，跳转到指定页面



    //提交第一个任务后创建流程
    @RequestMapping(value="/startProInstance",method= RequestMethod.POST)
    public String[]  startProInstance(String type,String userName)
    {
        String proDeId="";//最新流程定义编号
        String proInstanceId="";//新建流程实例编号
        String taskId="";//流程实例第一个任务编号
        String isPass="";//流程分支条件
        String assignees="";



        Long businessKey=0l;//设置流程实例的键值


        Missive missive = new Missive();//新建公文对象

        List<MissiveVersion> lmv =new ArrayList<MissiveVersion>();

        MissiveVersion missV = new MissiveVersion();//公文版本
        missV.setVersionNumber(0);//新建公文时设置版本为一
        mVerR.save(missV);//保存版本

        lmv.add(missV);

        missive.setVersions(lmv);//设置公文版本属性
        missive = misssR.save(missive);//保存公文信息

        missV.setMissive(missive);//设置版本的公文编号
        mVerR.save(missV);

        User user =ud.findByUserName(userName); //获取用户对象





        if(type.contains("收文")) {


            //获取收文流程最新定义编号
            proDeId=getCurrentDeId("Receipt");

            //新建阅办卡
            MissiveRecSeeCard mrsc = new MissiveRecSeeCard();
            mrsc.setMissiveInfo(missive);
            mrsc = mrscr.save(mrsc);
            businessKey = mrsc.getId();//获取阅办卡编号，作为流程实例的businesskey

            if (proDeId != "") {//流程实例id
                proInstanceId = actService.startProcess(proDeId, businessKey, user);//启动流程
            }


            MissiveReceiveTaskDealer mrtd = new MissiveReceiveTaskDealer();//每一步的执行人

            mrtd.setInstanceId(Long.parseLong(proInstanceId));
            mrtd.setOfficeRegist(userName);
            mTaskDealerR.save(mrtd);


            //获取当前任务编号
            if (proInstanceId != ""){//任务id
                Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                taskId = task.getId();
                task.setDescription("0");   //01-13孙乐 新添加 增加置顶功能所需字段
                taskService.saveTask(task);
            }

            mrsc.setInstanceId(proInstanceId);//设置阅办卡的流程编号属性，绑定流程实例和阅办卡
            mrscr.save(mrsc);


        }
        else if(type.contains("发文")){
            proDeId=getCurrentDeId("PublishMissive");
            //新建阅办卡
            MissivePublish mPublish = new MissivePublish();
            mPublish.setMissiveInfo(missive);

            mPublish = mPublishR.save(mPublish);
            businessKey = mPublish.getId();//获取阅办卡编号，作为流程实例的businesskey
            if (proDeId != "") {//流程实例id
                proInstanceId = actService.startProcess(proDeId, businessKey, user);//启动流程
            }
            //获取当前任务编号
            if (proInstanceId != ""){//任务id
                Task task = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(proInstanceId));
                taskId = task.getId();
                task.setDescription("0");   //01-13孙乐 新添加 增加置顶功能所需字段
                taskService.saveTask(task);
            }
            //插入办公室复核人及排版人
           /* UserConfig checker = userConR.findByName("missivePublish_officeCheckUser");
            UserConfig printer = userConR.findByName("missivePublish_printingUser");
            User checkerU = ud.findByUserName(checker.getValue());
            User printerU = ud.findByUserName(printer.getValue());
            mPublish.setOfficeCheckUser(checkerU);
            mPublish.setPrinter(printerU);*/


            mPublish.setProcessID(Long.parseLong(proInstanceId));//设置阅办卡的流程编号属性，绑定流程实例和阅办卡
            mPublish.setTaskID(Long.parseLong(taskId));
            mPublishR.save(mPublish);
        }



        String[] resultArr={proDeId,proInstanceId,taskId};//方法返回数组
        logger.info(userName+" start "+type+" process instance,PROC_INST_ID="+proInstanceId);
        return resultArr;

    }

    //根据用户名，获取待办任务
    @RequestMapping(value="/getTasks/{userName}")
    public List<TaskForm> getTasksByUser(@PathVariable String userName){
        User user=ud.findByUserName(userName);
        List<Task>  tasks = this.actService.getCurrentTasksByUser(user);//获取当前任务

        List<TaskForm> tfl = new ArrayList<TaskForm>();

        if(tasks!=null&&tasks.size()!=0) {
            for (Task task : tasks) {
                String instanceId = task.getProcessInstanceId();//流程实例编号
                String taskId = task.getId();
                String missiveTitle = "";//公文标题
                String version = "";//公文版本号
                String lastTaskId = actService.getPreviousTaskIDByCurrentTaskID(task.getId());//上一个任务编号
                String processDeId = task.getProcessDefinitionId();//流程定义号
                String missiveType = "";//公文类型
                String urgencyLevel = "";

                TaskForm tf = mrf.Tasks2TaskForm(task);
                tf.setIsPadDealMissive("no");
                List<NextTask> lnt = new ArrayList<NextTask>();

                String taskName = tf.getName();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String taskStartTime = String.valueOf(format.format(task.getCreateTime()));
                tf.setTaskStartTime(taskStartTime);

                List<Map<String, ? extends Object>> actVars = actService.getNextTaskInfo(instanceId, Integer.parseInt(taskId));//获取下一步任务
                if (actVars != null) {
                    for (Map<String, ? extends Object> act : actVars) {
                        NextTask nt = new NextTask();
                        nt.setText(act.get("name").toString());
                        nt.setVariableName("isPass");
                        nt.setValue(act.get("condition").toString());
                        lnt.add(nt);
                    }
                }
                tf.setTaskOperate(lnt);
                if (processDeId.contains("ReceiptId")) {//获取收文流程待办任务信息，如公文标题、公文类型、版本号
                    missiveType = "missiveReceive";
                    MissiveRecSeeCard mrsc = mrscr.getMissData(instanceId);
                    missiveTitle = mrsc.getTitle();
                    Missive missiveInfo = mrsc.getMissiveInfo();
                    version = String.valueOf(fileupc.getMaxMissiveVersion(Long.parseLong(instanceId), "missiveReceive"));
                    if (mrsc.getUrgencyLevel() != null) {
                        urgencyLevel = mrsc.getUrgencyLevel().getValue();
                    }
                    tf.setIsPadSelectUser("no");
                    List<String> paReceivedMissives = new ArrayList<String>();//"领导批示","分发阅办"};
                    paReceivedMissives.add("领导批示");
                    paReceivedMissives.add("分发阅办");
                    if(paReceivedMissives.contains(taskName)){
                        tf.setIsPadDealMissive("yes");
                    }
                    else{
                        tf.setIsPadDealMissive("no");
                    }

                } else if (processDeId.contains("PublishMissiveId")) {//获取发文流程待办任务信息
                    missiveType = "missivePublish";
                    MissivePublish mp = mPublishR.findByProcessID(Long.parseLong(instanceId));
                    Missive missiveInfo = mp.getMissiveInfo();
                    missiveTitle = mp.getMissiveTittle();
                    version = String.valueOf(fileupc.getMaxMissiveVersion(Long.parseLong(instanceId), "missivePublish"));
                    if (missiveInfo != null && missiveInfo.getUrgentLevel() != null) {
                        urgencyLevel = missiveInfo.getUrgentLevel().getValue();
                    }
                    if(taskName.equals("处室领导审核")||taskName.equals("办公室审核及编号")){
                        tf.setIsPadSelectUser("yes");
                    }
                    else{
                        tf.setIsPadSelectUser("no");
                    }
                    List<String> padPublishMissives = new ArrayList<String>();
                    padPublishMissives.add("处室领导审核");
                    padPublishMissives.add("相关部门会签");
                    padPublishMissives.add("领导审批");
                    if(padPublishMissives.contains(taskName)){
                        tf.setIsPadDealMissive("yes");
                    }
                    else{
                        tf.setIsPadDealMissive("no");
                    }

                }
                if (urgencyLevel != null) {
                    tf.setUrgencyLevel(urgencyLevel);//紧急程度
                }
                tf.setMissiveTitle(missiveTitle);//公文标题
                tf.setMissiveVersion(version);//公文最新版本
                tf.setMissiveType(missiveType);//公文操作类型
                if (lastTaskId != null) {
                    tf.setLastTaskId(Long.parseLong(lastTaskId));
                }

                tfl.add(tf);
            }
        }
        return tfl;
    }

    //委托任务
    @RequestMapping(value="/delegateTask/{taskId}/{userName}")
    public void delegateTask(@PathVariable Long taskId,@PathVariable String userName){
        actService.delegateTask(taskId,userName);
    }



    //根据流程实例编号获取阅办卡内容
    @RequestMapping(value="/getPreContent")
    public MissiveReceiveRender getPreContent(Long instanceid,Long taskid){
         MissiveRecSeeCard mrsc4 = mrscr.getMissData(instanceid.toString());
        String taskName = actService.getCurrentTask(Integer.parseInt(taskid.toString())).getName();
        MissiveReceiveRender mrr=mrf.MissiveReceive2MissiveReceiveRender(mrsc4,taskName);
        return mrr;
    }

    //完成当前任务
    @RequestMapping(value="/submitFormContent/{instanceId}/{taskId}")
    public String completeCurreentTask(String formData,@PathVariable Long instanceId,@PathVariable Long taskId, @AuthenticationPrincipal User currentUser) throws IOException {

        logger.info("begin insert form data:"+new Date().toString());
        String formDataStr = formData.substring(1, formData.length() - 2);
        String[] formDataArr = formDataStr.split(",\"");
        HashMap<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < formDataArr.length; i++) {
            String[] tempArr = formDataArr[i].split("\":");
            String Name = tempArr[0].replace("\"", "");
            String Value = tempArr[1].replace("\"", "");
            map.put(Name, Value);
        }
        String OfficeHandle = map.get("Handle");
        String ChargerCheck = map.get("ChargerCheck");
        String LeaderSign = map.get("LeaderSign");
        String Reado = map.get("Reado");
        String OfficeDispose = map.get("OfficeDispose");
        String UndertakeDispose = map.get("UndertakeDispose");
        //String ReadoDispose = map.get("ReadoDispose");
        String remark = map.get("remark");
        String sendUnit=map.get("sendUnit");
        String mTitle = map.get("mTitle");

        //String mTittle=mTitle.replace("<br>", " ");
        String mTittle=mTitle;

        String mType = map.get("mType");
        String mTaskIndex = map.get("mTaskIndex");

        String mReceiveYear = map.get("mReceiveYear");
        String mReceiveMonth = map.get("mReceiveMonth");
        String mReceiveDay = map.get("mReceiveDay");
        String mReceiveNum = map.get("mReceiveNum");


        String mMissiveNum = map.get("mMissiveNum");
        String mMissiveCount = map.get("mMissiveCount");
        String mEmergencyLevel = map.get("mEmergencyLevel");
        String mDeadline = map.get("mDeadline");
        String mMissiveAbstract = map.get("mMissiveAbstract");

        String mRegisterModify = map.get("mRegisterModify");
        String mToDoModify = map.get("mToDoModify");
        String mOfficeToDo = map.get("mOfficeToDo");
        String mToDoMonth = map.get("mToDoMonth");
        String mToDoDay = map.get("mToDoDay");

        String mLeaderInstruct_img = map.get("mLeaderInstruct_img");
       /* String mLeaderInstructBase30img = map.get("mLeaderInstructBase30img");
        String mLeaderInstructJS_img = map.get("mLeaderInstructJS_img");
        String mLeaderInstructJSBase30img=map.get("mLeaderInstructJSBase30img");*/
        String mLeaderSignArea = map.get("mLeaderSignArea");
        String mInstructMonth = map.get("mInstructMonth");
        String mInstructDay = map.get("mInstructDay");

        String mLookSign_img = map.get("mLookSign_img");
      /*  String mLookSignBase30img_sign = map.get("mLookSignBase30img_sign");
        String mLookSignJS_img = map.get("mLookSignJS_img");
        String mLookSignJSBase30img_sign = map.get("mLookSignJSBase30img_sign");*/
        String mLookSignArea = map.get("mLookSignArea");

        String mUndertake_img = map.get("mUndertake_img");
       /* String mUndertakeBase30img_sign = map.get("mUndertakeBase30img_sign");
        String mUndertakeJS_img = map.get("mUndertakeJS_img");
        String mUndertakeJSBase30img_sign = map.get("mUndertakeJSBase30img_sign");*/
        String mUndertakeArea = map.get("mUndertakeArea");

        String mUploadAttach = map.get("mUploadAttach");
        String assName=map.get("nextTaskUserName");

        MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
        Missive missive = mrsc.getMissiveInfo();
        MissiveReceiveTaskDealer mrtd = mTaskDealerR.getTaskDealer(instanceId);

        //任务执行人
        if (!OfficeHandle.equals("")) {//拟办人
            mrtd.setOfficeHandle(OfficeHandle);
        }
        if (!ChargerCheck.equals("")) {//审核人
            mrtd.setChargerCheck(ChargerCheck);
        }
        if (!LeaderSign.equals("")) {//领导批示
            mrtd.setLeaderSign(LeaderSign);
        }
        if (!OfficeDispose.equals("")) {//办公室处理人
            mrtd.setOfficeDispose(OfficeDispose);
        }
        if (!Reado.equals("")) {//阅办人
            mrtd.setReado(Reado);
        }
//        if (!ReadoDispose.equals("")) {//阅办后处理人
//            mrtd.setReadoDispose(ReadoDispose);
//        }
        if (!UndertakeDispose.equals("")) {//承办人
            mrtd.setUndertakeDispose(UndertakeDispose);
        }
        mTaskDealerR.save(mrtd);

        //公文标题
        if (!mTitle.equals("")) {
            mrsc.setTitle(mTittle);
        }
        if(!sendUnit.equals("")){
            mrsc.setSendUnit(sendUnit);
        }

        if (!mType.equals("")) {
            MissiveType mt = mTypeR.findOne(Long.parseLong(mType));
            missive.setMissiveType(mt);
            mrsc.setMissiveInfo(missive);
        }

        //收文年份
        if (!mReceiveYear.equals("")) {
            mrsc.setReceiveYear(mReceiveYear);
        }

        //收文月份
        if (!mReceiveMonth.equals("")) {
            mrsc.setReceiveMonth(mReceiveMonth);
        }

        //收文日
        if (!mReceiveDay.equals("")) {
            mrsc.setReceiveDay(mReceiveDay);
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
        String nowTime1 = String.valueOf(format1.format(new Date()));
        //收文号

        if (!mReceiveNum.equals("")) {
            if (!mType.equals("")) {
                MissiveType mt = mTypeR.findOne(Long.parseLong(mType));
                String missiveTyName = mt.typeName;

                if (missiveTyName.contains("传真")) {
                    if(mReceiveNum.contains("传")){
                        mReceiveNum = nowTime1 + "|"  + mReceiveNum;
                    }else
                    mReceiveNum = nowTime1 + "|" + "传" + " " + mReceiveNum;
                    mrsc.setCode(mReceiveNum);
                } else {
                    if(mReceiveNum.contains("收")){
                        mReceiveNum = nowTime1 + "|" + mReceiveNum;
                    }else
                    mReceiveNum = nowTime1 + "|" + "收" + " " + mReceiveNum;
                    mrsc.setCode(mReceiveNum);
                }

            }
            else {
                mReceiveNum=nowTime1+"|"+mReceiveNum;
                mrsc.setCode(mReceiveNum);
            }
        }




        //文件字号
        if (!mMissiveNum.equals("")) {
            mrsc.setMissiveNumber(mMissiveNum);
        }

        //份数
        if (!mMissiveCount.equals("")) {
            mrsc.setFileCount(mMissiveCount);
        }

        //紧急程度
        if (!mEmergencyLevel.equals("")) {
            UrgentLevel ul = urgentR.findOne(Long.parseLong(mEmergencyLevel));
            mrsc.setUrgencyLevel(ul);
        }

        //办理时限
        if (!mDeadline.equals("")) {
            mrsc.setDealDeadline(mDeadline);
        }

        //来文摘要
        if (!mMissiveAbstract.equals("")) {
            mrsc.setMissiveAbstract(mMissiveAbstract);
        }

        //拟办内容
        if (!mOfficeToDo.equals("")) {
            mrsc.setOfficeToDo(mOfficeToDo);
        }


        //修改意见
        if (!mRegisterModify.equals("")) {
            mrsc.setReModifyContent(mRegisterModify);
        }
        if (!mToDoModify.equals("")) {
            mrsc.setToModifyContent(mToDoModify);
        }


        //领导批示
       // if (!mLeaderInstruct_img.equals("") || !mLeaderInstructBase30img.equals("") || !mLeaderSignArea.equals("")||!mLeaderInstructJS_img.equals("")||!mLeaderInstructJSBase30img.equals("")) {
            if (!mLeaderInstruct_img.equals("")) {

                CommentContent leaderInstuct = mrsc.getLeaderInstruct();
            if (leaderInstuct != null) {
                if (!mLeaderInstruct_img.equals("")) {
                    leaderInstuct.setImageurl(mLeaderInstruct_img);
                }
//               if (!mLeaderInstructBase30img.equals("")) {
//                    leaderInstuct.setBase30url(mLeaderInstructBase30img);
//                }
                if (!mLeaderSignArea.equals("")) {
                    leaderInstuct.setContentText(mLeaderSignArea);
                }
//                if(!mLeaderInstructJS_img.equals("")){
//                    leaderInstuct.setJsignatureImageurl(mLeaderInstructJS_img);
//                }
//                if(!mLeaderInstructJSBase30img.equals("")){
//                    leaderInstuct.setJsignatureBase30url(mLeaderInstructJSBase30img);
//                }
                commentR.save(leaderInstuct);
                mrsc.setLeaderInstruct(leaderInstuct);
            } else {
                CommentContent cc = new CommentContent();
                if (!mLeaderInstruct_img.equals("")) {
                    cc.setImageurl(mLeaderInstruct_img);
                }
//               if (!mLeaderInstructBase30img.equals("")) {
//                    cc.setBase30url(mLeaderInstructBase30img);
//                }
                if (!mLeaderSignArea.equals("")) {
                    cc.setContentText(mLeaderSignArea);
                }
//                if(!mLeaderInstructJS_img.equals("")){
//                    cc.setJsignatureImageurl(mLeaderInstructJS_img);
//                }
//                if(!mLeaderInstructJSBase30img.equals("")){
//                    cc.setJsignatureBase30url(mLeaderInstructJSBase30img);
//                }
                commentR.save(cc);
                mrsc.setLeaderInstruct(cc);
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime=String.valueOf(format.format(new Date()));
        if(remark!=null && !remark.equals("")) {

            this.jdbcTemplate.execute("INSERT INTO oa4.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('" + taskId + "','" + nowTime + "','" + currentUser.getUserName() + "','" + taskId + "','" + instanceId + "','备注','" + remark + "')");
            //this.actService.addTaskComment(taskId.toString(),remark);
        }
        //阅办人签字
        //if (!mLookSign_img.equals("") || !mLookSignBase30img_sign.equals("") || !mLookSignArea.equals("")||!mLookSignJS_img.equals("") || !mLookSignJSBase30img_sign.equals("") ) {

            if (!mLookSign_img.equals("") ) {
            CommentContent lookerSign = mrsc.getLookerSign();
            if (lookerSign != null) {
                if (!mLookSign_img.equals("")) {
                    lookerSign.setImageurl(mLookSign_img);
                }
//               if (!mLookSignBase30img_sign.equals("")) {
//                    lookerSign.setBase30url(mLookSignBase30img_sign);
//                }
                if (!mLookSignArea.equals("")) {
                    lookerSign.setContentText(mLookSignArea);
                }
//                if(!mLookSignJS_img.equals("")){
//                    lookerSign.setJsignatureImageurl(mLookSignJS_img);
//                }
//                if(!mLookSignJSBase30img_sign.equals("")){
//                    lookerSign.setJsignatureBase30url(mLookSignJSBase30img_sign);
//                }
                commentR.save(lookerSign);
                mrsc.setLookerSign(lookerSign);
            } else {
                CommentContent cc = new CommentContent();
                if (!mLookSign_img.equals("")) {
                    cc.setImageurl(mLookSign_img);
                }
//                if (!mLookSignBase30img_sign.equals("")) {
//                    cc.setBase30url(mLookSignBase30img_sign);
//                }
                if (!mLookSignArea.equals("")) {
                    cc.setContentText(mLookSignArea);
                }
//                if(!mLookSignJS_img.equals("")){
//                    cc.setJsignatureImageurl(mLookSignJS_img);
//                }
//                if(!mLookSignJSBase30img_sign.equals("")){
//                    cc.setJsignatureBase30url(mLookSignJSBase30img_sign);
//                }
                commentR.save(cc);
                mrsc.setLookerSign(cc);
            }
        }

        //承办
        //if (!mUndertake_img.equals("") || !mUndertakeBase30img_sign.equals("") || !mUndertakeArea.equals("")||!mUndertakeJS_img.equals("") || !mUndertakeJSBase30img_sign.equals("")) {

            if (!mUndertake_img.equals("") ) {
            CommentContent undertake = mrsc.getUndertake();
            if (undertake != null) {
                if (!mUndertake_img.equals("")) {
                    undertake.setImageurl(mUndertake_img);
                }
//                if (!mUndertakeBase30img_sign.equals("")) {
//                    undertake.setBase30url(mUndertakeBase30img_sign);
//                }
                if (!mUndertakeArea.equals("")) {
                    undertake.setContentText(mUndertakeArea);
                }
//                if(!mUndertakeJS_img.equals("")){
//                    undertake.setJsignatureImageurl(mUndertakeJS_img);
//                }
//                if(!mUndertakeJSBase30img_sign.equals("")){
//                    undertake.setJsignatureBase30url(mUndertakeJSBase30img_sign);
//                }
                commentR.save(undertake);
                mrsc.setUndertake(undertake);
            } else {
                CommentContent cc = new CommentContent();
                if (!mUndertake_img.equals("")) {
                    cc.setImageurl(mUndertake_img);
                }
//                if (!mUndertakeBase30img_sign.equals("")) {
//                    cc.setBase30url(mUndertakeBase30img_sign);
//                }
                if (!mUndertakeArea.equals("")) {
                    cc.setContentText(mUndertakeArea);
                }
//                if(!mUndertakeJS_img.equals("")){
//                    cc.setJsignatureImageurl(mUndertakeJS_img);
//                }
//                if(!mUndertakeJSBase30img_sign.equals("")){
//                    cc.setJsignatureBase30url(mUndertakeJSBase30img_sign);
//                }
                commentR.save(cc);
                mrsc.setUndertake(cc);
            }
        }

        String taskName = actService.getCurrentTask(Integer.parseInt(taskId.toString())).getName();

        //拟办月份
        if (!mToDoMonth.equals("") && taskName.equals("办公室拟办及审核")) {
            mrsc.setToDoDateMonth(mToDoMonth);
        }

        //拟办日
        if (!mToDoDay.equals("") && taskName.equals("办公室拟办及审核")) {
            mrsc.setToDoDateDay(mToDoDay);
        }

        //批示月份
        if (!mInstructMonth.equals("") && taskName.equals("领导批示")) {
            mrsc.setReceiveMonth(mInstructMonth);
        }

        //批示日期
        if (!mInstructDay.equals("") && taskName.equals("领导批示")) {
            mrsc.setReceiveDay(mInstructDay);
        }
        logger.info("end insert form data:"+new Date().toString());
        logger.info("begin convert:"+new Date().toString());
        //阅办用户
//        List<User> tempCounterSignUsersList=new ArrayList<User>();//阅办用户
//        String[] tempCounterSignUsers=Reado.split("\\|");
//        for(int i=0;i<tempCounterSignUsers.length;i++){
//            User tempCounterSignUser=ur.findByUserName(tempCounterSignUsers[i]);
//            tempCounterSignUsersList.add(tempCounterSignUser);
//        }




        //存储附件
        Long latestVersionNum = Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missiveReceive")));//获取最新版本号
        List<Attachment> actl;
        Long newVersionNum;
        Long newVersionId;
        MissiveVersion tempMissiveVersion;

       // if (taskName.equals("办公室登记")) {
            //保存附件

            newVersionNum = latestVersionNum + 1;


            String fileBasePath = fileUploadDir + "/missiveReceive/" + instanceId + "/" + newVersionNum + "/";
            MissiveVersion missVer = new MissiveVersion();//创建最新公文版本
            missVer.setVersionNumber(newVersionNum);
            missVer.setMissive(missive);
            MissiveVersion newVersion = mVerR.save(missVer);
            //tempMissiveVersion=newVersion;
            newVersionId = newVersion.getId();//获取新版本Id
            if (latestVersionNum != 0) {//区分新建和更新
                String newFolder = fileUploadDir + "/missiveReceive/" + instanceId + "/" + newVersionNum;//新版本路径 使用流程实例ID
                String oldFolder = fileUploadDir + "/missiveReceive/" + instanceId + "/" + latestVersionNum;//使用流程实例ID
                boolean folderExit = FileOperate.exitFolder(newFolder);//判断新版本路径是否存在

                if (!folderExit) {
                    //如果存在，不对文件夹进行操作
                    //不存在，将就版本整个文件夹拷贝至新版本路径下
                    FileOperate.newFolder(newFolder);
                    FileOperate.copyFolder(oldFolder, newFolder);
                }
            }

            actl = new ArrayList<Attachment>();
            if (!mUploadAttach.equals("")) {
                String[] attachments = mUploadAttach.split("&");
                if (attachments != null) {
                    for (int i = 0; i < attachments.length; i++) {
                /*Boolean fileExist=false;//防止重复上传
                for (int i = 0; i < attachments.length; i++) {
                    for(Attachment existAtt:actl){
                        if(existAtt.getAttachmentTittle().equals(attachments[i])) {
                            fileExist=true;
                        }
                    }
                    if(!fileExist) {*/
                        Attachment actFiles = new Attachment();
                        actFiles.setOriginalFile(true);
                        actFiles.setAttachmentTittle(attachments[i]);
                        actFiles.setAttachmentFilePath(fileBasePath + attachments[i]);
                        actFiles.setMissiveVersion(missVer);
                        actchR.save(actFiles);
                        actl.add(actFiles);
                    }
                }
            }
      /*  } else {
            actl = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getAttachments();
            newVersionNum = latestVersionNum;
            //tempMissiveVersion=mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId());
            newVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getId();
        }*/

        String[] attachs = new String[actl.size()];//附件名列表
        for (int i = 0; i < actl.size(); i++) {
            attachs[i] = actl.get(i).getAttachmentFilePath();
        }
        String htmlUrl = htmlUrlBase + instanceId;
        //转换附件和html
//        logger.info("begin convert Thread::"+new Date().toString());
////        ConvertPdfThread cpThread = new ConvertPdfThread(taskName, "办公室登记", newVersionId.toString(), newVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive",commF);
////        cpThread.run();
//        //commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), latestVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");
//        //ConvertPdf("Receipt",instanceId,taskId);
//
//        logger.info("end convert Thread::"+new Date().toString());
//
//        logger.info("end convert:"+new Date().toString());

        //执行任务
       logger.info("begin complete task");
       MissiveReceiveTaskDealer currentTaskUser = mTaskDealerR.getTaskDealer(instanceId);


        String user;
        User user1=null;
        String nextTaskID="";
        String title="";
        String currentTasker="";
        String currentTaskName="";
        if(mTitle!=null&&!mTitle.equals("")){
            title=mTitle;
        }
        else{
            title=mrsc.getTitle();
        }
        currentTasker = actService.getCurrentTask(Integer.parseInt(taskId.toString())).getAssignee();
        currentTaskName=actService.getCurrentTask(Integer.parseInt(taskId.toString())).getName();
        String currentTaskerName = getName(currentTasker);
        List<Map<String, ? extends Object>> actVars = actService.getNextTaskInfo(String.valueOf(instanceId), Integer.parseInt(taskId.toString()));
        String proDeId = actService.getCurrentTask(Integer.parseInt(taskId.toString())).getProcessDefinitionId();

        if (actVars.size() == 1) {
            Map<String, Object> taskData = new HashMap<String, Object>();
            for (Map<String, ? extends Object> act : actVars) {
                String assigneeVal = act.get("assignee").toString();
//                user = getUserByAssignee(assigneeVal, currentTaskUser);
                user = assName;
                taskData.put(assigneeVal, user);
                user1 = ud.findByUserName(user);
                //this.actService.completeTask(taskId, taskData);
                this.actService.completeTask(taskId,taskData,"missiveReceive",instanceId);
                nextTaskID=actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();


            }
        } else if (actVars.size() == 2) {
            Map<String, Object> taskData = new HashMap<String, Object>();
            for (Map<String, ? extends Object> act : actVars) {
                String condition = act.get("condition").toString();
                if (mTaskIndex.equals(condition)) {
                      if(condition.equals("End")){
                          this.actCont.completeTask(taskId,"",condition,"","missiveReceive",instanceId);
                      }
                    else {
                          String assigneeVal = act.get("assignee").toString();
//                          user = getUserByAssignee(assigneeVal, currentTaskUser);
                          taskData.put("IsPass", condition);
//                          taskData.put(assigneeVal, user);
                          taskData.put(assigneeVal, assName);
                          user1 = ud.findByUserName(assName);
                          this.actService.completeTask(taskId,taskData,"missiveReceive",instanceId);
                          nextTaskID=actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();
                      }

                }
            }
            //this.actService.completeTask(taskId, taskData);


        } else if (actVars.size() == 3) {
            Map<String, Object> taskData = new HashMap<String, Object>();

            for (Map<String, ? extends Object> act : actVars) {
                String condition = act.get("condition").toString();
                if (mTaskIndex.equals(condition)) {
                    if(condition.equals("UndertakeDisposeComp")){
                        this.actCont.completeTask(taskId,"",condition,"","missiveReceive",instanceId);
                    }
                    else if(condition.equals("EndReado")){
                        this.actCont.completeTask(taskId,"","EndReado","","missiveReceive",instanceId);
                        //this.actService.completeTask(taskId, taskData, "missiveReceive", instanceId);


                    }
                    else if(taskName.equals("办公室处理")) {
                        if (Boolean.parseBoolean(act.get("multi").toString())) {
                            String assigneeVal = act.get("multiAssignee").toString();
                            List<String> userlist = new ArrayList<String>();
                            String[] ReadoList = Reado.split("\\|");
                            for (int i = 0; i < ReadoList.length; i++) {
                                userlist.add(ReadoList[i]);
                            }

                            taskData.put(assigneeVal, userlist);
                            // taskData.put(assigneeVal, tempCounterSignUsersList);
                            taskData.put("IsPass", condition);
                            user1 = ud.findByUserName(userlist.get(0).toString());
                            this.actService.completeTask(taskId, taskData, "missiveReceive", instanceId);
                            nextTaskID = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();
                        }else{
                            String assigneeVal = act.get("assignee").toString();
                            user = getUserByAssignee(assigneeVal, currentTaskUser);
                            taskData.put(assigneeVal, user);
                            taskData.put("IsPass", condition);
                            user1 = ud.findByUserName(user);
                            this.actService.completeTask(taskId, taskData, "missiveReceive", instanceId);
                            nextTaskID = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();


                        } }
                    else {
                            String assigneeVal = act.get("assignee").toString();
                            user = getUserByAssignee(assigneeVal, currentTaskUser);
                            taskData.put(assigneeVal, user);
                            taskData.put("IsPass", condition);
                            user1 = ud.findByUserName(user);
                            this.actService.completeTask(taskId, taskData, "missiveReceive", instanceId);
                            nextTaskID = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();
                        }

                }
            }
            //this.actService.completeTask(taskId, taskData);
//            jdbcTemplate.execute("update oa4.act_ru_task set ASSIGNEE_='"+assName+"' where ID_="+taskId);



        }
        mrscr.save(mrsc);
        logger.info("end complete task:"+new Date().toString());
        if(nextTaskID!="") {
            if(currentTaskName.equals("领导批示")){
                try {
                    logger.info("begin send email and message:"+new Date().toString());
                    boolean isSend =user1.getEmailSend();
                    String emalAddr = user1.getEmail();
                    String missiveType="MissiveReceive";
                    //commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), latestVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");

                    Future<String> result =  this.convertService.convertPDFAndSendMailSMS(taskName,mVerR.findOne(newVersionId),newVersionNum,attachs,
                            instanceId,taskId,htmlUrl,missiveType,user1.getEmailSend(),user1.getEmail(),title,
                            nextTaskID,user1,currentTaskerName,this.actService);

                }

                catch (Exception ex){
                    logger.error("web send message or email exception",ex);
                }
                finally {
                    return  "ok";
                }
            }

            else {
                try {
                    logger.info("begin send email and message:"+new Date().toString());
                    boolean isSend =user1.getEmailSend();
                    String emalAddr = user1.getEmail();
                    String missiveType="MissiveReceive";
                    //commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), latestVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");

                    Future<String> result =  this.convertService.convertPDFAndSendMailSMS(taskName,mVerR.findOne(newVersionId),newVersionNum,attachs,
                            instanceId,taskId,htmlUrl,missiveType,user1.getEmailSend(),user1.getEmail(),title,
                            nextTaskID,user1,currentTaskerName,this.actService);
               /* try {
                    result.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/

                /*if(isSend) {
                    //actService.emailSender(proDeId, user1, title, nextTaskID, String.valueOf(instanceId));//发送邮件
                    EmailSenderThread emailSnd = new EmailSenderThread(proDeId,isSend,emalAddr, title, nextTaskID, String.valueOf(instanceId),actService);
                    //DaoService daoService=new DaoService(proDeId,isSend,emalAddr, title, nextTaskID, String.valueOf(instanceId),actService);
                    //daoService.run();
                    emailSnd.run();
                }
                actService.msgSender(user1, title, currentTaskerName);//发送短信提醒
                logger.info("end send emial and message:"+new Date().toString());*/

                }
                catch (Exception ex){
                    logger.error("web send message or email exception",ex);
                }
                finally {
                    return  "ok";
                }
            }
//            try {
//                logger.info("begin send email and message:"+new Date().toString());
//                boolean isSend =user1.getEmailSend();
//                String emalAddr = user1.getEmail();
//                String missiveType="MissiveReceive";
//                //commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), latestVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");
//
//                Future<String> result =  this.convertService.convertPDFAndSendMailSMS(taskName,mVerR.findOne(newVersionId),newVersionNum,attachs,
//                        instanceId,taskId,htmlUrl,missiveType,user1.getEmailSend(),user1.getEmail(),title,
//                        nextTaskID,user1,currentTaskerName,this.actService);
//               /* try {
//                    result.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }*/
//
//                /*if(isSend) {
//                    //actService.emailSender(proDeId, user1, title, nextTaskID, String.valueOf(instanceId));//发送邮件
//                    EmailSenderThread emailSnd = new EmailSenderThread(proDeId,isSend,emalAddr, title, nextTaskID, String.valueOf(instanceId),actService);
//                    //DaoService daoService=new DaoService(proDeId,isSend,emalAddr, title, nextTaskID, String.valueOf(instanceId),actService);
//                    //daoService.run();
//                    emailSnd.run();
//                }
//                actService.msgSender(user1, title, currentTaskerName);//发送短信提醒
//                logger.info("end send emial and message:"+new Date().toString());*/
//
//            }
//            catch (Exception ex){
//                logger.error("web send message or email exception",ex);
//            }
//            finally {
//                return  "ok";
//            }
        }else if(this.actService.isProcessFinished(String.valueOf(instanceId))){

            String missiveType="MissiveReceive";
            Future<String> result =  this.convertService.convertPDF(taskName,mVerR.findOne(newVersionId),newVersionNum,attachs,
                    instanceId,taskId,htmlUrl,missiveType);

        }
        return "ok";
    }


//    @RequestMapping(value="/convertPdf/{processType}/{instanceId}/{taskId}")//转换和合并附件
//    public void convertAndMergePdf(@PathVariable String processType ,@PathVariable Long instanceId,@PathVariable Long taskId){
//       String taskName = actService.getCurrentTask(Integer.parseInt(taskId.toString())).getName();//由于当前任务已经完成，所以根据taskid获取其人物名，两种方法
//        //String taskName=taskDAO.getCurrentTaskName(Integer.parseInt(taskID.toString()));
//        Long latestVersionNum;//最新版本号
//        Long newVersionId;//最新版本Id
//        List<Attachment> actl;//附件列表
//        String proDeId=getCurrentDeId(processType);//获取流程定义Id，区分三个流程
//        if(proDeId.contains("ReceiptId")){
//            latestVersionNum = Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missiveReceive")));//获取最新版本号
//            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
//            Missive missive = mrsc.getMissiveInfo();
//            actl = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getAttachments();//获取附件列表
//            newVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getId();
//            String[] attachs = new String[actl.size()];//附件名列表
//            for (int i = 0; i < actl.size(); i++) {
//                attachs[i] = actl.get(i).getAttachmentFilePath();
//            }
//            String htmlUrl = htmlUrlBase + instanceId;
//            commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), latestVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");
//        }
//        else if(proDeId.contains("PublishMissiveId")) {
//            latestVersionNum= Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missivePublish")));
//            MissivePublish mPublish = mPublishR.findByProcessID(instanceId);
//            Missive missive = mPublish.getMissiveInfo();
//            newVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getId();//获取最新版本Id
//            actl = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getAttachments();//获取附件列表
//            String[] attachs = new String[actl.size()];//附件名列表
//            for (int i = 0; i < actl.size(); i++) {
//                attachs[i] = actl.get(i).getAttachmentFilePath();
//            }
//            String htmlpath="http://localhost:"+hostport+"/missive/missivePublishToPDF/"+instanceId;
//            if (taskName.equals("文印室套红排版打印")) {
//                commF.convertAtt2Pdf2(taskName, "文印室套红排版打印", newVersionId.toString(), latestVersionNum, attachs,instanceId, taskId, htmlpath, "MissivePublish");
//            } else {
//                commF.convertAtt2Pdf2(taskName, "处室拟稿", newVersionId.toString(), latestVersionNum, attachs,instanceId, taskId, htmlpath, "MissivePublish");
//            }
//        }
//        else if(proDeId.contains("Sign")){
//            latestVersionNum= Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missivePublish")));
//            MissiveSign mSign = mSignR.findByProcessID(instanceId);
//            Missive missive = mSign.getMissiveInfo();
//            newVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getId();//获取最新版本Id
//            actl = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missive.getId()).getAttachments();//获取附件列表
//            String[] attachs = new String[actl.size()];//附件名列表
//            for (int i = 0; i < actl.size(); i++) {
//                attachs[i] = actl.get(i).getAttachmentFilePath();
//            }
//            String htmlpath="http://localhost:"+hostport+"/missiveSign/missiveSignToPDF/"+instanceId;//静态页面的地址随着当前任务的完成，可能根据流程号，已找不到指定taskid的页面，可能会出错
//            commF.convertAtt2Pdf2(taskName,"处室拟稿",newVersionId.toString(),latestVersionNum,attachs,instanceId,taskId,htmlpath,"MissiveSign");
//        }
//
//
//
//    }

    //绑定任务的assignee属性和MissiveReceiveTaskDealer类的执行人属性
    public String getUserByAssignee(String assignee,MissiveReceiveTaskDealer mrtd){
        String user="";
        if(assignee.equals("Register")) {
            user=mrtd.getOfficeRegist();
        }
        else if(assignee.equals("Handler")) {
            user=mrtd.getOfficeHandle();
        }
        else if(assignee.equals("Checker")) {
            user=mrtd.getChargerCheck();
        }
        else if(assignee.equals("Signer")) {
            user=mrtd.getLeaderSign();
        }
        else if(assignee.equals("Disposer")) {
            user=mrtd.getOfficeDispose();
        }
        else if(assignee.equals("Reador")) {
            user=mrtd.getReado();
        }
        else if(assignee.equals("ReadoDispose")) {
            user=mrtd.getReadoDispose();
        }
        else if(assignee.equals("UndertakeDisposer")) {
            user=mrtd.getUndertakeDispose();
        }
        return user;
    }


   //pad端完成当前任务
    @RequestMapping(value="/ipad/commitTask/{processDeId}/{instanceId}/{taskId}")
    public @ResponseBody String ipadCommitTask(@PathVariable String processDeId,@PathVariable Long instanceId,@PathVariable Long taskId,@RequestParam String taskValue,@RequestParam String userName,@RequestParam String missiveType){
        try {
            if(userName!=null&&userName!=""){
                User padSelectUser  = ud.findByUserName(userName);
                List<User> lur = new ArrayList<User>();
                lur.add(padSelectUser);
                if(missiveType.equals("missivePublish")){
                        MissivePublish mp = mPublishR.findByProcessID(instanceId);
                        mp.setCounterSignUsers(lur);
                       mPublishR.save(mp);
                }


            }
             String[] attWithPath = null;
             String title="";
             String type="";
             if (processDeId.contains("ReceiptId")) {
                 type=processDeId;
                 MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
                 Missive missiveRec = mrsc.getMissiveInfo();
                 title=mrsc.getTitle();
                 Long latestVersionNum = Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missiveReceive")));//获取最新版本号
                 Long latestVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missiveRec.getId()).getId();
                 List<Attachment> actl = mVerR.findById(latestVersionId).getAttachments();
                 if (actl != null && actl.size() != 0) {
                     attWithPath = new String[actl.size()];
                     for (int i = 0; i < actl.size(); i++) {
                         attWithPath[i] = actl.get(i).getAttachmentFilePath();
                     }
                 }
                 String htmlUrl = htmlUrlBase + instanceId;
                 ConvertPdfThread cpThread = new ConvertPdfThread("1", "2", latestVersionId.toString(), latestVersionNum, attWithPath, instanceId, taskId, htmlUrl, "missiveReceive",commF);
                 cpThread.run();
                // commF.convertAtt2Pdf2("1", "2", latestVersionId.toString(), latestVersionNum, attWithPath, instanceId, taskId, htmlUrl, "missiveReceive");//附件不进行转化
             } else if (processDeId.contains("PublishMissiveId")) {
                 type="missivePublish";
                 MissivePublish mp = mPublishR.findByProcessID(instanceId);
                 title=mp.getMissiveTittle();
                 Missive missivePub = mp.getMissiveInfo();
                 Long latestVersionNum = Long.parseLong(String.valueOf(fileupc.getMaxMissiveVersion(instanceId, "missivePublish")));//获取最新版本号
                 Long latestVersionId = mVerR.getVersionByLastVersionAndMissiveID(latestVersionNum, missivePub.getId()).getId();
                 List<Attachment> actl = mVerR.findById(latestVersionId).getAttachments();
                 if (actl != null && actl.size() != 0) {
                     attWithPath = new String[actl.size()];
                     for (int i = 0; i < actl.size(); i++) {
                         attWithPath[i] = actl.get(i).getAttachmentFilePath();
                     }
                 }
                 String htmlpath="http://localhost:"+hostport+"/missive/missivePublishToPDF/";
                 htmlpath+=instanceId;
                 ConvertPdfThread cpThread = new ConvertPdfThread("1", "2", latestVersionId.toString(), latestVersionNum, attWithPath, instanceId, taskId, htmlpath, "missivePublish",commF);
                 cpThread.run();
                 //commF.convertAtt2Pdf2("1", "2", latestVersionId.toString(), latestVersionNum, attWithPath, instanceId, taskId, htmlpath, "missivePublish");//附件不进行转化


             }


             ArrayList<String> user =new ArrayList<String>();
             String mTaskIndex = "";
             User userD=null;
             List<Map<String, ? extends Object>> actVars = actService.getNextTaskInfo(String.valueOf(instanceId), Integer.parseInt(taskId.toString()));
             Task currentTask = actService.getCurrentTask(Integer.parseInt(taskId.toString()));
             if (actVars.size() == 1) {
                 Map<String, Object> taskData = new HashMap<String, Object>();
                 for (Map<String, ? extends Object> act : actVars) {
                     String assigneeVal = act.get("assignee").toString();
                     if (processDeId.contains("ReceiptId")) {
                         MissiveReceiveTaskDealer currentTaskUser = mTaskDealerR.getTaskDealer(instanceId);
                         user.add(getUserByAssignee(assigneeVal, currentTaskUser));

                     }
                     else if(processDeId.contains("PublishMissiveId"))
                     {
                         assigneeVal=act.get("assignee").toString();
                         user= commF.getAssigneeByAssigneeMap("missivePublish",instanceId,assigneeVal);

                     }
                     else if(processDeId.contains("Sign")){
                         assigneeVal=act.get("assignee").toString();
                         user= commF.getAssigneeByAssigneeMap("missiveSign",instanceId,assigneeVal);

                     }
                     if(user!=null) {
                         taskData.put(assigneeVal, user.get(0));

                     }
                     else{
                         taskData.put(assigneeVal, "");
                     }
                     this.actService.completeTask(taskId, taskData);
                 }

             } else if (actVars.size() == 2) {
                 Map<String, Object> taskData = new HashMap<String, Object>();
                 for (Map<String, ? extends Object> act : actVars) {
                     String condition = act.get("condition").toString();
                     if (taskValue.equals(condition)) {
                         if (act.size() == 2) {
                             taskData.put("IsPass", condition);
                         } else {

                             String assigneeVal="";
                             if (processDeId.contains("ReceiptId")) {
                                 assigneeVal = act.get("assignee").toString();
                                 MissiveReceiveTaskDealer currentTaskUser = mTaskDealerR.getTaskDealer(instanceId);
                                 user.add( getUserByAssignee(assigneeVal, currentTaskUser));

                             }
                             else if(processDeId.contains("PublishMissiveId")) {
                                 String index = act.get("multi").toString();
                                 if(index.equals("true")){
                                     assigneeVal=act.get("multiAssignee").toString();
                                     user= commF.getAssigneeByAssigneeMap("missivePublish",instanceId,assigneeVal);

                                     taskData.put(assigneeVal, user);
                                 }
                                 else{
                                     assigneeVal=act.get("assignee").toString();
                                     user= commF.getAssigneeByAssigneeMap("missivePublish",instanceId,assigneeVal);

                                     taskData.put(assigneeVal, user.get(0));
                                 }
                             }


                             taskData.put("IsPass", condition);

                         }
                     }
                 }
                 this.actService.completeTask(taskId, taskData);
             } else if (actVars.size() == 3) {
                 Map<String, Object> taskData = new HashMap<String, Object>();

                 for (Map<String, ? extends Object> act : actVars) {
                     String condition = act.get("condition").toString();
                     if (taskValue.equals(condition)&&!taskValue.equals("EndReado")) {
                         String assigneeVal = act.get("assignee").toString();
//                         if (processDeId.contains("ReceiptId")) {
//                             MissiveReceiveTaskDealer currentTaskUser = mTaskDealerR.getTaskDealer(instanceId);
//                             user.add( getUserByAssignee(assigneeVal, currentTaskUser));
//
//                             taskData.put(assigneeVal, user);
//                         }

                         if (processDeId.contains("ReceiptId")) {
                             if(assigneeVal.equals("Disposer")){
                                 MissiveReceiveTaskDealer currentTaskUser = mTaskDealerR.getTaskDealer(instanceId);
                                 user.add( getUserByAssignee(assigneeVal, currentTaskUser));

                                 taskData.put(assigneeVal, user.get(0));
                             }else{
                                 taskData.put(assigneeVal, userName);

                             }

                         }
                         else if(processDeId.contains("PublishMissiveId")) {
                             String index = act.get("multi").toString();
                             if(index.equals("true")){
                                 assigneeVal=act.get("multiAssignee").toString();
                                 user= commF.getAssigneeByAssigneeMap("missivePublish",instanceId,assigneeVal);
                                 taskData.put(assigneeVal, user);
                             }
                             else{
                                 assigneeVal=act.get("assignee").toString();
                                 user= commF.getAssigneeByAssigneeMap("missivePublish",instanceId,assigneeVal);
                                 taskData.put(assigneeVal, user.get(0));
                             }
                         }
                         else if(processDeId.contains("Sign")){
                             String index = act.get("multi").toString();
                             if(index.equals("true")){
                                 assigneeVal=act.get("multiAssignee").toString();
                                 user= commF.getAssigneeByAssigneeMap("missiveSign",instanceId,assigneeVal);

                                 taskData.put(assigneeVal, user);
                             }
                             else{
                                 assigneeVal=act.get("assignee").toString();
                                 user= commF.getAssigneeByAssigneeMap("missiveSign",instanceId,assigneeVal);

                                 taskData.put(assigneeVal, user.get(0));
                             }
                         }


                         taskData.put("IsPass", condition);
                     }
                 }
                 if(user!=null&&user.size()!=0) {
                     userD = ud.findByUserName(user.get(0));
                 }
                 Task task = actService.getCurrentTasksByProcessInstanceId(instanceId);
                 String currentUser  = getName(task.getAssignee());

                 this.actService.completeTask(taskId, taskData);
                 String nextTaskID=actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceId))).getId();
                 if(nextTaskID!=null&&nextTaskID!="") {
                     try {
                         boolean isSend = userD.getEmailSend();
                         if(isSend) {
                             String emailAddr = userD.getEmail();
                           EmailSenderThread emailSnd = new EmailSenderThread(type,isSend,emailAddr, title,nextTaskID, String.valueOf(instanceId),actService);
                         emailSnd.run();
                             //actService.emailSender(type, userD, title, nextTaskID, String.valueOf(instanceId));//发送邮件
                         }
                         actService.msgSender(userD, title, currentUser);//发送短信提醒
                     }
                     catch (Exception ex){
                         logger.error("send email or meaaage exception",ex);
                         return  "ok";
                     }
                 }
             }
              return  "ok";
         }
         catch (Exception ex){
             logger.error("ipadCommitTask exception:",ex);
               return "error";
         }
     }

    //移动端获取已办公文
    @RequestMapping(value="/ipad/getDoneMissive/{userName}/{pageSize}/{pageNum}")
    public List<PadDoneMissiveInfo> getDoneMissiveByName(@PathVariable String userName,@PathVariable int pageSize,@PathVariable int pageNum){
        PageableHistoryTaskList historyTasks= actService.getProcessDoneByUserName(userName,pageSize,pageNum);
        List<TaskForm> lftDone=new ArrayList<TaskForm>();
        List<PadDoneMissiveInfo> missives = new ArrayList<PadDoneMissiveInfo>();
        if (historyTasks.getTasklist() != null) {
            lftDone = mmc.getTaskFormByHistroyTask(historyTasks.getTasklist());
            for(TaskForm tf:lftDone){
                String missiveAddr ="download/pdf/"+tf.getMissiveType()+"/"+tf.getProcessInstanceId()+"/"+tf.getId()+".pdf";//"/download/pdf/{fileType}/{instanceID}/{fname}.{filetype}/exists"
                PadDoneMissiveInfo padMissive = new PadDoneMissiveInfo(tf.getId(),tf.getMissiveTitle(),tf.getName(),tf.getTaskEndTime(),missiveAddr,tf.getUrgencyLevel());
                missives.add(padMissive);
            }
        }
        return  missives;
    }


    //获取公文标题

    @RequestMapping(value="/getTitle",produces = "text/html;charset=utf-8")
    public  String getTitle(Long instanceid){
        MissiveRecSeeCard mrsc = mrscr.getMissData(instanceid.toString());
        String title=mrsc.getTitle();
        return title;
    }


    //获取用户
    @RequestMapping(value="/receiveMissive/getUser")
    public List<UserFrom> getAllUser(){
        MissiveReceiveFunction mrf=new MissiveReceiveFunction();
        List<UserFrom> lists=new ArrayList<UserFrom>();
        List<User> users = ud.findAll();
        /*List<User> users=ud.findAll();*/
        if(users!=null&&users.size()!=0) {
            for (User user : users) {
                //UserFrom uf = mrf.userToUserForm(user);
                UserFrom uf =mpf.userToUserForm(user);
                String name = user.getName();
                String userName = user.getUserName();
                String groupName="";
                if(user.getGroup()!=null) {
                    groupName = user.getGroup().getGroupName();
                    uf.setName(name + " -- " + groupName);
                }
                uf.setUserName(userName);
                lists.add(uf);
            }
        }
        return lists;
    }

    @RequestMapping(value="/pad/getUser")
    public List<User4Pad> getPadUser(){
        MissiveReceiveFunction mrf=new MissiveReceiveFunction();

        List<User4Pad> lists=new ArrayList<User4Pad>();
        List<User> users = ud.findAll();
        if(users!=null&&users.size()!=0) {
            for (User user : users) {
                User4Pad up = new User4Pad();
                up.setName(user.getName());
                up.setId(user.getId());
                up.setUserName(user.getUserName());
                if(user.getGroup()!=null) {
                    up.setGroupName(user.getGroup().getGroupName());
                }
                else{
                    up.setGroupName("");
                }
                lists.add(up);
            }
        }
        return lists;
    }

    //加载背景图片
    @RequestMapping(value="/bgPng/{processType}/{instanceid}")
    public ResponseEntity<byte[]> downFile(@PathVariable String processType,@PathVariable String instanceid) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String bgPng="";
        if(processType.contains("missiveReceive")) {
            MissiveRecSeeCard mrsc = mrscr.getMissData(instanceid);
            bgPng = mrsc.getBgPngPath();
        }
        else if (processType.contains("missivePublish")){
            MissivePublish faxPub=mPublishR.findByProcessID(Long.parseLong(instanceid));
            bgPng=faxPub.getBackgroudImage();
        }


        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", bgPng.substring(bgPng.lastIndexOf("/")+1));

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(bgPng)),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("downFile exception:",e);
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }


    //加载背景图片
    @RequestMapping(value="/bgPng/small/{processType}/{instanceid}")
    public ResponseEntity<byte[]> downSmallFile(@PathVariable String processType,@PathVariable String instanceid) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String bgPng="";
        if(processType.contains("missiveReceive")) {
            MissiveRecSeeCard mrsc = mrscr.getMissData(instanceid);
            bgPng = mrsc.getBgPngPath();
        }

        if (bgPng != null && !bgPng.equals("")){

            String smallBgPng = bgPng.substring(0,bgPng.length()-4)+"_small.PNG";

            if (!FileOperate.exitFile(smallBgPng))
            {
                try {
                    Thumbnails.of(bgPng).scale(0.2).outputFormat("png")
                            .toFile(smallBgPng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", smallBgPng.substring(smallBgPng.lastIndexOf("/")+1));

            try {
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(smallBgPng)),
                        headers,
                        HttpStatus.OK);
            } catch (Exception e) {
                logger.error("downFile exception:",e);
                e.printStackTrace();
                //日志
                //TODO
            }
        }


        //Http响应头

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }

    //获取代办任务数量
    @RequestMapping(value="/getTaskNum/{userName}")
    public Long getTaskCount(@PathVariable String userName){
        return actService.getCurrentTasksByUser(userName);
    }

    //根据用户名获取用户姓名
    @RequestMapping(value="/getNameByUserName/{userName}")
    public String getName(@PathVariable String userName){
        return ud.findByUserName(userName).getName();
    }



}
