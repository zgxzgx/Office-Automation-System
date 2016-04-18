package cn.edu.shou.missive.domain;


import cn.edu.shou.missive.domain.missiveDataForm.*;
import org.hibernate.*;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.stat.SessionStatistics;
import org.jadira.usertype.spi.jta.HibernateSessionFactoryBean;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissivePublishFunction {

    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public UserSecret userToUserForm_Name_UserName(User user){

        UserSecret userFrom=new UserSecret();


        if(user.getUserName()!=null){
            userFrom.UserName=user.getUserName();
        }
        if(user.getName()!=null){
            userFrom.Name=user.getName();
        }
        if(user.getTel()!=null){
            userFrom.userTel=user.getTel();
        }

//        if(user.getGroup()!=null&&user.getGroup().getGroupName()!=null){//user.group
//            userFrom.GroupName=user.getGroup().getGroupName();
//        }

        return userFrom;
    }


    public List<UserSecret> userToUserForm_Name_UserName( List<Map<String, Object>> userlist){

        List<UserSecret> resultList = new ArrayList<UserSecret>();

        for (Map<String, Object> record : userlist)
        {
            UserSecret us = new UserSecret();
            us.setName(record.get("name").toString());
            us.setUserName(record.get("username").toString());
            us.setGroupName(record.get("group_name").toString());
            us.setAbrev(record.get("abrev")!=null?record.get("abrev").toString():"null");
            us.setAbbrev(record.get("abbrev")!=null?record.get("abbrev").toString():"null");
            us.setAbbrevName(record.get("abbrevname")!=null?record.get("abbrevname").toString():"null");
            resultList.add(us);
        }

        return resultList;
    }


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
//        if(user.getPassword()!=null){
//            userFrom.password=user.getPassword();
//        }
        userFrom.password="youcannotgetpasswordsbythisway";
        if(user.getImagePath()!=null){
            userFrom.imagePath=user.getImagePath();
        }
        if(user.getLastLoginTime()!=null){
            try{
                userFrom.lastLoginTime=dateFormat.format(user.getLastLoginTime());
            }
            catch (Exception e){}

        }
        if(user.isEnabled()!=false){
            userFrom.enabled=user.isEnabled();
        }
        if(user.getDescription()!=null){
            userFrom.description=user.getDescription();
        }
        if(user.getGroup()!=null){//user.group
            GroupFrom groupFrom=new GroupFrom();
            groupFrom=this.groupToGroupFrom(user.getGroup());

            userFrom.group=groupFrom;
        }

        return userFrom;
    }

    private UserFrom userToUserFormWithoutGroupInfo(User user){

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
//        if(user.getPassword()!=null){
//            userFrom.password=user.getPassword();
//        }
        userFrom.password="youcannotgetpasswordsbythisway";
        if(user.getImagePath()!=null){
            userFrom.imagePath=user.getImagePath();
        }
        if(user.getLastLoginTime()!=null){
            try{
                userFrom.lastLoginTime=dateFormat.format(user.getLastLoginTime());
            }
            catch (Exception e){}

        }
        if(user.isEnabled()!=false){
            userFrom.enabled=user.isEnabled();
        }
        if(user.getDescription()!=null){
            userFrom.description=user.getDescription();
        }

        userFrom.group=null;
        return userFrom;
    }


    public List<GroupFrom> convertGroupListToGFList(List<Map<String,Object>> groupList)
    {
        List<GroupFrom> GFList = new ArrayList<GroupFrom>();
        for(Map<String,Object> group:groupList)
        {
            GroupFrom gf =new GroupFrom();
            gf.setGroupName(group.get("group_name").toString());
            gf.setId(Long.parseLong(group.get("id").toString()));
            GFList.add(gf);
        }
        return GFList;
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
                userFrom=this.userToUserFormWithoutGroupInfo(user);
                userFroms.add(userFrom);
            }
            groupFrom.users=userFroms;
        }
        if(group.getCreatedDate()!=null){
            try {
                groupFrom.createdDate=dateFormat.format(group.getCreatedDate().toDate());
            }
            catch (Exception e){}
        }
        if(group.getLastModifiedDate()!=null){
            try{
                groupFrom.lastModifiedDate=dateFormat.format(group.getLastModifiedDate().toDate());
            }
            catch (Exception e){}
        }
        if(group.getIsDel()!=false){
            groupFrom.isDel=group.getIsDel();
        }
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
                    catch (Exception e){}
                }
                if(group1.getLastModifiedDate()!=null){
                    try {
                        groupFrom1.lastModifiedDate=dateFormat.format(group1.getLastModifiedDate().toDate());
                    }
                    catch (Exception e){}
                }
                if(group1.getIsDel()!=false){
                    groupFrom1.isDel=group1.getIsDel();
                }
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
                catch (Exception e){}

            }
            if(group.getGroup().getLastModifiedDate()!=null){
                try {
                    groupFrom1.lastModifiedDate=dateFormat.format(group.getGroup().getLastModifiedDate().toDate());
                }
                catch (Exception e){}

            }
            if(group.getGroup().getIsDel()!=false){
                groupFrom1.isDel=group.getGroup().getIsDel();
            }
            if(group.getGroup().getGroup()!=null){


                GroupFrom groupFrom1_1=new GroupFrom();
                if(group.getGroup().getGroup().getId()!=0){
                    groupFrom1_1.id=group.getGroup().getGroup().getId();
                }
                if(group.getGroup().getGroup().getGroupName()!=null){
                    groupFrom1_1.groupName=group.getGroup().getGroup().getGroupName();
                }
                if(group.getGroup().getGroup().getDescription()!=null){
                    groupFrom1_1.description=group.getGroup().getGroup().getDescription();
                }
                if(group.getGroup().getGroup().getCreatedDate()!=null){
                    try{
                        groupFrom1_1.createdDate=dateFormat.format(group.getGroup().getGroup().getCreatedDate().toDate());
                    }
                    catch (Exception e){}

                }
                if(group.getGroup().getGroup().getLastModifiedDate()!=null){
                    try {
                        groupFrom1_1.lastModifiedDate=dateFormat.format(group.getGroup().getGroup().getLastModifiedDate().toDate());
                    }
                    catch (Exception e){}

                }
                groupFrom1_1.users=null;
                groupFrom1_1.groupList=null;
                groupFrom1_1.group=null;

                groupFrom1.group=groupFrom1_1;

            }
            groupFrom1.users=null;
            groupFrom1.groupList=null;


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
            catch (Exception e){}
        }
        if(commentContent.getLastModifiedDate()!=null){
            try {
                commentContentFrom.lastModifiedDate=dateFormat.format(commentContent.getLastModifiedDate().toDate());
            }
            catch (Exception e){}
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
                catch (Exception e){}
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
            catch (Exception e){}
        }
        if(missiveType.getLastModifiedDate()!=null){
            try {
                missiveTypeFrom.lastModifiedDate=dateFormat.format(missiveType.getLastModifiedDate().toDate());
            }
            catch (Exception e){}

        }
        if(missiveType.isDeleted()!=false){
            missiveTypeFrom.isDeleted=missiveType.isDeleted();
        }
        missiveTypeFrom.missives=null;//公文类型.公文s设为NULL
        return missiveTypeFrom;
    }

    public UrgentLevelForm urgentLevelToUrgentLevelForm(UrgentLevel urgentLevel){
        UrgentLevelForm urgentLevelForm=new UrgentLevelForm();
        if(urgentLevel.getId()!=0){
            urgentLevelForm.id=urgentLevel.getId();
        }
        if(urgentLevel.getValue()!=null){
            urgentLevelForm.value=urgentLevel.getValue();
        }
        if(urgentLevel.getCreatedDate()!=null){
            try {
                urgentLevelForm.createdDate=dateFormat.format(urgentLevel.getCreatedDate().toDate());
            }
            catch (Exception e){}
        }
        if(urgentLevel.getLastModifiedDate()!=null){
            try {
                urgentLevelForm.lastModifiedDate=dateFormat.format(urgentLevel.getLastModifiedDate().toDate());
            }
            catch (Exception e){}

        }
        if(urgentLevel.isDel()!=false){
            urgentLevelForm.isDel=urgentLevel.isDel();
        }
        urgentLevelForm.missives=null;//公文类型.公文s设为NULL
        return urgentLevelForm;
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
            catch (Exception e){}
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
//            if(missiveVersion.getMissive().getMissiveNum()!=null){
//                if(missiveVersion.getMissive().getMissiveNum().contains("|"))
//                {
//                missiveFrom.missiveNum=missiveVersion.getMissive().getMissiveNum().split("\\|")[1];
//                }
//                else missiveFrom.missiveNum=missiveVersion.getMissive().getMissiveNum();
//            }
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


    public MissiveFrom missiveToMissiveFrom(Missive missive,String missiveType){
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
        if(missive.getUrgentLevel()!=null){
            UrgentLevelForm urgentLevelForm=new UrgentLevelForm();
            urgentLevelForm=urgentLevelToUrgentLevelForm(missive.getUrgentLevel());
            missiveFrom.urgentLevel=urgentLevelForm;
        }
        if(missive.getSecretLevel()!=null){
            SecretLevelFrom secretLevelFrom=new SecretLevelFrom();
            secretLevelFrom=secretLevelToSecretLevelFrom(missive.getSecretLevel());
            missiveFrom.secretLevel=secretLevelFrom;
        }


        if(missiveType=="missiveSign"){
            if(missive.getMissiveNum()!=null){
                if(missive.getMissiveNum().contains("|"))
                {
                    missiveFrom.missiveNum1=missive.getMissiveNum().split("\\|")[1];
                    missiveFrom.missiveNum=missive.getMissiveNum();
                }
                else {missiveFrom.missiveNum=missive.getMissiveNum();
                    missiveFrom.missiveNum1=missive.getMissiveNum();
                }
            }
        }else if(missive.getMissiveNum()!=null){
            missiveFrom.missiveNum=missive.getMissiveNum();
        }

        if(missive.getMissiveCreateUser()!=null){
            UserFrom userFrom=new UserFrom();
            userFrom=userToUserForm(missive.getMissiveCreateUser());
            missiveFrom.MissiveCreateUser=userFrom;
        }
        if(missive.getCopyToUsers()!=null){//-------copyto人员s
            List<UserFrom> ctuflist=new ArrayList<UserFrom>();
            for(User user:missive.getCopyToUsers()){
                UserFrom userFrom=userToUserForm(user);
                ctuflist.add(userFrom);
            }
            missiveFrom.copyToUsers=ctuflist;
        }
        return missiveFrom;
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
                missiveFrom=missiveToMissiveFrom(missivePublish.getMissiveInfo(),"missivePublish");
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

            if(missivePublish.getDep_LeaderCheckContent()!=null){//-------公文表单.部门领导审核内容
                CommentContentFrom ccf=commentContentToCommentContentForm(missivePublish.getDep_LeaderCheckContent());
                missivePublishForm.Dep_LeaderCheckContent=ccf;
            }

            String tempDep_LeaderCheckUserSuggestion = missivePublish.getDep_LeaderCheckUserSuggestion();
            missivePublishForm.Dep_LeaderCheckUserSuggestion=tempDep_LeaderCheckUserSuggestion;

            if(missivePublish.getOfficeCheckUser()!=null){//---------------公文表单.办公室复核
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getOfficeCheckUser());
                missivePublishForm.OfficeCheckUser=userFrom;
            }

            String tempOfficeCheckUserSuggestion = missivePublish.getOfficeCheckUserSuggestion();
            missivePublishForm.OfficeCheckUserSuggestion=tempOfficeCheckUserSuggestion;

            if(missivePublish.getPrintCount()!=0){
                missivePublishForm.printCount=missivePublish.getPrintCount();
            }
            if(missivePublish.getCheckReader()!=null){//---------------公文表单.校对
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getCheckReader());
                missivePublishForm.CheckReader=userFrom;
            }
            if(missivePublish.getPrinter()!=null){//---------------公文表单.校对
                UserFrom userFrom = new UserFrom();
                userFrom=userToUserForm(missivePublish.getPrinter());
                missivePublishForm.Printer=userFrom;
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
            if(missivePublish.getBackgroudImage()!=null){
//                MissiveFormBackGForm missiveFormBackGForm=missiveFormBackG2missiveFormBackGForm(missivePublish.getBackgroudImage());
                missivePublishForm.backgroudImage=missivePublish.getBackgroudImage();
            }
            //郑小罗 2014/12/25 添加主送外单位
            if(missivePublish.getMainSend()!=null){
                missivePublishForm.mainSend=missivePublish.getMainSend();
            }
            //郑小罗 2014/12/25 添加抄送外单位
            if(missivePublish.getMainSend()!=null){
                missivePublishForm.copyTo=missivePublish.getCopyTo();
            }

        }

        return missivePublishForm;

    }




}
