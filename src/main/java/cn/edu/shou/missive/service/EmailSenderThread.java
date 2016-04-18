package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shou on 2015/1/30.
 */
@Component
public class EmailSenderThread extends Thread {
    private ActivitiService actser;
    private String type;
    private boolean isSend;
    private String emailAddr;
    private String missiveTitle;
    private String taskID;
    private String processID;
    public EmailSenderThread(String type,boolean isSend,String emailAddr,String missiveTitle,String taskID,String processID,ActivitiService actser) {
        this.type = type;
        this.isSend=isSend;
        this.emailAddr=emailAddr;
        this.missiveTitle = missiveTitle;
        this.taskID = taskID;
        this.processID = processID;
        this.actser = actser;
    }
    public EmailSenderThread(){
        super();
    }

    public void run() {
        actser.emailSender(type, isSend,emailAddr, missiveTitle, taskID, processID);//发送邮件
    }


}
