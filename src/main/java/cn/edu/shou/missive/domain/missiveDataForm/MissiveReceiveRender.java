package cn.edu.shou.missive.domain.missiveDataForm;

import cn.edu.shou.missive.domain.MissiveReceiveTaskDealer;

/**
 * Created by TISSOT on 2014/8/21.
 */
public class MissiveReceiveRender extends BaseEntityForm {
    private String title;//公文标题
    private Long instanceId;///流程实例编号

    private MissiveFrom missiveInfo;//公文信息
    private String bgPngPath;//背景图片

    private Long missiveId;//公文编号
    private String receiveYear;//收文日期_年
    private String receiveMonth;//收文日期_月
    private String receiveDay;//收文日期_日
    private String code;//收文号
    private String code1;//收文小号
    private String missiveNumber;//文件字号
    private String fileCount;//份数

    private UrgentLevelForm urgencyLevel;//紧急程度

    private String dealDeadline;//办理时限
    private String missiveAbstract;//来文摘要
    private String sendUnit;//来文部门
    private String officeToDo;//办公室拟办
    private String toDoDateMonth;//办公室拟办日期_月
    private String toDoDateDay;//办公室拟办日期_日

    private String reModifyContent;//办公室审核修改办公室登记
    private String toModifyContent;//办公室审核修改办公室拟办

    private CommentContentFrom leaderInstruct;//领导批示
    private String instructDateMonth;//批示日期_月
    private String instructDateDay;//批示日期_日

    private CommentContentFrom lookerSign;//阅办人签字

    private CommentContentFrom undertake;//承办情况

    private MissiveReceiveTaskDealerData taskDealer;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOfficeToDo() {
        return officeToDo;
    }

    public void setOfficeToDo(String officeToDo) {
        this.officeToDo = officeToDo;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public MissiveFrom getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(MissiveFrom missiveInfo) {
        this.missiveInfo = missiveInfo;
    }


    public Long getMissiveId() {
        return missiveId;
    }

    public void setMissiveId(Long missiveId) {
        this.missiveId = missiveId;
    }


    public String getReceiveYear() {
        return receiveYear;
    }

    public void setReceiveYear(String receiveYear) {
        this.receiveYear = receiveYear;
    }


    public String getReceiveMonth() {
        return receiveMonth;
    }

    public void setReceiveMonth(String receiveMonth) {
        this.receiveMonth = receiveMonth;
    }


    public String getReceiveDay() {
        return receiveDay;
    }

    public void setReceiveDay(String receiveDay) {
        this.receiveDay = receiveDay;
    }


    public String getMissiveNumber() {
        return missiveNumber;
    }

    public void setMissiveNumber(String missiveNumber) {
        this.missiveNumber = missiveNumber;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }


    public UrgentLevelForm getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgentLevelForm urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }


    public String getDealDeadline() {
        return dealDeadline;
    }

    public void setDealDeadline(String dealDeadline) {
        this.dealDeadline = dealDeadline;
    }


    public String getMissiveAbstract() {
        return missiveAbstract;
    }

    public void setMissiveAbstract(String missiveAbstract) {
        this.missiveAbstract = missiveAbstract;
    }


    public String getToDoDateMonth() {
        return toDoDateMonth;
    }

    public void setToDoDateMonth(String toDoDateMonth) {
        this.toDoDateMonth = toDoDateMonth;
    }


    public String getReModifyContent() {
        return reModifyContent;
    }

    public void setReModifyContent(String reModifyContent) {
        this.reModifyContent = reModifyContent;
    }

    public String getToModifyContent() {
        return toModifyContent;
    }

    public void setToModifyContent(String toModifyContent) {
        this.toModifyContent = toModifyContent;
    }

    public String getToDoDateDay() {
        return toDoDateDay;
    }

    public void setToDoDateDay(String toDoDateDay) {
        this.toDoDateDay = toDoDateDay;
    }


    public CommentContentFrom getLeaderInstruct() {
        return leaderInstruct;
    }

    public void setLeaderInstruct(CommentContentFrom leaderInstruct) {
        this.leaderInstruct = leaderInstruct;
    }


    public String getInstructDateDay() {
        return instructDateDay;
    }

    public void setInstructDateDay(String instructDateDay) {
        this.instructDateDay = instructDateDay;
    }

    public String getInstructDateMonth() {
        return instructDateMonth;
    }

    public void setInstructDateMonth(String instructDateMonth) {
        this.instructDateMonth = instructDateMonth;
    }

    public CommentContentFrom getLookerSign() {
        return lookerSign;
    }

    public void setLookerSign(CommentContentFrom lookerSign) {
        this.lookerSign = lookerSign;
    }


    public CommentContentFrom getUndertake() {
        return undertake;
    }

    public void setUndertake(CommentContentFrom undertake) {
        this.undertake = undertake;
    }

    public String getBgPngPath() {
        return bgPngPath;
    }

    public void setBgPngPath(String bgPngPath) {
        this.bgPngPath = bgPngPath;
    }

    public MissiveReceiveTaskDealerData getTaskDealer() {
        return taskDealer;
    }

    public void setTaskDealer(MissiveReceiveTaskDealerData taskDealer) {
        this.taskDealer = taskDealer;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public String getSendUnit() {
        return sendUnit;
    }

    public void setSendUnit(String sendUnit) {
        this.sendUnit = sendUnit;
    }
}


