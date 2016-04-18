package cn.edu.shou.missive.domain.missiveDataForm;


import java.util.List;

/**
 * Created by TISSOT on 2014/9/23.
 */
public class TaskForm {
    private Long id;
    private String name;
    private String missiveVersion;//公文版本号
    private String missiveType;//公文类型
    private String typeTitle;//流程类型名称
    private String abbrevType; //流程类型简称
    private String cardTitle; //稿纸类型
    private String missiveTitle;//公文标题
    private  Long lastTaskId;//上一步任务ID
    private Long processInstanceId;
    private String processDefinitionId;
    private String urgencyLevel;//公文紧急程度
    private List<NextTask> taskOperate;//当前任务操作选项
    private int versionNum;
    private String taskStartTime;//任务创建时间
    private String taskEndTime;//任务完成时间
    private String taskUrl;
    private String intelTime;//任务拖延时间
    private String taskState;//任务状态
    private String taskAssName;
    private String delayWarm;
    private String delayNum;
    private String isPadDealMissive;//移动端能够是否处理该公文
    private String isPadSelectUser;//移动端是否进行选人操作
    private String description;
    private Long preTaskId;    //上一步任务编号
    private String htmlUrl;
    private String preOwner;
    private String taskCondition;  //当前任务名称
    private String nextTaskId;
    private String rollBackFlag;
    private String updateFlag;
    private String pdfLink;
    private String InfoLink;
    private String rollbackLink;
    private String deletedLink;
    private String missiveNum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getMissiveVersion() {
        return missiveVersion;
    }

    public void setMissiveVersion(String missiveVersion) {
        this.missiveVersion = missiveVersion;
    }

    public String getMissiveType() {
        return missiveType;
    }

    public void setMissiveType(String missiveType) {
        this.missiveType = missiveType;
    }

    public String getMissiveTitle() {
        return missiveTitle;
    }

    public void setMissiveTitle(String missiveTitle) {
        this.missiveTitle = missiveTitle;
    }

    public Long getLastTaskId() {
        return lastTaskId;
    }

    public void setLastTaskId(Long lastTaskId) {
        this.lastTaskId = lastTaskId;
    }

    public List<NextTask> getTaskOperate() {
        return taskOperate;
    }

    public void setTaskOperate(List<NextTask> taskOperate) {
        this.taskOperate = taskOperate;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getIntelTime() {
        return intelTime;
    }

    public void setIntelTime(String intelTime) {
        this.intelTime = intelTime;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTaskAssName() {
        return taskAssName;
    }

    public void setTaskAssName(String taskAssName) {
        this.taskAssName = taskAssName;
    }

    public String getDelayWarm() {
        return delayWarm;
    }

    public void setDelayWarm(String delayWarm) {
        this.delayWarm = delayWarm;
    }

    public String getDelayNum() {
        return delayNum;
    }

    public void setDelayNum(String delayNum) {
        this.delayNum = delayNum;
    }

    public String getIsPadSelectUser() {
        return isPadSelectUser;
    }

    public void setIsPadSelectUser(String isPadSelectUser) {
        this.isPadSelectUser = isPadSelectUser;
    }

    public String getIsPadDealMissive() {
        return isPadDealMissive;
    }

    public void setIsPadDealMissive(String isPadDealMissive) {
        this.isPadDealMissive = isPadDealMissive;
    }

    public String getDescription() {          //01-12孙乐 新添加 置顶
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPreTaskId() {
        return preTaskId;
    }

    public void setPreTaskId(Long preTaskId) {
        this.preTaskId = preTaskId;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getPreOwner() {
        return preOwner;
    }

    public void setPreOwner(String preOwner) {
        this.preOwner = preOwner;
    }

    public String getTaskCondition() {
        return taskCondition;
    }

    public void setTaskCondition(String taskCondition) {
        this.taskCondition = taskCondition;
    }

    public String getNextTaskId() {
        return nextTaskId;
    }

    public void setNextTaskId(String nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public String getRollBackFlag() {
        return rollBackFlag;
    }

    public void setRollBackFlag(String rollBackFlag) {
        this.rollBackFlag = rollBackFlag;
    }

    public String getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getAbbrevType() {
        return abbrevType;
    }

    public void setAbbrevType(String abbrevType) {
        this.abbrevType = abbrevType;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public String getInfoLink() {
        return InfoLink;
    }

    public void setInfoLink(String infoLink) {
        InfoLink = infoLink;
    }

    public String getRollbackLink() {
        return rollbackLink;
    }

    public void setRollbackLink(String rollbackLink) {
        this.rollbackLink = rollbackLink;
    }

    public String getDeletedLink() {
        return deletedLink;
    }

    public void setDeletedLink(String deletedLink) {
        this.deletedLink = deletedLink;
    }

    public String getMissiveNum() {
        return missiveNum;
    }

    public void setMissiveNum(String missiveNum) {
        this.missiveNum = missiveNum;
    }
}
