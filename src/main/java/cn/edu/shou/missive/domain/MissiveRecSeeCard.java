package cn.edu.shou.missive.domain;


import javax.persistence.*;

/**
 * Created by TISSOT on 2014/7/22.
 */
@Entity
@Table(name="MissiveRecSeeCard")
public class MissiveRecSeeCard extends BaseEntity
{


    private String bgPngPath;//背景图片地址

    private String title;//公文标题
    private String instanceId;///流程实例编号
    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="missiveInfo")
    private Missive missiveInfo;//公文信息

    private Long missiveId;//公文编号
    private String receiveYear;//收文日期_年
    private String receiveMonth;//收文日期_月
    private String receiveDay;//收文日期_日
    private String code;//收文号
    private String missiveNumber;//文件字号
    private String fileCount;//份数
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="urgencyLevel")
    private UrgentLevel urgencyLevel;//紧急程度

    private String secretLevel;//密级
    private String dealDeadline;//办理时限
    private String sendUnit;//来文单位
    private String missiveAbstract;//来文摘要

    private String officeToDo;//办公室拟办
    private String toDoDateMonth;//办公室拟办日期_月
    private String toDoDateDay;//办公室拟办日期_日


    private String reModifyContent;//办公室审核修改办公室登记
    private String toModifyContent;//办公室审核修改办公室拟办

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="leaderInstruct")
    private CommentContent leaderInstruct;//领导批示
    private String instructDateMonth;//批示日期_月
    private String instructDateDay;//批示日期_日

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="lookerSign")
    private CommentContent lookerSign;//阅办人签字

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="undertake")
    private CommentContent undertake;//承办情况

    private boolean isDel;

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean isDel) {
        this.isDel = isDel;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Missive getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(Missive missiveInfo) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMissiveNumber() {
        return missiveNumber;
    }

    public void setMissiveNumber(String missiveNumber) {
        this.missiveNumber = missiveNumber;
    }

    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }

    public UrgentLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgentLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
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

    public String getOfficeToDo() {
        return officeToDo;
    }

    public void setOfficeToDo(String officeToDo) {
        this.officeToDo = officeToDo;
    }

    public String getToDoDateMonth() {
        return toDoDateMonth;
    }

    public void setToDoDateMonth(String toDoDateMonth) {
        this.toDoDateMonth = toDoDateMonth;
    }

    public String getToDoDateDay() {
        return toDoDateDay;
    }

    public void setToDoDateDay(String toDoDateDay) {
        this.toDoDateDay = toDoDateDay;
    }

    public CommentContent getLeaderInstruct() {
        return leaderInstruct;
    }

    public void setLeaderInstruct(CommentContent leaderInstruct) {
        this.leaderInstruct = leaderInstruct;
    }

    public String getInstructDateMonth() {
        return instructDateMonth;
    }

    public void setInstructDateMonth(String instructDateMonth) {
        this.instructDateMonth = instructDateMonth;
    }

    public String getInstructDateDay() {
        return instructDateDay;
    }

    public void setInstructDateDay(String instructDateDay) {
        this.instructDateDay = instructDateDay;
    }

    public CommentContent getLookerSign() {
        return lookerSign;
    }

    public void setLookerSign(CommentContent lookerSign) {
        this.lookerSign = lookerSign;
    }

    public CommentContent getUndertake() {
        return undertake;
    }

    public void setUndertake(CommentContent undertake) {
        this.undertake = undertake;
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

    public String getBgPngPath() {
        return bgPngPath;
    }

    public void setBgPngPath(String bgPngPath) {
        this.bgPngPath = bgPngPath;
    }

    public MissiveRecSeeCard() {
    }

    public String getSendUnit() {
        return sendUnit;
    }

    public void setSendUnit(String sendUnit) {
        this.sendUnit = sendUnit;
    }
}
