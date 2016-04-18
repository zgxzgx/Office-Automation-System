package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;

/**
 * Created by sqhe18 on 14-8-12.
 */
public class MissivePublishForm extends BaseEntityForm {
    public long processID;//實例ID
    
    public long taskID;//任務ID

    public MissiveFrom missiveInfo;//公文相關數據，收文號、密級、類型、

    public UserFrom SignIssueUser;   //签发人员
    public CommentContentFrom signIssueContent;//签发内容
    public List<UserFrom> CounterSignUsers;//会签人员
    public CommentContentFrom counterSignContent;//会签内容
    public List<GroupFrom> MainSendGroups;   //主送部门
    public List<GroupFrom> CopytoGroups;   //抄送部门
    public UserFrom DrafterUser;//拟稿人
    public UserFrom ComposeUser;//排版人
    public UserFrom Dep_LeaderCheckUser;//处(室)领导核稿
    public CommentContentFrom Dep_LeaderCheckContent;//处(室)领导核稿内容
    public String Dep_LeaderCheckUserSuggestion;//处(室)领导返回修改意见
    public UserFrom OfficeCheckUser;//办公室复核
    public String OfficeCheckUserSuggestion;//办公室返回修改意见
    public int printCount;//打印份数
    public UserFrom CheckReader;//校对
    public UserFrom Printer;//打印
    public int Gov_info_attr;//政府信息属性
    public String missiveTittle;//标题
    public String attachmentTittle;//附件标题
    public String backgroudImage;
    private String missiveFlag;//公文标志
//    private boolean isSend;

    //郑小罗 2014/12/25 18:03:35
    public String mainSend;//郑小罗 2014/12/25 添加主送外单位
    public String copyTo;//郑小罗 2014/12/25 添加抄送外单位
    public long getProcessID() {
        return processID;
    }

    public void setProcessID(long processID) {
        this.processID = processID;
    }

    public long getTaskID() {
        return taskID;
    }

    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }

    public MissiveFrom getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(MissiveFrom missiveInfo) {
        this.missiveInfo = missiveInfo;
    }

    public UserFrom getSignIssueUser() {
        return SignIssueUser;
    }

    public void setSignIssueUser(UserFrom signIssueUser) {
        SignIssueUser = signIssueUser;
    }

    public CommentContentFrom getSignIssueContent() {
        return signIssueContent;
    }

    public void setSignIssueContent(CommentContentFrom signIssueContent) {
        this.signIssueContent = signIssueContent;
    }

    public List<UserFrom> getCounterSignUsers() {
        return CounterSignUsers;
    }

    public void setCounterSignUsers(List<UserFrom> counterSignUsers) {
        CounterSignUsers = counterSignUsers;
    }

    public CommentContentFrom getCounterSignContent() {
        return counterSignContent;
    }

    public void setCounterSignContent(CommentContentFrom counterSignContent) {
        this.counterSignContent = counterSignContent;
    }

    public List<GroupFrom> getMainSendGroups() {
        return MainSendGroups;
    }

    public void setMainSendGroups(List<GroupFrom> mainSendGroups) {
        MainSendGroups = mainSendGroups;
    }

    public List<GroupFrom> getCopytoGroups() {
        return CopytoGroups;
    }

    public void setCopytoGroups(List<GroupFrom> copytoGroups) {
        CopytoGroups = copytoGroups;
    }

    public UserFrom getDrafterUser() {
        return DrafterUser;
    }

    public void setDrafterUser(UserFrom drafterUser) {
        DrafterUser = drafterUser;
    }

    public UserFrom getComposeUser() {
        return ComposeUser;
    }

    public void setComposeUser(UserFrom composeUser) {
        ComposeUser = composeUser;
    }

    public UserFrom getDep_LeaderCheckUser() {
        return Dep_LeaderCheckUser;
    }

    public void setDep_LeaderCheckUser(UserFrom dep_LeaderCheckUser) {
        Dep_LeaderCheckUser = dep_LeaderCheckUser;
    }

    public CommentContentFrom getDep_LeaderCheckContent() {
        return Dep_LeaderCheckContent;
    }

    public void setDep_LeaderCheckContent(CommentContentFrom dep_LeaderCheckContent) {
        Dep_LeaderCheckContent = dep_LeaderCheckContent;
    }

    public UserFrom getOfficeCheckUser() {
        return OfficeCheckUser;
    }

    public void setOfficeCheckUser(UserFrom officeCheckUser) {
        OfficeCheckUser = officeCheckUser;
    }

    public String getDep_LeaderCheckUserSuggestion() {
        return Dep_LeaderCheckUserSuggestion;
    }

    public void setDep_LeaderCheckUserSuggestion(String dep_LeaderCheckUserSuggestion) {
        Dep_LeaderCheckUserSuggestion = dep_LeaderCheckUserSuggestion;
    }

    public String getOfficeCheckUserSuggestion() {
        return OfficeCheckUserSuggestion;
    }

    public void setOfficeCheckUserSuggestion(String officeCheckUserSuggestion) {
        OfficeCheckUserSuggestion = officeCheckUserSuggestion;
    }

    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public UserFrom getCheckReader() {
        return CheckReader;
    }

    public void setCheckReader(UserFrom checkReader) {
        CheckReader = checkReader;
    }

    public int getGov_info_attr() {
        return Gov_info_attr;
    }

    public void setGov_info_attr(int gov_info_attr) {
        Gov_info_attr = gov_info_attr;
    }

    public UserFrom getPrinter() {
        return Printer;
    }

    public void setPrinter(UserFrom printer) {
        Printer = printer;
    }

    public String getBackgroudImage() {
        return backgroudImage;
    }

    public void setBackgroudImage(String backgroudImage) {
        this.backgroudImage = backgroudImage;
    }

    public String getMissiveTittle() {
        return missiveTittle;
    }

    public void setMissiveTittle(String missiveTittle) {
        this.missiveTittle = missiveTittle;
    }

    public String getAttachmentTittle() {
        return attachmentTittle;
    }

    public void setAttachmentTittle(String attachmentTittle) {
        this.attachmentTittle = attachmentTittle;
    }

    public String getMainSend() {
        return mainSend;
    }

    public void setMainSend(String mainSend) {
        this.mainSend = mainSend;
    }

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public String getMissiveFlag() {
        return missiveFlag;
    }

    public void setMissiveFlag(String missiveFlag) {
        this.missiveFlag = missiveFlag;
    }

//    public boolean isSend() {
//        return isSend;
//    }
//
//    public void setSend(boolean isSend) {
//        this.isSend = isSend;
//    }
}
