package cn.edu.shou.missive.domain.missiveDataForm;


import java.util.List;

/**
 * Created by sqhe on 14-7-12.
 */
public class CommentFrom  {

    public String taskName;

    public String OperationTime;
    public String Operator;
    public String MissiveTitle;
    public String instantId;
    public String OperatorName;
    public String Comment;
    public  String Acction;
    public  String JumpTo;
    public String OrginalNode;
    public  String LastTaskId;
    public  String verType;
    public  String versionNum;
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getOperationTime() {
        return OperationTime;
    }

    public void setOperationTime(String operationTime) {
        OperationTime = operationTime;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public String getMissiveTitle() {
        return MissiveTitle;
    }

    public void setMissiveTitle(String missiveTitle) {
        MissiveTitle = missiveTitle;
    }

    public String getInstantId() {
        return instantId;
    }

    public void setInstantId(String instantId) {
        this.instantId = instantId;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
    public String getAcction() {
        return Acction;
    }
    public void setAcction(String acction) {
        Comment = Acction;
    }


    public String getJumpTo() {
        return JumpTo;
    }

    public void setJumpTo(String jumpTo) {
        JumpTo = jumpTo;
    }

    public String getOrginalNode() {
        return OrginalNode;
    }

    public void setOrginalNode(String orginalNode) {
        OrginalNode = orginalNode;
    }



    public String getLastTaskId() {
        return LastTaskId;
    }

    public void setLastTaskId(String lastTaskId) {
        LastTaskId = lastTaskId;
    }

    public String getVerType() {
        return verType;
    }

    public void setVerType(String verType) {
        this.verType = verType;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }
}
