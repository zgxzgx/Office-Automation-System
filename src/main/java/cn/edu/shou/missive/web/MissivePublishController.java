package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.*;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.api.MissiveReceiveController;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by sqhe on 14-7-19.
 */
@Controller
@RequestMapping(value = "/missive")
@SessionAttributes(value = {"userbase64", "user"})
public class MissivePublishController {
    @Value("${spring.main.uploaddir}")
    String fileUploadDir;

    @Value("${server.port}")
    String hostport;
    @Value("${spring.main.pdfBoxTool}")
    String pdfBoxTool;
    @Autowired
    private CommonFunction cmmF;

//    FileOperate fileOperate=new FileOperate();

    private final static Logger logger = LoggerFactory.getLogger(MissivePublishController.class);


    MissivePublishFunction mpf=new MissivePublishFunction();
   // CommonFunction cmmF=new CommonFunction();
   @Autowired
   private JdbcTemplate jdbcTemplate;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private MissivePublishRepository mpDAO;

    @Autowired
    private UserRepository ur;
    @Autowired
    private GroupRepository gr;
    @Autowired
    private MissiveRepository mr;
    @Autowired
    private CommentContentRepository ccr;
    @Autowired
    private MissiveVersionRepository mvr;
    @Autowired
    private AttachmentRepository attachr;
    @Autowired
    private MissiveTypeRepository mtr;
    @Autowired
    private SecretLevelRespository slr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private ActivitiController actR;//流程服务

    @Autowired
    private DIYphaseController diyPh;

    @Autowired
    private UserDAO ud;

    @Autowired
    private FileUploadController fileupc;

    @Autowired
    private UserConfigRepository userConfigR;
    @Autowired
    private MissiveReceiveController mrc;
    @Autowired
    private SignatureVisibleRepositoty signVisibleR;
    @Autowired
    private GroupDAO groupDAO;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ConvertService convertService;
    @Autowired
    private publishRepository pr;
    @Autowired
    private CommonFunction commF;



    @RequestMapping(value = "/missive/getmissiveID",produces = "text/html;charset=UTF-8")
    public String getmissiveID(){
        List<publish>psT;
        String json=null;
        psT=pr.findAll();
        json=commF.getJsonDataByObject(psT);
        return json;
    }



    @RequestMapping(value="/missivePublish/{intanceid}/{taskid}", method= RequestMethod.GET)
    public String index(@PathVariable int intanceid,@PathVariable int taskid,Model model,@AuthenticationPrincipal User currentUser){
        logger.info(currentUser.userName + " visited missivePublsh PROC_INST_ID:"+ intanceid + " TASKID="+taskid);
        model.addAttribute("instanceID",intanceid);
        model.addAttribute("taskID",taskid);
        model.addAttribute("currentEditableField",taskDAO.getCurrentEditableFieldsByTaskId(taskid,1));
        model.addAttribute("currentTaskName",taskDAO.getCurrentTaskName(taskid));

        SignatureVisible signatureVisible=signVisibleR.findByProcessID(intanceid);
        model.addAttribute("signatureVisible",signatureVisible);

        List<Map<String,? extends Object>> activitiNextStepInfo =new ArrayList<Map<String, ? extends Object>>();

        activitiNextStepInfo =this.actService.getNextTaskInfo(String.valueOf(intanceid),taskid);

        model.addAttribute("activitiNextStepInfo",activitiNextStepInfo);
        MissivePublish missivePublish=mpDAO.findByProcessID(intanceid);//------------->missivePublish add
        MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);

        int maxMV=0;
        String missiveNum=null;
        if(missivePublishForm.missiveInfo!=null){
            missiveNum=missivePublishForm.missiveInfo.missiveNum;
        }
        if(missivePublishForm.missiveInfo!=null&&missivePublishForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missivePublishForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missivePublishForm.missiveInfo.versions=mvform;
        }

//        List<Long> mvnum=new ArrayList<Long>();
//        for(MissiveVersionFrom mvf:mvfs){
//            mvnum.add(mvf.versionNumber);
//        }
//        int maxVN= Integer.parseInt(Collections.max(mvnum).toString());
        String remarkList="";
        //List resultList=this.jdbcTemplate.queryForList("SELECT TIME_,USER_ID_,MESSAGE_ FROM oa3.act_hi_comment where ACTION_='备注' and PROC_INST_ID_="+intanceid);
        //List resultList=this.jdbcTemplate.queryForList("SELECT a.MESSAGE_,a.USER_ID_ , b.NAME_,a.TIME_ FROM oa3.act_hi_comment as a,oa3.act_hi_taskinst as b where a.PROC_INST_ID_=b.PROC_INST_ID_ and a.ACTION_='备注' and a.PROC_INST_ID_="+intanceid);
        List resultList=this.jdbcTemplate.queryForList("SELECT a.MESSAGE_,a.TASK_ID_,a.USER_ID_ , b.NAME_,a.TIME_ FROM oa3.act_hi_comment as a,oa3.act_hi_taskinst as b where a.Task_ID_=b.ID_ and a.ACTION_='备注' and a.PROC_INST_ID_="+intanceid);
//        for(int i=0;i<resultList.size();i++){
//            if(i==0){
//                remarkList=resultList.get(i).toString();
//            }else remarkList+=resultList.get(i);
//
//        }

        Iterator it = resultList.iterator();
        while(it.hasNext()) {
            Map userMap = (Map) it.next();


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
            String nowTime=format.format(userMap.get("TIME_"));
            //String userName=ur.findByUserName(userMap.get("USER_ID_").toString()).getName();
            String userName=ur.findByUserName(userMap.get("USER_ID_").toString()).getName();

            //remarkList+=userName+":"+userMap.get("a.MESSAGE_").toString()+"\n";
            remarkList+=userMap.get("NAME_").toString()+","+nowTime+","+userName+":"+userMap.get("MESSAGE_").toString()+"\n"+"+";

        }
        model.addAttribute("remarkList", remarkList);


        model.addAttribute("missivePublishForm", missivePublishForm);

        List<String> missiveNumUsed=new ArrayList<String>();
        missiveNumUsed=mr.getMissiveNumUsed();
        if(missiveNum!=null&&!(missiveNum.equals("||"))){
            for(int i=0;i<missiveNumUsed.size();i++){
                if(missiveNum.equals(missiveNumUsed.get(i))){
                    missiveNumUsed.remove(i);
                    break;
                }
            }
        }

        model.addAttribute("missiveNumUsed",missiveNumUsed);

//        List<UserSecret> userDataSource =  new ArrayList<UserSecret>();
//        List<User> users=ur.findAll();
//        for (User user:users){
//            UserSecret userFrom=mpf.userToUserForm_Name_UserName(user);
//            userDataSource.add(userFrom);
//        }
//        model.addAttribute("userDataSource",userDataSource);
        model.addAttribute("userDataSource",mpf.userToUserForm_Name_UserName(ud.getAllUserNameAndGroupName()));

//        List<UserSecret> officeUserDataSource =  new ArrayList<UserSecret>();
//        List<User> officeusers=ur.findAll();
//        for (User user:officeusers){
//            UserSecret userFrom=mpf.userToUserForm_Name_UserName(user);
//            if(userFrom.GroupName!=null&&userFrom.GroupName.equals("办公室")){
//                officeUserDataSource.add(userFrom);
//            }
//        }
//        model.addAttribute("officeUserDataSource",officeUserDataSource);

        UserSecret officeCheckUser=new UserSecret();//办公室审核人，从配置表中读取
        String officeCheckUserName=null;
        if(userConfigR.findByName("missivePublish_officeCheckUser")!=null){
            officeCheckUserName=userConfigR.findByName("missivePublish_officeCheckUser").getValue();
            officeCheckUser=mpf.userToUserForm_Name_UserName(ur.findByUserName(officeCheckUserName));
        }
        model.addAttribute("officeCheckUser",officeCheckUser);

        UserSecret printingUser=new UserSecret();//文印室人员，从配置表中读取
        String printingUserName=null;
        if(userConfigR.findByName("missivePublish_printingUser")!=null){
            printingUserName=userConfigR.findByName("missivePublish_printingUser").getValue();
            printingUser=mpf.userToUserForm_Name_UserName(ur.findByUserName(printingUserName));
        }
        model.addAttribute("printingUser",printingUser);

        //自定义短语
        List<String> phases =diyPh.getPhaseByUserId(currentUser.getId());
        model.addAttribute("phases",phases);

//        List<GroupFrom> groupDataSource= new ArrayList<GroupFrom>();
//        List<Group> groups=gr.findAll();
//        for(Group g:groups){
//            GroupFrom gf=mpf.groupToGroupFrom(g);
//            if(gf.groupName!=null&&(!gf.groupName.equals("未分组"))){
//                groupDataSource.add(gf);
//            }
//        }


        model.addAttribute("groupDataSource",mpf.convertGroupListToGFList(groupDAO.getAllGroupsJDBC()));
        List<MissiveTypeFrom> missiveTypeDataSource=new ArrayList<MissiveTypeFrom>();
        List<MissiveType> mts=mtr.findAll();
        for(MissiveType mt:mts){
            MissiveTypeFrom mtf=mpf.missiveTypeToMissiveTypeFrom(mt);
            missiveTypeDataSource.add(mtf);
        }
        model.addAttribute("missiveTypeDataSource",missiveTypeDataSource);
        List<publish>psT;
        String json=null;
        psT=pr.findAll();
        model.addAttribute("missiveIdDataSource",psT);


        List<SecretLevelFrom> secretLevelDataSource=new ArrayList<SecretLevelFrom>();
        List<SecretLevel> sls=slr.findAll();
        for(SecretLevel sl:sls){
            SecretLevelFrom slf=mpf.secretLevelToSecretLevelFrom(sl);
            secretLevelDataSource.add(slf);
        }
        model.addAttribute("secretLevelDataSource",secretLevelDataSource);

        //List<String> uu=jdbcTemplate.query("'SELECT * FROM oa3.act_hi_comment where PROC_INST_ID_=('+intanceid+')'");
        //List<String> uu=jdbcTemplate.query("'SELECT * FROM oa3.act_hi_comment where PROC_INST_ID_='+intanceid+')'");
       //List<String> uu=jdbcTemplate.execute("SELECT * FROM oa3.act_hi_comment where PROC_INST_ID_=('"+intanceid+"')");

        //model.addAttribute("remark",);


        User cu = ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(cu);
        model.addAttribute("currentUser", userFrom);
        model.addAttribute("user", currentUser);
        model.addAttribute("hasBgPng",(missivePublish.getBackgroudImage()!=null && !missivePublish.getBackgroudImage().equals("")));
        String assigeeUserName=taskDAO.getTaskAssigeeUserName(intanceid,taskid);


        if(assigeeUserName!=null&&assigeeUserName.equals(currentUser.getUserName())){
            return "missivePublish";
        }
        else {
            return "index";
        }
    }

    @RequestMapping(value="/setMissivePublish", method= RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public  @ResponseBody String setMissivePublish(
            @Param("UserName") String UserName               //用戶名
            ,@Param("remark") String remark
            ,@Param("instanceID") Long instanceID             //實例ID
            ,@Param("taskID") Long taskID                   //任務ID
            //----------寫入公文信息
            ,@Param("missiveNum") String missiveNum         //公文號  賦值給公文Missive的missiveNum
            ,@Param("secretLevel") Long secretLevel         //公文密級    根據密級id查詢出密級賦值給公文Missive的secretLevel
            ,@Param("typeName") Long typeName           //公文類型ID    根據類型ID查出類型賦值給公文Missive的 missiveType
            //versions
            ,@Param("docFilePath") String docFilePath       // 賦值給公文的版本versions   最後公文賦值給公文發佈表單
            ,@Param("pdfFilePath") String pdfFilePath
            ,@Param("missiveTittle") String missiveTittle

//            ,@Param("content") String content
            //,@Param("missiveId") String missiveId  //
            ,@Param("name") String name   //公文名

            ,@Param("signIssueUser") String signIssueUserName   //签发人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("signIssueBase30url") String signIssueBase30url
            ,@Param("signIssueImageurl") String signIssueImageurl
            ,@Param("signIssueJSBase30url") String signIssueJSBase30url
            ,@Param("signIssueJSImageurl") String signIssueJSImageurl
            ,@Param("signIssue_Content") String signIssue_Content     //Add to CommentContent then to signIssueContent

            ,@Param("counterSignUsers") String counterSignUsersName   //会签人员  最终根据查出List<User>赋值给missivePublish
            ,@Param("counterSignBase30url") String counterSignBase30url
            ,@Param("counterSignImageurl") String counterSignImageurl
            ,@Param("counterSignJSBase30url") String counterSignJSBase30url
            ,@Param("counterSignJSImageurl") String counterSignJSImageurl
            ,@Param("counterSign_Content") String counterSign_Content     //Add to CommentContent then to counterSign_Content


            ,@Param("mainSendGroups") String mainSendGroups      //主送部门   根据username查出ID赋值给  //Add to CommentContent then to counterSign_Content
            ,@Param("copytoGroups") String copytoGroups      //--抄送部门
            ,@Param("sponsorUnit") String sponsorUnit    //主办单位
            ,@Param("phoneNum") String phoneNum    //电话
            ,@Param("drafter") String drafter    //拟稿人
            ,@Param("composeUser") String composeUser  //排版人
            ,@Param("dep_LeaderCheck") String dep_LeaderCheck  //处(室)领导核稿
            ,@Param("dep_LeaderCheck_img") String dep_LeaderCheck_img
            ,@Param("dep_LeaderCheck_Base30") String dep_LeaderCheck_Base30
            ,@Param("dep_LeaderCheckJS_img") String dep_LeaderCheckJS_img
            ,@Param("dep_LeaderCheckJS_Base30") String dep_LeaderCheckJS_Base30
            ,@Param("dep_LeaderCheck_Content") String dep_LeaderCheck_Content



//            ,@Param("dep_LeaderCheck") String dep_LeaderCheck  //处(室)领导核稿
            ,@Param("officeCheck") String officeCheck  //办公室复核
            ,@Param("printCount") int printCount  //打印份数
            ,@Param("CheckReader") String CheckReader  ////校对人
            ,@Param("Printer") String Printer  ////校对人Printer
            ,@Param("Gov_info_attr") int Gov_info_attr  ////政府信息属性
            ,@Param("appendTittle") String appendTittle  ////附件标题
            ,@Param("appendName") String appendName  ////附件内容
            ,@Param("appendPath") String appendPath  ////附件内容
            ,@Param("activitiVariables") String activitiVariables//
            ,@Param("dep_LeaderCheckSuggestion") String dep_LeaderCheckSuggestion//
            ,@Param("officeCheckSuggestion") String officeCheckSuggestion//
            ,@Param("mainSend") String mainSend//主送外单位 郑小罗 2014/12/25
            ,@Param("copyTo") String copyTo//主送外单位 郑小罗 2014/12/25
            ,@Param("depLeaderCheckSignature") String depLeaderCheckSignature
            ,@Param("officeCheckSignature") String officeCheckSignature
            ,@Param("printingSignature") String printingSignature

    ) throws IOException {
        //String Tittle=missiveTittle.replace("<br>", " ");
        String Tittle=missiveTittle;
        SignatureVisible signatureVisible=signVisibleR.findByProcessID(instanceID);
        if(signatureVisible==null){
            signatureVisible=new SignatureVisible();
            signatureVisible.setProcessID(instanceID);
        }
        if(depLeaderCheckSignature!=null&&!(depLeaderCheckSignature.equals(""))){
            signatureVisible.setDepLeaderCheckUser(depLeaderCheckSignature);
        }
        if(officeCheckSignature!=null&&!(officeCheckSignature.equals(""))){
            signatureVisible.setOfficeCheckUser(officeCheckSignature);
        }
        if(printingSignature!=null&&!(printingSignature.equals(""))){
            signatureVisible.setComposeUser(printingSignature);
            signatureVisible.setCheckReaderUser(printingSignature);
        }
        signVisibleR.save(signatureVisible);
//        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime=String.valueOf(format.format(new Date()));
        if(!remark.equals("")){
            this.jdbcTemplate.execute("INSERT INTO oa3.act_hi_comment(ID_,TIME_,USER_ID_,TASK_ID_,PROC_INST_ID_,ACTION_,MESSAGE_) VALUES ('"+taskID+"','"+nowTime+"','"+UserName+"','"+taskID+"','"+instanceID+"','备注','"+remark+"')");

        }


        String realPath = fileUploadDir+"/missivePublish/";
//        int realPathLen=58;//路径字符数量
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String newFolder="";//新版本路径
        String oldFolder="";
        MissivePublish tempMissivePublish;
        MissivePublish currentTempMissivePublish = new MissivePublish();//用于判断是否第一次创建公文表
        Missive tempMissive;//<---------------------------------公文
        MissiveVersion tempMissiveVersion;
        CommentContent tempSignIssueCommentContent;  //签发内容
        CommentContent tempCounterSignCommentContent;//会签内容

        CommentContent tempdep_LeaderCheckCommentContent;  //部门领导意见内容
//        String[] attachmentFilePathList=null;//附件路径列表
        List<String>  attachmentFilePathList=new ArrayList<String>();
        List<Attachment> attachmentList;   //孙乐 02-10
        long fileVersionNum = 0;//默认文件版本为0+1
        currentTempMissivePublish=mpDAO.findByProcessID(instanceID);

        if(currentTempMissivePublish!=null){   //如果MissivePublish表存在，则不创建表    如果不存在说明是第一次创建
            tempMissivePublish=currentTempMissivePublish;//则不创建表
            tempMissive=tempMissivePublish.getMissiveInfo();//Missive
            if(tempMissive==null)  tempMissive=new Missive();
            tempSignIssueCommentContent = tempMissivePublish.getSignIssueContent();  //获取签发内容 之后修改内容
            if(tempSignIssueCommentContent==null)  tempSignIssueCommentContent=new CommentContent();
            tempCounterSignCommentContent = tempMissivePublish.getCounterSignContent();//获取会签内容 之后修改内容
            if(tempCounterSignCommentContent==null) tempCounterSignCommentContent=new CommentContent();
            tempdep_LeaderCheckCommentContent = tempMissivePublish.getDep_LeaderCheckContent();  //
            if(tempdep_LeaderCheckCommentContent==null)  tempdep_LeaderCheckCommentContent=new CommentContent();
            List<String> currentEditableField=taskDAO.getCurrentEditableFieldsByTaskId(Integer.parseInt(taskID.toString()),1);
            if(currentEditableField.contains("missiveTittle")||
                    currentEditableField.contains("appendTittle")||
                    currentEditableField.contains("initiativePublic")||
                    currentEditableField.contains("applyForPublic")||
                    currentEditableField.contains("forbiddenPublic")||
                    currentEditableField.contains("printCount")||
                    currentEditableField.contains("copyTo_Person")||
                    currentEditableField.contains("attachments")){        //以下内容只有在任务回到拟稿才会创建新的公文版本
                //<---------------------------------公文

//                User tempUser=ur.findByUserName(UserName);
//                tempMissive.setMissiveCreateUser(tempUser);//missive增加creatUser
//                tempMissive.setName(name);
//                mr.save(tempMissive);//-------------------save

                tempMissiveVersion=new MissiveVersion();//创建新的公文版本
                tempMissiveVersion.setDocFilePath(docFilePath);//公文doc路径
                tempMissiveVersion.setPdfFilePath(pdfFilePath);//公文pdf
                tempMissiveVersion.setMissiveTittle(Tittle);//公文标题


                //点击提交按钮后，首先判断新版本下文件夹是否存在，如果存在则将就版本的文件拷贝到新版本文件夹 否则将整个文件夹拷贝至新版本
                boolean folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在

//            MissivePublish missivePublish=mpr.findByProcessID(instanceID);//获取最大版本号
                if(tempMissivePublish!=null){
                    if(tempMissivePublish.getMissiveInfo()!=null){
                        if(tempMissivePublish.getMissiveInfo().getVersions()!=null){
                            List<MissiveVersion> missiveVersions = tempMissivePublish.getMissiveInfo().getVersions();
                            if(tempMissivePublish.getMissiveInfo().getVersions().size()!=0){
                                List<Long> mvnum=new ArrayList<Long>();
                                for(MissiveVersion mv:missiveVersions){
                                    mvnum.add(mv.getVersionNumber());
                                }
                                fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                            }
                        }
                    }
                }
                oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
                fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
                newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新版本路径


                tempMissiveVersion.setVersionNumber(fileVersionNum);//公文版本号
                tempMissiveVersion.setMissive(tempMissive);
                mvr.save(tempMissiveVersion);//存入数据库-----------

//
                // ------------------------//<-------------附件
                if (fileVersionNum!=1){
                    folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
                    if (!folderExit) {
                        //如果存在，不对文件夹进行操作
                        //不存在，将就版本整个文件夹拷贝至新版本路径下
                        FileOperate.newFolder(newFolder);
                        FileOperate.copyFolder(oldFolder, newFolder);
                    }
                }
                else {
//               do nothing
                    if (!folderExit) {
                        //如果存在，不对文件夹进行操作
                        //不存在，将就版本整个文件夹拷贝至新版本路径下
                        FileOperate.newFolder(newFolder);
//                        FileOperate.copyFolder(oldFolder,newFolder);
                    }
                }
                String[] tempappendNames=appendName.split("\\|");
                if(tempappendNames.length!=0){
                    for(int i=0;i<tempappendNames.length;i++){
                        if(!(tempappendNames[i].equals(""))){
                            Attachment tempAttachment=new Attachment();
                            tempAttachment.setAttachmentTittle(tempappendNames[i]);
                            tempAttachment.setAttachmentFilePath(newFolder+tempappendNames[i]);
                            tempAttachment.setMissiveVersion(tempMissiveVersion);
                            attachr.save(tempAttachment);//存入数据库-------------
//                            attachmentFilePathList[i]=tempAttachment.getAttachmentFilePath();//生成附件列表的路径为数组-----给邓豪接口用
                            attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用
                        }
                    }
                }


            }
            else{
                int maxMV=0;
//                if(tempMissivePublish.getMissiveInfo()!=null&&tempMissivePublish.getMissiveInfo().getVersions()!=null) {
                    List<MissiveVersion> mvs = tempMissivePublish.getMissiveInfo().getVersions();
                    for (int i = 0; i < mvs.size() - 1; i++) {
                        if (mvs.get(i).getVersionNumber() > mvs.get(i + 1).getVersionNumber()) {
                            maxMV = i;
                        } else maxMV = i + 1;
                    }
                    tempMissiveVersion = mvs.get(maxMV);
//                fileVersionNum=Long.parseLong(String.valueOf(tempMissiveVersion.getVersionNumber()));
                fileVersionNum=tempMissiveVersion.getVersionNumber();
//                }
//                不创建新的公文版本
                attachmentList = mvr.getVersionByLastVersionAndMissiveID(fileVersionNum, tempMissive.getId()).getAttachments();  //孙乐 02-10
                //attachmentFilePathList = new String[attachmentList.size()];//附件名列表
                for (int i = 0; i < attachmentList.size(); i++) {
                    attachmentFilePathList.add(attachmentList.get(i).getAttachmentFilePath());
                }
            }

        }
        else{
            tempMissivePublish=new MissivePublish();
            tempMissive=new Missive();
            tempSignIssueCommentContent = new CommentContent();  //会签内容
            tempCounterSignCommentContent=new CommentContent();//会签内容
            tempdep_LeaderCheckCommentContent = new CommentContent();



            //<---------------------------------公文
            User tempUser=ur.findByUserName(UserName);
//            tempMissive.setMissiveCreateUser(tempUser);//missive增加creatUser
            tempMissive.setName(name);
//            tempMissive.setCreatedBy(tempUser);
//            tempMissive.setLastModifiedBy(tempUser);
            mr.save(tempMissive);//-------------------save

            tempMissiveVersion=new MissiveVersion();
            tempMissiveVersion.setDocFilePath(docFilePath);//公文doc路径
            tempMissiveVersion.setPdfFilePath(pdfFilePath);//公文pdf
            tempMissiveVersion.setMissiveTittle(Tittle);//公文标题
            tempMissiveVersion.setVersionNumber(1);//第一创建默认为1 公文版本号
            tempMissiveVersion.setMissive(tempMissive);
            mvr.save(tempMissiveVersion);//存入数据库-----------


            //点击提交按钮后，首先判断新版本下文件夹是否存在，如果存在则将就版本的文件拷贝到新版本文件夹 否则将整个文件夹拷贝至新版本
            boolean folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
//            MissivePublish missivePublish=mpr.findByProcessID(instanceID);//获取最大版本号
            if(tempMissivePublish!=null){
                if(tempMissivePublish.getMissiveInfo()!=null){
                    if(tempMissivePublish.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = tempMissivePublish.getMissiveInfo().getVersions();
                        if(tempMissivePublish.getMissiveInfo().getVersions().size()!=0){
                            List<Long> mvnum=new ArrayList<Long>();
                            for(MissiveVersion mv:missiveVersions){
                                mvnum.add(mv.getVersionNumber());
                            }
                            fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                        }
                    }
                }
            }
            oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
            fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
            newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新版本路径

            if (fileVersionNum!=1){
                folderExit= FileOperate.exitFolder(newFolder);//判断新版本路径是否存在
                if (!folderExit) {
                    //如果存在，不对文件夹进行操作
                    //不存在，将就版本整个文件夹拷贝至新版本路径下
                    FileOperate.newFolder(newFolder);
                    FileOperate.copyFolder(oldFolder, newFolder);
                }
            }
            else {
//               do nothing
                if (!folderExit) {
                    //如果存在，不对文件夹进行操作
                    //不存在，将就版本整个文件夹拷贝至新版本路径下
                    FileOperate.newFolder(newFolder);
//                        FileOperate.copyFolder(oldFolder,newFolder);
                }
            }

            //<-------------附件

            List<Attachment> tempListAttachment=new ArrayList<Attachment>();//附件List
//            String[] tempappendPaths=appendPath.split("\\|");
            String[] tempappendNames=appendName.split("\\|");
            if(tempappendNames.length!=0){
                for(int i=0;i<tempappendNames.length;i++){
                    if(!(tempappendNames[i].equals(""))){
                        Attachment tempAttachment=new Attachment();
                        tempAttachment.setAttachmentTittle(tempappendNames[i]);
                        tempAttachment.setAttachmentFilePath(newFolder+tempappendNames[i]);
                        tempAttachment.setMissiveVersion(tempMissiveVersion);
                        attachr.save(tempAttachment);//存入数据库-------------
                        tempListAttachment.add(tempAttachment);
//                        attachmentFilePathList[i]=tempAttachment.getAttachmentFilePath();//生成附件列表的路径为数组-----给邓豪接口用
                        attachmentFilePathList.add(tempAttachment.getAttachmentFilePath());//生成附件列表的路径为数组-----给邓豪接口用
                    }
                }
            }
        }

        MissiveType tempMissiveType=mtr.findOne(typeName);// 公文类型、公文密级、公文号 设置---------
        tempMissive.setMissiveType(tempMissiveType);//missive增加MissiveType

        SecretLevel tempSecretLevel=new SecretLevel();
        if(secretLevel!=null){
            tempSecretLevel=slr.findOne(secretLevel);
        }
        else tempSecretLevel=null;
        tempMissive.setSecretLevel(tempSecretLevel);//missive增加SecretLevel
        if(!(missiveNum.equals("")))
        tempMissive.setMissiveNum(missiveNum);//missive增加MissiveNum
        mr.save(tempMissive);//-------------------save

        //  填写发文表单----
        tempMissivePublish.setProcessID(instanceID);//实例名
        tempMissivePublish.setTaskID(taskID);//任务名


        User tempSignIssueUser;//<----------签发用户
        tempSignIssueUser=ur.findByUserName(signIssueUserName);
        tempMissivePublish.setSignIssueUser(tempSignIssueUser);

//        //签发内容
        tempSignIssueCommentContent.setBase30url(signIssueBase30url);
        tempSignIssueCommentContent.setImageurl(signIssueImageurl);
        tempSignIssueCommentContent.setJsignatureBase30url(signIssueJSBase30url);
        tempSignIssueCommentContent.setJsignatureImageurl(signIssueJSImageurl);
        tempSignIssueCommentContent.setContentText(signIssue_Content);
        List<User> tempSignIssueUsersList=new ArrayList<User>();
        tempSignIssueUsersList.add(tempSignIssueUser);
        tempSignIssueCommentContent.setContentUsers(tempSignIssueUsersList);
        tempSignIssueCommentContent.setEnabled(true);
        ccr.save(tempSignIssueCommentContent);//---------save  SignIssueCommentContent
        tempMissivePublish.setSignIssueContent(tempSignIssueCommentContent);//missivePublish add 签发内容

        List<User> tempCounterSignUsersList=new ArrayList<User>();//会签用户s
        String[] tempCounterSignUsers=counterSignUsersName.split("\\|");
        for(int i=0;i<tempCounterSignUsers.length;i++){
            User tempCounterSignUser=ur.findByUserName(tempCounterSignUsers[i]);
            tempCounterSignUsersList.add(tempCounterSignUser);
        }
        tempMissivePublish.setCounterSignUsers(tempCounterSignUsersList);

//         //会签内容
        tempCounterSignCommentContent.setBase30url(counterSignBase30url);
        tempCounterSignCommentContent.setImageurl(counterSignImageurl);
        tempCounterSignCommentContent.setJsignatureBase30url(counterSignJSBase30url);
        tempCounterSignCommentContent.setJsignatureImageurl(counterSignJSImageurl);
        tempCounterSignCommentContent.setContentText(counterSign_Content);
        tempCounterSignCommentContent.setContentUsers(tempCounterSignUsersList);
        tempCounterSignCommentContent.setEnabled(true);
        ccr.save(tempCounterSignCommentContent);//-----------------save CounterSignCommentContent
        tempMissivePublish.setCounterSignContent(tempCounterSignCommentContent);//missivePublish add 会签内容

        List<User> missiveCopytoUsers=new ArrayList<User>();//公文可见人员
        try{
            missiveCopytoUsers=tempMissivePublish.getMissiveInfo().getCopyToUsers();
            missiveCopytoUsers.clear();
        }
        catch (Exception e){
            logger.error("setMissivePublish exception:",e);

        }

        List<Group> tempMainSendGroupsList=new ArrayList<Group>();//----主送部门


        String[] tempMainSendGroups=mainSendGroups.split("\\|");
        for(int i=0;i<tempMainSendGroups.length;i++){
            if(!(tempMainSendGroups[i].equals(""))){
                try{
                    Group tempMainSendGroup=gr.findOne(Long.parseLong(tempMainSendGroups[i]));
                    if(tempMainSendGroup.getUsers()!=null){
                        for(User cptuser:tempMainSendGroup.getUsers()){
                            missiveCopytoUsers.add(cptuser);
                        }
                    }
                    tempMainSendGroupsList.add(tempMainSendGroup);
                }
                catch (Exception e){
                    logger.error("setMissivePublishMainSendGroups exception:",e);
                }

            }

        }
        tempMissivePublish.setMainSendGroups(tempMainSendGroupsList);//missivePublish add 主送部门

        List<Group> tempCopytoGroupsList=new ArrayList<Group>();//----抄送部门
        String[] tempCopytoGroups=copytoGroups.split("\\|");
        for(int i=0;i<tempCopytoGroups.length;i++){
            if(!(tempCopytoGroups[i].equals(""))){
                try {
                    Group tempCopytoGroup=gr.findOne(Long.parseLong(tempCopytoGroups[i]));
                    if(tempCopytoGroup.getUsers()!=null){
                        for(User cptuser:tempCopytoGroup.getUsers()){
                            missiveCopytoUsers.add(cptuser);
                        }
                    }
                    tempCopytoGroupsList.add(tempCopytoGroup);
                }
                catch (Exception e){
                    logger.error("setMissivePublishCopytoGroupsList exception:",e);


                }
            }

        }
        tempMissivePublish.setCopytoGroups(tempCopytoGroupsList);//missivePublish add 抄送部门
        tempMissive.setCopyToUsers(missiveCopytoUsers);//missive 可见人员
        tempMissivePublish.setMissiveInfo(tempMissive);//公文信息

        //User tempDrafterUser;//拟稿人
        User tempDrafterUser=ur.findByUserName(drafter);
        if(tempDrafterUser!=null)tempMissivePublish.setDrafterUser(tempDrafterUser);

        //User tempComposeUser;
        User tempComposeUser=ur.findByUserName(composeUser);
        if (tempComposeUser!=null) tempMissivePublish.setComposeUser(tempComposeUser);//排版人

        User tempDep_LeaderCheckUser=new User();//处(室)领导核稿ren
        tempDep_LeaderCheckUser=ur.findByUserName(dep_LeaderCheck);
        tempMissivePublish.setDep_LeaderCheckUser(tempDep_LeaderCheckUser);
        tempdep_LeaderCheckCommentContent.setBase30url(dep_LeaderCheck_Base30);
        tempdep_LeaderCheckCommentContent.setImageurl(dep_LeaderCheck_img);
        tempdep_LeaderCheckCommentContent.setJsignatureBase30url(dep_LeaderCheckJS_Base30);
        tempdep_LeaderCheckCommentContent.setJsignatureImageurl(dep_LeaderCheckJS_img);
        tempdep_LeaderCheckCommentContent.setContentText(dep_LeaderCheck_Content);

        List<User> temptempDep_LeaderCheckUserList=new ArrayList<User>();
        temptempDep_LeaderCheckUserList.add(tempDep_LeaderCheckUser);
        tempdep_LeaderCheckCommentContent.setContentUsers(temptempDep_LeaderCheckUserList);
        tempdep_LeaderCheckCommentContent.setEnabled(true);
        ccr.save(tempdep_LeaderCheckCommentContent);//-----------------save CounterSignCommentContent
        tempMissivePublish.setDep_LeaderCheckContent(tempdep_LeaderCheckCommentContent);//missivePublish add 签发内容



        User tempOfficeCheckUser=new User();//办公室复核
        tempOfficeCheckUser=ur.findByUserName(officeCheck);
        tempMissivePublish.setOfficeCheckUser(tempOfficeCheckUser);

        tempMissivePublish.setPrintCount(printCount);//打印份数

        User tempCheckReader=new User();//校对人
        tempCheckReader=ur.findByUserName(CheckReader);
        tempMissivePublish.setCheckReader(tempCheckReader);

        if(!(Printer.equals(""))){
            User tempPrinter=new User();//打印
            tempPrinter=ur.findByUserName(Printer);
            tempMissivePublish.setPrinter(tempPrinter);
        }

        tempMissivePublish.setGov_info_attr(Gov_info_attr);
        tempMissivePublish.setMissiveTittle(Tittle);
        tempMissivePublish.setAttachmentTittle(appendTittle);
        tempMissivePublish.setDep_LeaderCheckUserSuggestion(dep_LeaderCheckSuggestion);
        tempMissivePublish.setOfficeCheckUserSuggestion(officeCheckSuggestion);

        //郑小罗 2014/12/25 添加主送、抄送外单位信息
        tempMissivePublish.setMainSend(mainSend);
        tempMissivePublish.setCopyTo(copyTo);
        mpDAO.save(tempMissivePublish);

//        tempMainSendGroups

        Map<String,Object> activitiMap = new HashMap<String, Object>();
//        List<String> userlist = new ArrayList<String>();
//        userlist.add("kermit");
//        userlist.add("sqhe18");
//        activitiMap.put("",userlist);


        if(!(activitiVariables.equals(""))){
            String[] tempActivitiVariables = activitiVariables.split("\\|");
            if(tempActivitiVariables.length!=0){
                for(int i=0;i<tempActivitiVariables.length;i++){
                    String[] tempActivitiVariable=tempActivitiVariables[i].split(",");
                    if(tempActivitiVariable[3].equals("false")){
                        activitiMap.put(tempActivitiVariable[0],tempActivitiVariable[1]);
                    }
                    else{
                        String[] valueList = tempActivitiVariable[i].split(";");
                        List<String> userlist = new ArrayList<String>();
                        for(int j=0;j<valueList.length;j++){
                            userlist.add(valueList[j]);

                        }
                        activitiMap.put(tempActivitiVariable[0],userlist);
                    }
                }
            }
        }

        String currentUserName = actService.getCurrentTask(Integer.parseInt(taskID.toString())).getAssignee();
        String currentName= mrc.getName(currentUserName);
        String htmlpath="http://localhost:"+hostport+"/missive/missivePublishToPDF/";
        htmlpath+=instanceID;
        String currentTaskName=taskDAO.getCurrentTaskName(Integer.parseInt(taskID.toString()));
        String[] attachment_StringArray =new String[attachmentFilePathList.size()];
        for(int attSTR_i=0;attSTR_i<attachmentFilePathList.size();attSTR_i++){
            attachment_StringArray[attSTR_i]=attachmentFilePathList.get(attSTR_i);
        }
//        if(currentTaskName.equals("文印室套红排版打印")){
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "文印室套红排版打印",String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//            cmmF.convertAtt2Pdf2(currentTaskName, "文印室套红排版打印", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//        else {
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//           cmmF.convertAtt2Pdf2(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//todo fix this

        this.actService.completeTask(Long.parseLong(taskID.toString()),activitiMap,"missivePublish",instanceID);//只是完成任务 没有结束，所以不需要将内容写入全文检索
        Task nextTask = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(instanceID)));
        if(nextTask!=null){
            String nextTaskID=nextTask.getId();

            String nextUser=nextTask.getAssignee();

            String missive_tittle=tempMissivePublish.getMissiveTittle();

            String missiveType="MissivePublish";

            //if(nextUsers!=null&&nextUsers.size()!=0){

               // for(String user:nextUsers){
                    if(nextUser!=null){
                        User  user1=ur.findByUserName(nextUser);

                       Future<String> result =  this.convertService.convertPDFAndSendMailSMS(currentTaskName,tempMissiveVersion,fileVersionNum,attachment_StringArray,
                                instanceID,taskID,htmlpath,missiveType,user1.getEmailSend(),user1.getEmail(),missive_tittle,
                                nextTaskID,user1,currentName,this.actService);
                        /*try {
                            result.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }*/
//                        if(user1.getTel()!=null) {
//                            try {
//                                boolean isSend = user1.getEmailSend();
//                                if(isSend) {
//                                    String emailAddr = user1.getEmail();
//                                    EmailSenderThread emailSnd = new EmailSenderThread("missivePublish", isSend,emailAddr, missive_tittle, nextTaskID, String.valueOf(instanceID),actService);
//                                    emailSnd.run();
//                                    //actService.emailSnd("missivePublish", user1, missive_tittle, nextTaskID, String.valueOf(instanceID));//发送邮件
//                                }
//                                actService.msgSender(user1, missive_tittle, currentName);//发送短信提醒
//                            }
//                            catch (Exception ex){
//                                logger.info("email or message exception:");
//                                logger.error(ex.toString());
//                            }
//                            finally {
//                                return "success";
//                            }
//                        }
                    }
                //}
            //}
        }
        else if(this.actService.isProcessFinished(String.valueOf(instanceID))){
            String missiveType="MissivePublish";


            Future<String> result =  this.convertService.convertPDF(currentTaskName,tempMissiveVersion,fileVersionNum,attachment_StringArray,
                    instanceID,taskID,htmlpath,missiveType);







        }






        logger.info("end missive publish");
        return "success";

    }

    @RequestMapping(value="/missivePublishToPDF/{intanceid}", method= RequestMethod.GET)
    public String toPDF(@PathVariable int intanceid,Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("instanceID",intanceid);
        MissivePublish missivePublish=mpDAO.findByProcessID(intanceid);//------------->missivePublish add
        MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);
        int maxMV=0;
        if(missivePublishForm.missiveInfo!=null&&missivePublishForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missivePublishForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missivePublishForm.missiveInfo.versions=mvform;
        }


        String signIssueContent_text="";
        if(missivePublishForm.signIssueContent!=null&&missivePublishForm.signIssueContent.contentText!=null){
            //signIssueContent_text=missivePublishForm.signIssueContent.contentText.replaceAll("<br>","\n");
            signIssueContent_text=missivePublishForm.signIssueContent.contentText;
//            try{
//
//                if(signIssueContent_text!=null&&!signIssueContent_text.equals("")&&signIssueContent_text.substring(0,1).equals("\n"))
//                    signIssueContent_text=" "+signIssueContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-signIssue exception:",e);
//
//
//            }

        }
        missivePublishForm.signIssueContent.contentText=signIssueContent_text;

        String counterSignContent_text="";
        if(missivePublishForm.counterSignContent!=null&&missivePublishForm.counterSignContent.contentText!=null){
            //counterSignContent_text=missivePublishForm.counterSignContent.contentText.replaceAll("<br>","\n");
            counterSignContent_text=missivePublishForm.counterSignContent.contentText;
//            try{
//                if(counterSignContent_text!=null&&!counterSignContent_text.equals("")&&counterSignContent_text.substring(0,1).equals("\n"))
//                    counterSignContent_text=" "+counterSignContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-counterSign exception:",e);
//
//            }
        }
        missivePublishForm.counterSignContent.contentText=counterSignContent_text;
        String dep_LeaderCheckContent_text="";
        if(missivePublishForm.Dep_LeaderCheckContent!=null&&missivePublishForm.Dep_LeaderCheckContent.contentText!=null){
            //dep_LeaderCheckContent_text=missivePublishForm.Dep_LeaderCheckContent.contentText.replaceAll("<br>","\n");
            dep_LeaderCheckContent_text=missivePublishForm.Dep_LeaderCheckContent.contentText;
//            try{
//                if(counterSignContent_text!=null&&!counterSignContent_text.equals("")&&counterSignContent_text.substring(0,1).equals("\n"))
//                    counterSignContent_text=" "+counterSignContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-counterSign exception:",e);
//
//            }
        }
        missivePublishForm.Dep_LeaderCheckContent.contentText=dep_LeaderCheckContent_text;
        String missiveTittle_text=null;
        if(missivePublishForm.missiveTittle!=null){
            //missiveTittle_text=missivePublishForm.missiveTittle.replaceAll("<br>","\n");
            missiveTittle_text=missivePublishForm.missiveTittle;
        }
        missivePublishForm.missiveTittle=missiveTittle_text;
        String attachmentTittle_text=null;
        if(missivePublishForm.attachmentTittle!=null){
            //attachmentTittle_text=missivePublishForm.attachmentTittle.replaceAll("<br>","\n");
            attachmentTittle_text=missivePublishForm.attachmentTittle;
        }
        missivePublishForm.attachmentTittle=attachmentTittle_text;

        model.addAttribute("missivePublishForm", missivePublishForm);
//missive number = number1 + ..2 + ..3

        String missiveNumber="";
        String missiveNumber1="";
        String missiveNumber2="";
        String missiveNumber3="";
        if(missivePublishForm!=null&&missivePublishForm.getMissiveInfo()!=null&&missivePublishForm.getMissiveInfo().getMissiveNum()!=null){
            missiveNumber=missivePublishForm.getMissiveInfo().getMissiveNum();
            String[] tempMissiveNumberArray=missiveNumber.split("\\|");
            if(tempMissiveNumberArray.length>=3){
                missiveNumber1=tempMissiveNumberArray[0];
                missiveNumber2=tempMissiveNumberArray[1];
                missiveNumber3=tempMissiveNumberArray[2];
            }
        }
        model.addAttribute("missiveNumber1", missiveNumber1);
        model.addAttribute("missiveNumber2", missiveNumber2);
        model.addAttribute("missiveNumber3", missiveNumber3);


        String outer_mainsend=null;
        if(missivePublishForm.mainSend !=null){//主送人员
            outer_mainsend=missivePublishForm.mainSend;
        }
        String outer_copyto=null;
        if(missivePublishForm.copyTo !=null){//主送人员
            outer_copyto=missivePublishForm.copyTo;
        }
        String mainsendGroupString=null;

        if(missivePublishForm.MainSendGroups !=null){//主送人员
            if(missivePublishForm.MainSendGroups.size()!=0){
                for(int i=0;i<missivePublishForm.MainSendGroups.size();i++){

                    if(i==0) mainsendGroupString = missivePublishForm.MainSendGroups.get(i).groupName;
                    else{
                        mainsendGroupString = mainsendGroupString+", "+missivePublishForm.MainSendGroups.get(i).groupName;
                    }
                }
                if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                    mainsendGroupString=mainsendGroupString+", "+outer_mainsend;
                }
            }
            else{
                if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                    mainsendGroupString = outer_mainsend;
                }
            }

        }
        else{
            if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                mainsendGroupString = outer_mainsend;
            }
        }//主送内容end
        model.addAttribute("mainsendGroup", mainsendGroupString);

        String copytoGroupString=null;
        if(missivePublishForm.CopytoGroups!=null){//chao送人员
            if(missivePublishForm.CopytoGroups.size()!=0){
                for(int i=0;i<missivePublishForm.CopytoGroups.size();i++){
                    if(i==0) copytoGroupString= missivePublishForm.CopytoGroups.get(i).groupName;
                    else{
                        copytoGroupString = copytoGroupString+", "+missivePublishForm.CopytoGroups.get(i).groupName;
                    }
                }
                if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                    copytoGroupString=copytoGroupString+", "+outer_copyto;
                }
            }
            else{
                if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                    copytoGroupString=outer_copyto;
                }
            }


        }
        else{
            if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                copytoGroupString=outer_copyto;
            }
        }//chao送内容end
        model.addAttribute("copytoGroup", copytoGroupString);

        String blankImageurl="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAxEAAAFjCAYAAABCEoaFAAAWB0lEQVR4Xu3XoREAAAgDMbr/0szwPuiqHOZ3jgABAgQIECBAgAABAkFgYWtKgAABAgQIECBAgACBExGegAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSTwRdcBZA2nfHIAAAAASUVORK5CYII=";
        model.addAttribute("blankImage", blankImageurl);
        model.addAttribute("hasBgPng",(missivePublish.getBackgroudImage()!=null && !missivePublish.getBackgroudImage().equals("")));
        return "missivePublishForPDF";
    }

    @RequestMapping(value="/missivePublishToPDF/{intanceid}/1", method= RequestMethod.GET)
    public String PublishHtmltoPDF(@PathVariable int intanceid,Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("instanceID",intanceid);
        MissivePublish missivePublish=mpDAO.findByProcessID(intanceid);//------------->missivePublish add
        MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);
        int maxMV=0;
        if(missivePublishForm.missiveInfo!=null&&missivePublishForm.missiveInfo.versions!=null){
            List<MissiveVersionFrom> mvfs=missivePublishForm.missiveInfo.versions;
            for(int i=0;i<mvfs.size()-1;i++){
                if(mvfs.get(i).versionNumber>mvfs.get(i+1).versionNumber){
                    maxMV=i;
                }
                else maxMV=i+1;
            }
            List<MissiveVersionFrom> mvform=new ArrayList<MissiveVersionFrom>();
            mvform.add(mvfs.get(maxMV));
            missivePublishForm.missiveInfo.versions=mvform;
        }


        String signIssueContent_text="";
        if(missivePublishForm.signIssueContent!=null&&missivePublishForm.signIssueContent.contentText!=null){
            //signIssueContent_text=missivePublishForm.signIssueContent.contentText.replaceAll("<br>","\n");
            signIssueContent_text=missivePublishForm.signIssueContent.contentText;
//            try{
//
//                if(signIssueContent_text!=null&&!signIssueContent_text.equals("")&&signIssueContent_text.substring(0,1).equals("\n"))
//                    signIssueContent_text=" "+signIssueContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-signIssue exception:",e);
//
//
//            }

        }
        missivePublishForm.signIssueContent.contentText=signIssueContent_text;

        String counterSignContent_text="";
        if(missivePublishForm.counterSignContent!=null&&missivePublishForm.counterSignContent.contentText!=null){
            //counterSignContent_text=missivePublishForm.counterSignContent.contentText.replaceAll("<br>","\n");
            counterSignContent_text=missivePublishForm.counterSignContent.contentText;
//            try{
//                if(counterSignContent_text!=null&&!counterSignContent_text.equals("")&&counterSignContent_text.substring(0,1).equals("\n"))
//                    counterSignContent_text=" "+counterSignContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-counterSign exception:",e);
//
//            }
        }
        missivePublishForm.counterSignContent.contentText=counterSignContent_text;
        String dep_LeaderCheckContent_text="";
        if(missivePublishForm.Dep_LeaderCheckContent!=null&&missivePublishForm.Dep_LeaderCheckContent.contentText!=null){
            dep_LeaderCheckContent_text=missivePublishForm.Dep_LeaderCheckContent.contentText;
//            try{
//                if(counterSignContent_text!=null&&!counterSignContent_text.equals("")&&counterSignContent_text.substring(0,1).equals("\n"))
//                    counterSignContent_text=" "+counterSignContent_text;
//            }
//            catch (Exception e){
//                logger.error("toPDF-counterSign exception:",e);
//
//            }
        }
        missivePublishForm.Dep_LeaderCheckContent.contentText=dep_LeaderCheckContent_text;
        String missiveTittle_text=null;
        if(missivePublishForm.missiveTittle!=null){
            missiveTittle_text=missivePublishForm.missiveTittle.replaceAll("<br>","\n");
        }
        missivePublishForm.missiveTittle=missiveTittle_text;
        String attachmentTittle_text=null;
        if(missivePublishForm.attachmentTittle!=null){
            //attachmentTittle_text=missivePublishForm.attachmentTittle.replaceAll("<br>","\n");
            attachmentTittle_text=missivePublishForm.attachmentTittle;
        }
        missivePublishForm.attachmentTittle=attachmentTittle_text;

        model.addAttribute("missivePublishForm", missivePublishForm);
//missive number = number1 + ..2 + ..3

        String missiveNumber="";
        String missiveNumber1="";
        String missiveNumber2="";
        String missiveNumber3="";
        if(missivePublishForm!=null&&missivePublishForm.getMissiveInfo()!=null&&missivePublishForm.getMissiveInfo().getMissiveNum()!=null){
            missiveNumber=missivePublishForm.getMissiveInfo().getMissiveNum();
            String[] tempMissiveNumberArray=missiveNumber.split("\\|");
            if(tempMissiveNumberArray.length>=3){
                missiveNumber1=tempMissiveNumberArray[0];
                missiveNumber2=tempMissiveNumberArray[1];
                missiveNumber3=tempMissiveNumberArray[2];
            }
        }
        model.addAttribute("missiveNumber1", missiveNumber1);
        model.addAttribute("missiveNumber2", missiveNumber2);
        model.addAttribute("missiveNumber3", missiveNumber3);


        String outer_mainsend=null;
        if(missivePublishForm.mainSend !=null){//主送人员
            outer_mainsend=missivePublishForm.mainSend;
        }
        String outer_copyto=null;
        if(missivePublishForm.copyTo !=null){//主送人员
            outer_copyto=missivePublishForm.copyTo;
        }
        String mainsendGroupString=null;

        if(missivePublishForm.MainSendGroups !=null){//主送人员
            if(missivePublishForm.MainSendGroups.size()!=0){
                for(int i=0;i<missivePublishForm.MainSendGroups.size();i++){

                    if(i==0) mainsendGroupString = missivePublishForm.MainSendGroups.get(i).groupName;
                    else{
                        mainsendGroupString = mainsendGroupString+", "+missivePublishForm.MainSendGroups.get(i).groupName;
                    }
                }
                if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                    mainsendGroupString=mainsendGroupString+", "+outer_mainsend;
                }
            }
            else{
                if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                    mainsendGroupString = outer_mainsend;
                }
            }

        }
        else{
            if(outer_mainsend!=null&&!(outer_mainsend.equals(""))){
                mainsendGroupString = outer_mainsend;
            }
        }//主送内容end
        model.addAttribute("mainsendGroup", mainsendGroupString);

        String copytoGroupString=null;
        if(missivePublishForm.CopytoGroups!=null){//chao送人员
            if(missivePublishForm.CopytoGroups.size()!=0){
                for(int i=0;i<missivePublishForm.CopytoGroups.size();i++){
                    if(i==0) copytoGroupString= missivePublishForm.CopytoGroups.get(i).groupName;
                    else{
                        copytoGroupString = copytoGroupString+", "+missivePublishForm.CopytoGroups.get(i).groupName;
                    }
                }
                if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                    copytoGroupString=copytoGroupString+", "+outer_copyto;
                }
            }
            else{
                if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                    copytoGroupString=outer_copyto;
                }
            }


        }
        else{
            if(outer_copyto!=null&&!(outer_copyto.equals(""))){
                copytoGroupString=outer_copyto;
            }
        }//chao送内容end
        model.addAttribute("copytoGroup", copytoGroupString);

        String blankImageurl="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAxEAAAFjCAYAAABCEoaFAAAWB0lEQVR4Xu3XoREAAAgDMbr/0szwPuiqHOZ3jgABAgQIECBAgAABAkFgYWtKgAABAgQIECBAgACBExGegAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSQgIhKXMQECBAgQIECAAAECIsIPECBAgAABAgQIECCQBERE4jImQIAAAQIECBAgQEBE+AECBAgQIECAAAECBJKAiEhcxgQIECBAgAABAgQIiAg/QIAAAQIECBAgQIBAEhARicuYAAECBAgQIECAAAER4QcIECBAgAABAgQIEEgCIiJxGRMgQIAAAQIECBAgICL8AAECBAgQIECAAAECSUBEJC5jAgQIECBAgAABAgREhB8gQIAAAQIECBAgQCAJiIjEZUyAAAECBAgQIECAgIjwAwQIECBAgAABAgQIJAERkbiMCRAgQIAAAQIECBAQEX6AAAECBAgQIECAAIEkICISlzEBAgQIECBAgAABAiLCDxAgQIAAAQIECBAgkAREROIyJkCAAAECBAgQIEBARPgBAgQIECBAgAABAgSSgIhIXMYECBAgQIAAAQIECIgIP0CAAAECBAgQIECAQBIQEYnLmAABAgQIECBAgAABEeEHCBAgQIAAAQIECBBIAiIicRkTIECAAAECBAgQICAi/AABAgQIECBAgAABAklARCQuYwIECBAgQIAAAQIERIQfIECAAAECBAgQIEAgCYiIxGVMgAABAgQIECBAgICI8AMECBAgQIAAAQIECCQBEZG4jAkQIECAAAECBAgQEBF+gAABAgQIECBAgACBJCAiEpcxAQIECBAgQIAAAQIiwg8QIECAAAECBAgQIJAERETiMiZAgAABAgQIECBAQET4AQIECBAgQIAAAQIEkoCISFzGBAgQIECAAAECBAiICD9AgAABAgQIECBAgEASEBGJy5gAAQIECBAgQIAAARHhBwgQIECAAAECBAgQSAIiInEZEyBAgAABAgQIECAgIvwAAQIECBAgQIAAAQJJQEQkLmMCBAgQIECAAAECBESEHyBAgAABAgQIECBAIAmIiMRlTIAAAQIECBAgQICAiPADBAgQIECAAAECBAgkARGRuIwJECBAgAABAgQIEBARfoAAAQIECBAgQIAAgSTwRdcBZA2nfHIAAAAASUVORK5CYII=";
        model.addAttribute("blankImage", blankImageurl);
        model.addAttribute("hasBgPng",(missivePublish.getBackgroudImage()!=null && !missivePublish.getBackgroudImage().equals("")));

        String taskid="";
        Task task2 = actService.getCurrentTasksByProcessInstanceId(Long.parseLong(String.valueOf(intanceid)));
        if(task2!=null) {
            taskid = task2.getId();
        }else{
//            this.historyService.deleteHistoricProcessInstance(String.valueOf(intanceid));
            List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(String.valueOf(intanceid)).orderByHistoricTaskInstanceEndTime().desc().list();

            //return previousTasklist.get(0).getId();
            if(!previousTasklist.isEmpty()) {
                taskid= previousTasklist.get(0).getId();
            }
        }
        //  Long TASKID =Long.parseLong(taskid);
//        Long lastTaskId=Long.parseLong(actService.getPreviousTaskIDByCurrentTaskID(taskid));//上一个任务编号 03-04
//        model.addAttribute("lastTaskId",lastTaskId);

        int versionNum = fileupc.getMaxMissiveVersion(Long.parseLong(String.valueOf(intanceid)),"missivePublish");
        model.addAttribute("newVersionId",versionNum);


        model.addAttribute("missivePublishForm", missivePublishForm);
        model.addAttribute("currentEditableField",taskDAO.getCurrentEditableFieldsByTaskId(Integer.valueOf(taskid),1));







        return "missivePublishForPDF2";
    }

//    @Async
//    public void convertPDFAndSendMailSMS(String currentTaskName,MissiveVersion tempMissiveVersion,long fileVersionNum,String[] attachment_StringArray,long instanceID,Long taskID,String htmlpath,
//                                         boolean isSend,String emailAddr,String missive_tittle,String nextTaskID,User user1,String currentName,ActivitiService actservice)
//    {
//        logger.info("start convertPDFAndSendMailSMS");
//
//        if(currentTaskName.equals("文印室套红排版打印")){
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "文印室套红排版打印",String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//            cmmF.convertAtt2Pdf2(currentTaskName, "文印室套红排版打印", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//        else {
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//            cmmF.convertAtt2Pdf2(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//
//
//        EmailSenderThread emailSnd = new EmailSenderThread("missivePublish", isSend,emailAddr, missive_tittle, nextTaskID, String.valueOf(instanceID),actService);
//        emailSnd.run();
//        try {
//            actService.msgSender(user1, missive_tittle, currentName);//发送短信提醒
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error(e.getMessage());
//        }
//        logger.info("end convertPDFAndSendMailSMS");
//    }

}