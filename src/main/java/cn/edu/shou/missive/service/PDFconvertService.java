package cn.edu.shou.missive.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by shou on 2015/1/30.
 */

@Service
public class PDFconvertService {


    private ActivitiService actser;
    private String type;
    private boolean isSend;
    private String emailAddr;
    private String missiveTitle;
    private String taskID;
    private String processID;
    public PDFconvertService(String type, boolean isSend, String emailAddr, String missiveTitle, String taskID, String processID, ActivitiService actser) {
        this.type = type;
        this.isSend=isSend;
        this.emailAddr=emailAddr;
        this.missiveTitle = missiveTitle;
        this.taskID = taskID;
        this.processID = processID;
        this.actser = actser;
    }
    public PDFconvertService(){
        super();
    }

    /*public void run() {
        actser.emailSender(type, isSend,emailAddr, missiveTitle, taskID, processID);//发送邮件
    }*/


    @Async
    public void run() {
        try {
            actser.emailSender(type, isSend,emailAddr, missiveTitle, taskID, processID);//发送邮件
            Thread.sleep(2000);
            // do something
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("operation complete.");
    }
}
