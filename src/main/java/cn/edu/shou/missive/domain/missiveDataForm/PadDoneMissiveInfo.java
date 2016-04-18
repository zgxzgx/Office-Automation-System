package cn.edu.shou.missive.domain.missiveDataForm;

/**
 * Created by shou on 2015/1/2.
 */
public class PadDoneMissiveInfo {
    Long taskId;
    String missiveTitle;
    String taskName;
    String missiveDoneTime;
    String missiveAddr;
    String urgency;

    public String getMissiveTitle() {
        return missiveTitle;
    }

    public void setMissiveTitle(String missiveTitle) {
        this.missiveTitle = missiveTitle;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMissiveDoneTime() {
        return missiveDoneTime;
    }

    public void setMissiveDoneTime(String missiveDoneTime) {
        this.missiveDoneTime = missiveDoneTime;
    }

    public String getMissiveAddr() {
        return missiveAddr;
    }

    public void setMissiveAddr(String missiveAddr) {
        this.missiveAddr = missiveAddr;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public PadDoneMissiveInfo(Long taskId, String missiveTitle, String taskName, String missiveDoneTime, String missiveAddr, String urgency) {
        this.taskId = taskId;
        this.missiveTitle = missiveTitle;
        this.taskName = taskName;
        this.missiveDoneTime = missiveDoneTime;
        this.missiveAddr = missiveAddr;
        this.urgency = urgency;
    }
}
