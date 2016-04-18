package cn.edu.shou.missive.domain.missiveDataForm;

import cn.edu.shou.missive.web.CommonFunction;

/**
 * Created by shou on 2015/2/1.
 */
public class ConvertPdfThread extends Thread {
    //commF.convertAtt2Pdf2(taskName, "办公室登记", newVersionId.toString(), newVersionNum, attachs, instanceId, taskId, htmlUrl, "missiveReceive");
    private CommonFunction commF;
    private String nowTaskName;
    private String needConvertTaskName;
    private String versionId;
    private Long versionNum;
    private String[] attachs;
    private Long instanceId;
    private Long taskId;
    private String htmlUrl;
    private String missiveType;

    public ConvertPdfThread(){
        super();
    }

    public ConvertPdfThread( String nowTaskName, String needConvertTaskName, String versionId, Long versionNum,  String[] attachs,Long instanceId, Long taskId, String htmlUrl, String missiveType,CommonFunction commF) {
        this.nowTaskName = nowTaskName;
        this.needConvertTaskName = needConvertTaskName;
        this.versionId = versionId;
        this.versionNum = versionNum;
        this.instanceId = instanceId;
        this.attachs = attachs;
        this.taskId = taskId;
        this.htmlUrl = htmlUrl;
        this.missiveType = missiveType;
        this.commF=commF;
    }

       //异步执行
    public void run(){
        commF.convertAtt2Pdf2(nowTaskName, needConvertTaskName, versionId, versionNum, attachs, instanceId, taskId, htmlUrl, missiveType);
    }
}
