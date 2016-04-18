package cn.edu.shou.missive.domain;

/**
 * Created by Administrator on 2014/7/18.
 */
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MissivePublish extends BaseEntity{

    @Getter @Setter
    private long processID;//實例ID
    @Getter @Setter
    private long taskID;//任務ID

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="missiveInfo")
    private Missive missiveInfo;//公文相關數據，收文號、密級、類型、

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="UserId")
    private User SignIssueUser;   //签发人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="signIssueContent")
    private CommentContent signIssueContent;//签发内容

    @Getter @Setter
    @ManyToMany
    @JoinTable(name="missivePublish_counterSignUser", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="UserId")})
    private List<User> CounterSignUsers;//会签人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="counterSignContent")
    private CommentContent counterSignContent;//会签内容
    //private Group group;


    @ManyToMany
    @JoinTable(name="missivePublish_mainSendGroups", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="GroupId")})
    @Getter @Setter
    private List<Group> MainSendGroups;   //主送部门

    @ManyToMany
    @JoinTable(name="missivePublish_copytoGroups", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="GroupId")})
    @Getter @Setter
    private List<Group> CopytoGroups;   //抄送部门


    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="DrafterUser")
    private User DrafterUser;//拟稿人
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="ComposeUser")
    private User ComposeUser;//排版人
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="Dep_LeaderCheckUser")
    private User Dep_LeaderCheckUser;//处(室)领导核稿
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="Dep_LeaderCheckContentId")
    private CommentContent Dep_LeaderCheckContent;//处(室)领导核稿内容

    private String Dep_LeaderCheckUserSuggestion;//处(室)领导返回修改意见
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="OfficeCheckUser")
    private User OfficeCheckUser;//办公室复核
    private String OfficeCheckUserSuggestion;//办公室返回修改意见
    @Getter @Setter
    private int printCount;//打印份数
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="CheckReader")
    private User CheckReader;//校对
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="Printer")
    private User Printer;//打印人
    @Getter @Setter
    private int Gov_info_attr;//政府信息属性
    @Getter @Setter
    private String missiveTittle;//标题
    @Getter @Setter
    private String attachmentTittle;//附件标题
    //    @ManyToOne(cascade= CascadeType.ALL)
//    @JoinColumn(name="backgroudImage")
    private String backgroudImage;

    //郑小罗 2014/12/24 添加主送、抄送字段
    @Getter @Setter
    private String MainSend;//主送单位 外单位
    @Getter @Setter
    private String CopyTo;//抄送单位 外单位


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

    public Missive getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(Missive missiveInfo) {
        this.missiveInfo = missiveInfo;
    }

    public User getSignIssueUser() {
        return SignIssueUser;
    }

    public void setSignIssueUser(User signIssueUser) {
        SignIssueUser = signIssueUser;
    }

    public CommentContent getSignIssueContent() {
        return signIssueContent;
    }

    public void setSignIssueContent(CommentContent signIssueContent) {
        this.signIssueContent = signIssueContent;
    }

    public List<User> getCounterSignUsers() {
        return CounterSignUsers;
    }

    public void setCounterSignUsers(List<User> counterSignUsers) {
        CounterSignUsers = counterSignUsers;
    }

    public CommentContent getCounterSignContent() {
        return counterSignContent;
    }

    public void setCounterSignContent(CommentContent counterSignContent) {
        this.counterSignContent = counterSignContent;
    }

    public List<Group> getMainSendGroups() {
        return MainSendGroups;
    }

    public void setMainSendGroups(List<Group> mainSendGroups) {
        MainSendGroups = mainSendGroups;
    }

    public List<Group> getCopytoGroups() {
        return CopytoGroups;
    }

    public void setCopytoGroups(List<Group> copytoGroups) {
        CopytoGroups = copytoGroups;
    }

    public User getDrafterUser() {
        return DrafterUser;
    }

    public void setDrafterUser(User drafterUser) {
        DrafterUser = drafterUser;
    }

    public User getComposeUser() {
        return ComposeUser;
    }

    public void setComposeUser(User composeUser) {
        ComposeUser = composeUser;
    }

    public User getDep_LeaderCheckUser() {
        return Dep_LeaderCheckUser;
    }

    public void setDep_LeaderCheckUser(User dep_LeaderCheckUser) {
        Dep_LeaderCheckUser = dep_LeaderCheckUser;
    }

    public User getOfficeCheckUser() {
        return OfficeCheckUser;
    }

    public void setOfficeCheckUser(User officeCheckUser) {
        OfficeCheckUser = officeCheckUser;
    }

    public CommentContent getDep_LeaderCheckContent() {
        return Dep_LeaderCheckContent;
    }

    public void setDep_LeaderCheckContent(CommentContent dep_LeaderCheckContent) {
        Dep_LeaderCheckContent = dep_LeaderCheckContent;
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

    public User getCheckReader() {
        return CheckReader;
    }

    public void setCheckReader(User checkReader) {
        CheckReader = checkReader;
    }

    public User getPrinter() {
        return Printer;
    }

    public void setPrinter(User printer) {
        Printer = printer;
    }

    public int getGov_info_attr() {
        return Gov_info_attr;
    }

    public void setGov_info_attr(int gov_info_attr) {
        Gov_info_attr = gov_info_attr;
    }

    public String getMissiveTittle() {
        return missiveTittle;
    }

    public void setMissiveTittle(String missiveTittle) {
        this.missiveTittle = missiveTittle;
    }

    public String getBackgroudImage() {
        return backgroudImage;
    }

    public void setBackgroudImage(String backgroudImage) {
        this.backgroudImage = backgroudImage;
    }

    public String getAttachmentTittle() {
        return attachmentTittle;
    }

    public void setAttachmentTittle(String attachmentTittle) {
        this.attachmentTittle = attachmentTittle;
    }

    public String getMainSend() {
        return MainSend;
    }

    public void setMainSend(String mainSend) {
        MainSend = mainSend;
    }

    public String getCopyTo() {
        return CopyTo;
    }

    public void setCopyTo(String copyTo) {
        CopyTo = copyTo;
    }


}