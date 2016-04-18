package cn.edu.shou.missive.web;


import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.*;
import cn.edu.shou.missive.service.MissiveRecSeeCardRepository;
import cn.edu.shou.missive.service.MissiveReceiveTaskDealerRepository;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
@RequestMapping(value="")
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class MissiveReceiveFunction {
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissiveReceiveTaskDealerRepository mrtdr;

    private final static Logger logger = LoggerFactory.getLogger(MissiveReceiveFunction.class);


    cn.edu.shou.missive.domain.CommonFunction cf = new cn.edu.shou.missive.domain.CommonFunction();



    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public UserFrom userToUserForm(User user){

        UserFrom userFrom=new UserFrom();

        if(user.getId()!=0){
            userFrom.id=user.getId();
        }
        if(user.getUserName()!=null){
            userFrom.userName=user.getUserName();
        }
        if(user.getName()!=null){
            userFrom.Name=user.getName();
        }
        if(user.getSex()!=null){
            userFrom.sex=user.getSex();
        }
        if(user.getTel()!=null){
            userFrom.tel=user.getTel();
        }
        if(user.getEmail()!=null){
            userFrom.sex=user.getSex();
        }
        if(user.getEmail()!=null){
            userFrom.email=user.getEmail();
        }
        if(user.getPassword()!=null){
            userFrom.password=user.getPassword();
        }
        if(user.getImagePath()!=null){
            userFrom.imagePath=user.getImagePath();
        }
        if(user.getLastLoginTime()!=null){
            try{
                userFrom.lastLoginTime=dateFormat.format(user.getLastLoginTime());
            }
            catch (Exception e){
                logger.error("userToUserForm exception:",e);


            }

        }
        if(user.isEnabled()!=false){
            userFrom.enabled=user.isEnabled();
        }
        if(user.getDescription()!=null){
            userFrom.description=user.getDescription();
        }
        if(user.getGroup()!=null){//user.group
            GroupFrom groupFrom=new GroupFrom();
            if(user.getGroup().getId()!=0){
                groupFrom.id=user.getGroup().getId();
            }
            if(user.getGroup().getGroupName()!=null){
                groupFrom.groupName=user.getGroup().getGroupName();
            }
            if(user.getGroup().getDescription()!=null){
                groupFrom.description=user.getGroup().getDescription();
            }
            if(user.getGroup().getCreatedDate()!=null){
                try{
                    groupFrom.createdDate=dateFormat.format(user.getGroup().getCreatedDate().toDate());
                }
                catch (Exception e){
                    logger.error("userToUserForm-createdDate exception:",e);

                }

            }
            if(user.getGroup().getLastModifiedDate()!=null){
                try{
                    groupFrom.lastModifiedDate=dateFormat.format(user.getGroup().getLastModifiedDate().toDate());
                }
                catch (Exception e){
                    logger.error("userToUserForm-lastModifiedDate exception:",e);

                }
            }

                groupFrom.isDel=user.getGroup().getIsDel();

            groupFrom.groupList=null;
            groupFrom.group=null;
            groupFrom.users=null;

            userFrom.group=groupFrom;
        }

        return userFrom;
    }
    public GroupFrom groupToGroupFrom(Group group){
        GroupFrom groupFrom=new GroupFrom();
        if(group.getId()!=0){
            groupFrom.id=group.getId();
        }
        if(group.getGroupName()!=null){
            groupFrom.groupName=group.getGroupName();
        }
        if(group.getDescription()!=null){
            groupFrom.description=group.getDescription();
        }
        if(group.getGroupName()!=null){
            groupFrom.groupName=group.getGroupName();
        }
        if(group.getUsers()!=null){
            List<UserFrom> userFroms=new ArrayList<UserFrom>();
            for(User user:group.getUsers()){
                UserFrom userFrom=new UserFrom();
                if(user.getId()!=0){
                    userFrom.id=user.getId();
                }
                if(user.getUserName()!=null){
                    userFrom.userName=user.getUserName();
                }
                if(user.getName()!=null){
                    userFrom.Name=user.getName();
                }
                if(user.getSex()!=null){
                    userFrom.sex=user.getSex();
                }
                if(user.getTel()!=null){
                    userFrom.tel=user.getTel();
                }
                if(user.getEmail()!=null){
                    userFrom.sex=user.getSex();
                }
                if(user.getEmail()!=null){
                    userFrom.email=user.getEmail();
                }
                if(user.getPassword()!=null){
                    userFrom.password=user.getPassword();
                }
                if(user.getImagePath()!=null){
                    userFrom.imagePath=user.getImagePath();
                }
                if(user.getLastLoginTime()!=null){
                    try {
                        userFrom.lastLoginTime=dateFormat.format(user.getLastLoginTime());
                    }
                    catch (Exception e){
                        logger.error("groupToGroupFrom-lastLoginTime exception:",e);

                    }
                }
                if(user.isEnabled()!=false){
                    userFrom.enabled=user.isEnabled();
                }
                if(user.getDescription()!=null){
                    userFrom.description=user.getDescription();
                }
                userFrom.group=null;
                userFroms.add(userFrom);
            }
            groupFrom.users=userFroms;
        }
        if(group.getCreatedDate()!=null){
            try {
                groupFrom.createdDate=dateFormat.format(group.getCreatedDate().toDate());
            }
            catch (Exception e){
                logger.error("groupToGroupFrom-createdDate exception:",e);

            }
        }
        if(group.getLastModifiedDate()!=null){
            try{
                groupFrom.lastModifiedDate=dateFormat.format(group.getLastModifiedDate().toDate());
            }
            catch (Exception e){
                logger.error("groupToGroupFrom-lastModifiedDate exception:",e);

            }
        }

            groupFrom.isDel=group.getIsDel();

        if(group.getGroupList()!=null)
        {
            List<GroupFrom> groupFromList=new ArrayList<GroupFrom>();
            for(Group group1:group.getGroupList()){
                GroupFrom groupFrom1=new GroupFrom();
                if(group1.getId()!=0){
                    groupFrom1.id=group1.getId();
                }
                if(group1.getGroupName()!=null){
                    groupFrom1.groupName=group1.getGroupName();
                }
                if(group1.getDescription()!=null){
                    groupFrom1.description=group1.getDescription();
                }
                if(group1.getCreatedDate()!=null){
                    try {
                        groupFrom1.createdDate=dateFormat.format(group1.getCreatedDate().toDate());
                    }
                    catch (Exception e){
                        logger.error("groupToGroupFrom-createdDate exception:",e);

                    }
                }
                if(group1.getLastModifiedDate()!=null){
                    try {
                        groupFrom1.lastModifiedDate=dateFormat.format(group1.getLastModifiedDate().toDate());
                    }
                    catch (Exception e){
                        logger.error("groupToGroupFrom-lastModifiedDate exception:",e);

                    }
                }

                    groupFrom1.isDel=group1.getIsDel();

                groupFrom1.users=null;
                groupFrom1.groupList=null;
                groupFrom1.group=null;
                groupFromList.add(groupFrom1);
            }
            groupFrom.groupList=groupFromList;
        }
        if(group.getGroup()!=null){//-----group.group
            GroupFrom groupFrom1=new GroupFrom();
            if(group.getGroup().getId()!=0){
                groupFrom1.id=group.getGroup().getId();
            }
            if(group.getGroup().getGroupName()!=null){
                groupFrom1.groupName=group.getGroup().getGroupName();
            }
            if(group.getGroup().getDescription()!=null){
                groupFrom1.description=group.getGroup().getDescription();
            }
            if(group.getGroup().getCreatedDate()!=null){
                try{
                    groupFrom1.createdDate=dateFormat.format(group.getGroup().getCreatedDate().toDate());
                }
                catch (Exception e){
                    logger.error("groupToGroupFrom1-createdDate exception:",e);

                }

            }
            if(group.getGroup().getLastModifiedDate()!=null){
                try {
                    groupFrom1.lastModifiedDate=dateFormat.format(group.getGroup().getLastModifiedDate().toDate());
                }
                catch (Exception e){
                    logger.error("groupToGroupFrom1-lastModifiedDate exception:",e);

                }

            }

                groupFrom1.isDel=group.getGroup().getIsDel();

            groupFrom1.users=null;
            groupFrom1.groupList=null;
            groupFrom1.group=null;

            groupFrom.group=groupFrom1;
        }
        return groupFrom;
    }
    public CommentContentFrom commentContentToCommentContentForm(CommentContent commentContent){
        CommentContentFrom commentContentFrom=new CommentContentFrom();
        if(commentContent.getId()!=0){
            commentContentFrom.id=commentContent.getId();
        }
        if(commentContent.getBase30url()!=null){
            commentContentFrom.Base30url=commentContent.getBase30url();
        }
        if(commentContent.getImageurl()!=null){
            commentContentFrom.Imageurl=commentContent.getImageurl();
        }
        if(commentContent.getJsignatureBase30url()!=null){
            commentContentFrom.JsignatureBase30url=commentContent.getJsignatureBase30url();
        }
        if(commentContent.getJsignatureImageurl()!=null){
            commentContentFrom.JsignatureImageurl=commentContent.getJsignatureImageurl();
        }
        if(commentContent.getContentText()!=null){
            commentContentFrom.contentText=commentContent.getContentText();
        }
        if(commentContent.getContentUsers()!=null){
            List<UserFrom> contentUsers= new ArrayList<UserFrom>();
            for(User user:commentContent.getContentUsers()){
                UserFrom contentUser=userToUserForm(user);
                contentUsers.add(contentUser);
            }
            commentContentFrom.ContentUsers=contentUsers;
        }
        if(commentContent.getCreatedDate()!=null){
            try{
                commentContentFrom.createdDate=dateFormat.format(commentContent.getCreatedDate().toDate());
            }
            catch (Exception e){
                logger.error("CommentContentFrom-createdDate exception:",e);

            }
        }
        if(commentContent.getLastModifiedDate()!=null){
            try {
                commentContentFrom.lastModifiedDate=dateFormat.format(commentContent.getLastModifiedDate().toDate());
            }
            catch (Exception e){
                logger.error("CommentContentFrom-lastModifiedDate exception:",e);

            }
        }
        if(commentContent.isEnabled()!=false){
            commentContentFrom.enabled=commentContent.isEnabled();
        }
        return commentContentFrom;
    }
    public AttachmentFrom attachmentToAttachmentFrom(Attachment attachment){
        AttachmentFrom attachmentFrom=new AttachmentFrom();
        if(attachment.getId()!=0){
            attachmentFrom.id=attachment.getId();
        }
        if(attachment.getAttachmentTittle()!=null){
            attachmentFrom.attachmentTittle=attachment.getAttachmentTittle();
        }
        if(attachment.getAttachmentFilePath()!=null){
            attachmentFrom.attachmentFilePath=attachment.getAttachmentFilePath();
        }
        if(attachment.getMissiveVersion()!=null){
            MissiveVersionFrom mvf=new MissiveVersionFrom();
            if(attachment.getMissiveVersion().getId()!=0){
                mvf.id=attachment.getMissiveVersion().getId();
            }
            if(attachment.getMissiveVersion().getVersionNumber()!=0){
                mvf.versionNumber=attachment.getMissiveVersion().getVersionNumber();
            }
            if(attachment.getMissiveVersion().getMissiveTittle()!=null){
                mvf.missiveTittle=attachment.getMissiveVersion().getMissiveTittle();
            }
            if(attachment.getMissiveVersion().getDocFilePath()!=null){
                mvf.docFilePath=attachment.getMissiveVersion().getDocFilePath();
            }
            if(attachment.getMissiveVersion().getPdfFilePath()!=null){
                mvf.pdfFilePath=attachment.getMissiveVersion().getPdfFilePath();
            }
            if(attachment.getMissiveVersion().getCreatedDate()!=null){
                try {
                    mvf.createdDate=dateFormat.format(attachment.getMissiveVersion().getCreatedDate().toDate());
                }
                catch (Exception e){
                    logger.error("AttachmentFrom-createdDate exception:",e);

                }
            }
            mvf.missive=null;
            mvf.attachments=null;

            attachmentFrom.missiveVersion=mvf;
        }
        return  attachmentFrom;
    }
    public MissiveTypeFrom missiveTypeToMissiveTypeFrom(MissiveType missiveType){
        MissiveTypeFrom missiveTypeFrom=new MissiveTypeFrom();
        if(missiveType.getId()!=0){
            missiveTypeFrom.id=missiveType.getId();
        }
        if(missiveType.getTypeName()!=null){
            missiveTypeFrom.typeName=missiveType.getTypeName();
        }
        if(missiveType.getCreatedDate()!=null){
            try {
                missiveTypeFrom.createdDate=dateFormat.format(missiveType.getCreatedDate().toDate());
            }
            catch (Exception e){
                logger.error("MissiveTypeFrom-createdDate exception:",e);

            }
        }
        if(missiveType.getLastModifiedDate()!=null){
            try {
                missiveTypeFrom.lastModifiedDate=dateFormat.format(missiveType.getLastModifiedDate().toDate());
            }
            catch (Exception e){
                logger.error("MissiveTypeFrom-lastModifiedDate exception:",e);

            }
            //missiveTypeFrom.lastModifiedDate=dateFormat.format(missiveType.getLastModifiedDate());

        }
        /*if(missiveType.isDel()!=false){
            missiveTypeFrom.isDeleted=missiveType.isDel();
        }*/
        missiveTypeFrom.missives=null;//公文类型.公文s设为NULL
        return missiveTypeFrom;
    }
    public SecretLevelFrom secretLevelToSecretLevelFrom(SecretLevel secretLevel){
        SecretLevelFrom secretLevelFrom=new SecretLevelFrom();
        if(secretLevel.getId()!=0){
            secretLevelFrom.id=secretLevel.getId();
        }
        if(secretLevel.getSecretLevelName()!=null){
            secretLevelFrom.secretLevelName=secretLevel.getSecretLevelName();
        }
        secretLevelFrom.missives=null;
        return secretLevelFrom;
    }
    public MissiveVersionFrom missiveVersionToMssiveVersionFrom(MissiveVersion missiveVersion){
        MissiveVersionFrom missiveVersionFrom=new MissiveVersionFrom();
        if(missiveVersion.getId()!=0){
            missiveVersionFrom.id=missiveVersion.getId();
        }
        if(missiveVersion.getVersionNumber()!=0){
            missiveVersionFrom.versionNumber=missiveVersion.getVersionNumber();
        }
        if(missiveVersion.getMissiveTittle()!=null){
            missiveVersionFrom.missiveTittle=missiveVersion.getMissiveTittle();
        }
        if(missiveVersion.getDocFilePath()!=null){
            missiveVersionFrom.docFilePath=missiveVersion.getDocFilePath();
        }
        if(missiveVersion.getPdfFilePath()!=null){
            missiveVersionFrom.pdfFilePath=missiveVersion.getPdfFilePath();
        }
        if(missiveVersion.getAttachments()!=null){
            List<AttachmentFrom> attachmentFroms=new ArrayList<AttachmentFrom>();
            for (Attachment attachment:missiveVersion.getAttachments()){
                AttachmentFrom attachmentFrom=attachmentToAttachmentFrom(attachment);
                attachmentFroms.add(attachmentFrom);
            }
            missiveVersionFrom.attachments=attachmentFroms;
        }
        if(missiveVersion.getCreatedDate()!=null){
            try {
                missiveVersionFrom.createdDate=dateFormat.format(missiveVersion.getCreatedDate().toDate());
            }
            catch (Exception e){
                logger.error("MissiveVersionFrom-createdDate exception:",e);

            }
        }
        if(missiveVersion.getMissive()!=null){
            MissiveFrom missiveFrom=new MissiveFrom();
            if(missiveVersion.getMissive().getId()!=0){
                missiveFrom.id=missiveVersion.getMissive().getId();
            }
            if(missiveVersion.getMissive().getName()!=null){
                missiveFrom.name=missiveVersion.getMissive().getName();
            }
            missiveFrom.versions=null;
            if(missiveVersion.getMissive().getMissiveType()!=null){
                MissiveTypeFrom typeFrom=new MissiveTypeFrom();
                typeFrom=missiveTypeToMissiveTypeFrom(missiveVersion.getMissive().getMissiveType());
                missiveFrom.missiveType=typeFrom;
            }
            if(missiveVersion.getMissive().getSecretLevel()!=null){
                SecretLevelFrom secretLevelFrom=new SecretLevelFrom();
                secretLevelFrom=secretLevelToSecretLevelFrom(missiveVersion.getMissive().getSecretLevel());
                missiveFrom.secretLevel=secretLevelFrom;
            }
            if(missiveVersion.getMissive().getMissiveNum()!=null){
                missiveFrom.missiveNum=missiveVersion.getMissive().getMissiveNum();
            }
            if(missiveVersion.getMissive().getMissiveCreateUser()!=null){
                UserFrom userFrom=new UserFrom();
                userFrom=userToUserForm(missiveVersion.getMissive().getMissiveCreateUser());
                missiveFrom.MissiveCreateUser=userFrom;
            }
            missiveVersionFrom.missive=missiveFrom;
        }
        return missiveVersionFrom;
    }
    public MissiveFrom missiveToMissiveFrom(Missive missive){
        MissiveFrom missiveFrom=new MissiveFrom();
        if(missive.getId()!=0){
            missiveFrom.id=missive.getId();
        }
        if(missive.getName()!=null){
            missiveFrom.name=missive.getName();
        }
        if(missive.getVersions()!=null){
            List<MissiveVersionFrom> missiveVersionList=new ArrayList<MissiveVersionFrom>();
            for(MissiveVersion missiveVersion:missive.getVersions()){
                MissiveVersionFrom versionFrom=new MissiveVersionFrom();
                versionFrom=missiveVersionToMssiveVersionFrom(missiveVersion);
                missiveVersionList.add(versionFrom);
            }
            missiveFrom.versions=missiveVersionList;
        }
        if(missive.getMissiveType()!=null){
            MissiveTypeFrom missiveTypeFrom=new MissiveTypeFrom();
            missiveTypeFrom=missiveTypeToMissiveTypeFrom(missive.getMissiveType());
            missiveFrom.missiveType=missiveTypeFrom;
        }
        if(missive.getSecretLevel()!=null){
            SecretLevelFrom secretLevelFrom=new SecretLevelFrom();
            secretLevelFrom=secretLevelToSecretLevelFrom(missive.getSecretLevel());
            missiveFrom.secretLevel=secretLevelFrom;
        }
        if(missive.getMissiveNum()!=null){
            missiveFrom.missiveNum=missive.getMissiveNum();
        }
        if(missive.getMissiveCreateUser()!=null){
            UserFrom userFrom=new UserFrom();
            userFrom=userToUserForm(missive.getMissiveCreateUser());
            missiveFrom.MissiveCreateUser=userFrom;
        }
        return missiveFrom;
    }
    public UrgentLevelForm urgentLevel2UrgentLevelForm(UrgentLevel urgentLevel){
        UrgentLevelForm ulf = new UrgentLevelForm();
        ulf.setId(urgentLevel.getId());
        if(urgentLevel.getValue()!=null){
            ulf.setValue(urgentLevel.getValue());
        }
        return ulf;
    }
    public CommentContentFrom commontContent2CommentContentForm(CommentContent cc){
        CommentContentFrom ccf = new CommentContentFrom();
        if(cc!=null){

                ccf.setId(cc.getId());

            if(cc.getBase30url()!=null){
                ccf.setBase30url(cc.getBase30url());
            }
            if(cc.getImageurl()!=null){
                ccf.setImageurl(cc.getImageurl());
            }
            if(cc.getJsignatureImageurl()!=null){
                ccf.setJsignatureImageurl(cc.getJsignatureImageurl());
            }
            if(cc.getJsignatureBase30url()!=null){
                ccf.setJsignatureBase30url(cc.getJsignatureBase30url());
            }
            if(cc.getContentText()!=null){
                ccf.setContentText(cc.getContentText());
            }
            if(cc.getContentUsers()!=null){
                List<UserFrom> luf = new ArrayList<UserFrom>();
                List<User> lu =cc.getContentUsers();
                for(User uu:lu){
                    UserFrom uf = userToUserForm(uu);
                    luf.add(uf);
                }
                ccf.setContentUsers(luf);
            }
            ccf.setEnabled(cc.isEnabled());
        }
        return  ccf;
    }
    public TaskForm Tasks2TaskForm(Task task){
        TaskForm tf = new TaskForm();
        tf.setId(Long.parseLong(task.getId()));
        tf.setName(task.getName());
        tf.setProcessDefinitionId(task.getProcessDefinitionId());
        tf.setProcessInstanceId(Long.parseLong(task.getProcessInstanceId()));
        return tf;
    }
    public MissiveReceiveRender MissiveReceive2MissiveReceiveRender(MissiveRecSeeCard mrsc,String taskName){
        MissiveReceiveRender mrr = new MissiveReceiveRender();
        if(mrsc.getInstanceId()!=null){
        Long instanceId =Long.parseLong(mrsc.getInstanceId());
        if(mrsc!=null) {
            if (mrsc.getId() != 0l) {
                mrr.id = mrsc.getId();
            }
            //背景图片
            if (mrsc.getBgPngPath() != null) {
                mrr.setBgPngPath(mrsc.getBgPngPath());
            }

            //公文信息
            if (mrsc.getMissiveInfo() != null) {
                mrr.setMissiveInfo(missiveToMissiveFrom(mrsc.getMissiveInfo()));
            }

            //公文标题
            if (mrsc.getTitle() != null) {
                mrr.setTitle(mrsc.getTitle());
            }

            //来文部门
            if (mrsc.getSendUnit() != null) {
                mrr.setSendUnit(mrsc.getSendUnit());
            }

            //收文年份
            if (mrsc.getReceiveYear() != null) {
                mrr.setReceiveYear(mrsc.getReceiveYear());
            } else {
                mrr.setReceiveYear(String.valueOf(cf.getCurrentDateTime()[0]));
            }

            //收文月份
            if (mrsc.getReceiveMonth() != null) {
                mrr.setReceiveMonth(mrsc.getReceiveMonth());
            } else {
                mrr.setReceiveMonth(String.valueOf(cf.getCurrentDateTime()[1]));
            }

            //收文日
            if (mrsc.getReceiveDay() != null) {
                mrr.setReceiveDay(mrsc.getReceiveDay());
            } else {
                mrr.setReceiveDay(String.valueOf(cf.getCurrentDateTime()[2]));
            }

            //收文号
            String missiveCode="";
            if (mrsc.getCode() != null) {
                if(mrsc.getCode().contains("|")){
                    missiveCode=mrsc.getCode().split("\\|")[1];
                    String  missiveCode1=missiveCode.split("\\ ")[missiveCode.split("\\ ").length-1];
                    mrr.setCode(missiveCode);
                    mrr.setCode1(missiveCode1);
                }else {
                mrr.setCode(String.valueOf(mrsc.getCode()));
                }
            }


            //文件字号
            if (mrsc.getMissiveNumber() != null) {
                mrr.setMissiveNumber(mrsc.getMissiveNumber());
            }

            //份数
            if (mrsc.getFileCount() != null) {
                mrr.setFileCount(mrsc.getFileCount());
            }

            //紧急程度
            if (mrsc.getUrgencyLevel() != null) {
                UrgentLevelForm urgentForm = this.urgentLevel2UrgentLevelForm(mrsc.getUrgencyLevel());
                mrr.setUrgencyLevel(urgentForm);
            }

            //截止日期
            if (mrsc.getDealDeadline() != null) {
                mrr.setDealDeadline(mrsc.getDealDeadline());
            }

            //来文摘要
            if (mrsc.getMissiveAbstract() != null) {
                mrr.setMissiveAbstract(mrsc.getMissiveAbstract());
            }

            //办公室拟办
            if (mrsc.getOfficeToDo() != null) {
                mrr.setOfficeToDo(mrsc.getOfficeToDo());
            }

            //拟办月
            if (mrsc.getToDoDateMonth() != null) {
                mrr.setToDoDateMonth(mrsc.getToDoDateMonth());
            } else if (mrsc.getToDoDateMonth() == null && taskName.equals("办公室拟办及审核")) {
                mrr.setToDoDateMonth(String.valueOf(cf.getCurrentDateTime()[1]));
            }

            //拟办日
            if (mrsc.getToDoDateDay() != null) {
                mrr.setToDoDateDay(mrsc.getToDoDateDay());
            } else if (mrsc.getToDoDateDay() == null && taskName.equals("办公室拟办及审核")) {
                mrr.setToDoDateDay(String.valueOf(cf.getCurrentDateTime()[2]));
            }

            //办公室登记修改意见
            if (mrsc.getReModifyContent() != null) {
                mrr.setReModifyContent(mrsc.getReModifyContent());
            }
            //办公室拟办修改意见
            if (mrsc.getToModifyContent() != null) {
                mrr.setToModifyContent(mrsc.getToModifyContent());
            }


            //领导批示
            if (mrsc.getLeaderInstruct() != null) {
                CommentContentFrom LeaderInstruct = commontContent2CommentContentForm(mrsc.getLeaderInstruct());
                mrr.setLeaderInstruct(LeaderInstruct);
            }

            //批示月
            if (mrsc.getInstructDateMonth() != null) {
                mrr.setInstructDateMonth(mrsc.getInstructDateMonth());
            } else if (mrsc.getInstructDateMonth() == null && taskName.equals("领导批示")) {
                mrr.setInstructDateMonth(String.valueOf(cf.getCurrentDateTime()[1]));
            }

            //批示日
            if (mrsc.getInstructDateDay() != null) {
                mrr.setInstructDateDay(mrsc.getInstructDateDay());
            } else if (mrsc.getInstructDateDay() == null && taskName.equals("        ")) {
                mrr.setInstructDateDay(String.valueOf(cf.getCurrentDateTime()[2]));
            }

            //阅办人签字
            if (mrsc.getLookerSign() != null) {
                CommentContentFrom lookerSign = commontContent2CommentContentForm(mrsc.getLookerSign());
                mrr.setLookerSign(lookerSign);
            }

            //承办情况
            if (mrsc.getUndertake() != null) {
                CommentContentFrom undertake = commontContent2CommentContentForm(mrsc.getUndertake());
                mrr.setUndertake(undertake);
            }
            //任务执行人
            MissiveReceiveTaskDealer mrtd = mrtdr.getTaskDealer(instanceId);
            if (mrtd != null) {
                MissiveReceiveTaskDealerData mrtdd = missiveDealerToDealerData(mrtd);
                mrr.setTaskDealer(mrtdd);
            }
        }



        }
        return mrr;
    }
    public MissiveReceiveTaskDealerData missiveDealerToDealerData(MissiveReceiveTaskDealer mrtd){
        MissiveReceiveTaskDealerData mrtdd = new MissiveReceiveTaskDealerData();
        if(mrtd.getInstanceId()!=null){
            mrtdd.setInstanceId(mrtd.getInstanceId());
        }
        if(mrtd.getChargerCheck()!=null){
            mrtdd.setChargerCheck(mrtd.getChargerCheck());
        }
        if(mrtd.getLeaderSign()!=null){
            mrtdd.setLeaderSign(mrtd.getLeaderSign());
        }
        if(mrtd.getOfficeDispose()!=null){
            mrtdd.setOfficeDispose(mrtd.getOfficeDispose());
        }
        if(mrtd.getOfficeHandle()!=null){
            mrtdd.setOfficeHandle(mrtd.getOfficeHandle());
        }
        if(mrtd.getOfficeRegist()!=null){
            mrtdd.setOfficeRegist(mrtd.getOfficeRegist());
        }
        if(mrtd.getReado()!=null){
            mrtdd.setReado(mrtd.getReado());
        }
        if(mrtd.getReadoDispose()!=null){
            mrtdd.setReadoDispose(mrtd.getReadoDispose());
        }
        if(mrtd.getUndertakeDispose()!=null){
            mrtdd.setUndertakeDispose(mrtd.getUndertakeDispose());
        }
        return mrtdd;
    }
    public MissivePublishForm MissivePublishToMissivePublishForm(MissivePublish missivePublish){
        MissivePublishForm missivePublishForm=new MissivePublishForm();
        if(missivePublish!=null){
            if(missivePublish.getId()!=0){
                missivePublishForm.id=missivePublish.getId();
            }
            if(missivePublish.getProcessID()!=0){
                missivePublishForm.processID=missivePublish.getProcessID();
            }
            if(missivePublish.getTaskID()!=0){
                missivePublishForm.taskID=missivePublish.getTaskID();
            }
            if(missivePublish.getMissiveInfo()!=null){
                MissiveFrom missiveFrom=new MissiveFrom();
                missiveFrom=missiveToMissiveFrom(missivePublish.getMissiveInfo());
                missivePublishForm.missiveInfo=missiveFrom;
            }
            if(missivePublish.getSignIssueUser()!=null){//-------公文表单.w签发用户
                UserFrom siuf = userToUserForm(missivePublish.getSignIssueUser());
                missivePublishForm.SignIssueUser=siuf;
            }
            if(missivePublish.getSignIssueContent()!=null){//-------公文表单.签发内容
                CommentContentFrom ccf=commentContentToCommentContentForm(missivePublish.getSignIssueContent());
                missivePublishForm.signIssueContent=ccf;
            }
            if(missivePublish.getCounterSignUsers()!=null){//-------公文表单.会签人员s
                List<UserFrom> siuflist=new ArrayList<UserFrom>();
                for(User user:missivePublish.getCounterSignUsers()){
                    UserFrom userFrom=userToUserForm(user);
                    siuflist.add(userFrom);
                }
                missivePublishForm.CounterSignUsers=siuflist;
            }
            if(missivePublish.getCounterSignContent()!=null){//-------公文表单.会签内容
                CommentContentFrom ccf=commentContentToCommentContentForm(missivePublish.getCounterSignContent());
                missivePublishForm.counterSignContent=ccf;
            }
            if(missivePublish.getMainSendGroups()!=null){//==----------公文表单.主送部门
                List<GroupFrom> gflist=new ArrayList<GroupFrom>();
                for(Group group:missivePublish.getMainSendGroups()){
                    GroupFrom groupFrom=groupToGroupFrom(group);
                    gflist.add(groupFrom);
                }
                missivePublishForm.MainSendGroups=gflist;
            }
            if(missivePublish.getCopytoGroups()!=null){//==----------公文表单.抄送部门
                List<GroupFrom> gflist=new ArrayList<GroupFrom>();
                for(Group group:missivePublish.getCopytoGroups()){
                    GroupFrom groupFrom=groupToGroupFrom(group);
                    gflist.add(groupFrom);
                }
                missivePublishForm.CopytoGroups=gflist;
            }
            if(missivePublish.getDrafterUser()!=null){//---------------公文表单.拟稿人
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getDrafterUser());
                missivePublishForm.DrafterUser=userFrom;
            }
            if(missivePublish.getComposeUser()!=null){//---------------公文表单.排版人
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getComposeUser());
                missivePublishForm.ComposeUser=userFrom;
            }
            if(missivePublish.getDep_LeaderCheckUser()!=null){//---------------公文表单.部门领导审核
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getDep_LeaderCheckUser());
                missivePublishForm.Dep_LeaderCheckUser=userFrom;
            }
            if(missivePublish.getOfficeCheckUser()!=null){//---------------公文表单.办公室复核
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getOfficeCheckUser());
                missivePublishForm.OfficeCheckUser=userFrom;
            }
            if(missivePublish.getPrintCount()!=0){
                missivePublishForm.printCount=missivePublish.getPrintCount();
            }
            if(missivePublish.getCheckReader()!=null){//---------------公文表单.校对
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getCheckReader());
                missivePublishForm.CheckReader=userFrom;
            }
            if(missivePublish.getGov_info_attr()!=0){
                missivePublishForm.Gov_info_attr=missivePublish.getGov_info_attr();
            }
            if(missivePublish.getMissiveTittle()!=null){
                missivePublishForm.missiveTittle=missivePublish.getMissiveTittle();
            }
            if(missivePublish.getAttachmentTittle()!=null){
                missivePublishForm.attachmentTittle=missivePublish.getAttachmentTittle();
            }

        }

        return missivePublishForm;

    }
}
