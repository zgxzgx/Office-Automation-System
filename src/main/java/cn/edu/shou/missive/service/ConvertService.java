package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveVersion;
import cn.edu.shou.missive.domain.SendConfig;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.web.CommonFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by sqhe on 2015/2/8.
 */
@Component
public class ConvertService {

    private final static Logger logger = LoggerFactory.getLogger(ConvertService.class);
    @Autowired
    CommonFunction cmmF;
    @Autowired
    ActivitiService actService;

    @Autowired
    private SendConfigRepository sendService;

    @Async
    public Future<String> convertPDFAndSendMailSMS(String currentTaskName,MissiveVersion tempMissiveVersion,long fileVersionNum,String[] attachment_StringArray,long instanceID,Long taskID,String htmlpath,String missiveType,
                                         boolean isSend,String emailAddr,String missive_tittle,String nextTaskID,User user1,String currentName,ActivitiService actservice)
    {
        logger.info("start convertPDFAndSendMailSMS");

//        if(currentTaskName.equals("文印室套红排版打印")&&missiveType.equals("MissivePublish")){
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "文印室套红排版打印",String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//            cmmF.convertAtt2Pdf2(currentTaskName, "文印室套红排版打印", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//
//        if(currentTaskName.equals("处室拟稿")&&missiveType.equals("MissivePublish")){
////            ConvertPdfThread cpThread = new ConvertPdfThread(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish",cmmF);
////            cpThread.run();
//            cmmF.convertAtt2Pdf2(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissivePublish");
//        }
//
//        if(currentTaskName.equals("处室拟稿")&&missiveType.equals("MissiveSign")){
//            cmmF.convertAtt2Pdf2(currentTaskName, "处室拟稿", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "MissiveSign");
//
//        }
//        if(currentTaskName.equals("办公室登记")&&missiveType.equals("MissiveReceive")){
//            cmmF.convertAtt2Pdf2(currentTaskName, "办公室登记", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, "missiveReceive");
//
//
//        }

        cmmF.convertAtt2Pdf2(currentTaskName, "", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, missiveType);




        SendConfig flag=sendService.findByName("emailSend");  //03-04孙乐
        String sendFlag=flag.getValue();

        if(sendFlag.equals("true")) {
            actService.emailSender(missiveType, isSend, emailAddr, missive_tittle, String.valueOf(taskID), String.valueOf(instanceID));//发送邮件

        }
        SendConfig flag2=sendService.findByName("messageSend");  //03-04孙乐
        String messageFlag=flag2.getValue();
        if(messageFlag.equals("true")) {
            try {    //03-03注释by sl
                actService.msgSender(user1, missive_tittle, currentName);//发送短信提醒
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    //03-10孙乐注释

           /* if (this.actService.isProcessFinishedByTaskID(String.valueOf(taskID))) {
                fullsearchR.setSearchContent(missiveType, Long.toString(taskID), Long.toString(instanceID));
            }*/

        logger.info("end convertPDFAndSendMailSMS");


        //boolean isFishedProcess=isProcessFinished(Long.toString(processID));
        //isFishedProcess=true;//调试全文索引使用，后面一定要去掉


       return new AsyncResult<String>("ok");

    }


    @Async
    public Future<String> convertPDF
            (String currentTaskName,MissiveVersion tempMissiveVersion,long fileVersionNum,String[] attachment_StringArray,long instanceID,Long taskID,String htmlpath,String missiveType
                                                   )
    {
        logger.info("start convertPDF");
        cmmF.convertAtt2Pdf2(currentTaskName, "", String.valueOf(tempMissiveVersion.getId()), fileVersionNum, attachment_StringArray, Long.parseLong(String.valueOf(instanceID)), Long.parseLong(taskID.toString()), htmlpath, missiveType);
        logger.info("end convertPDF");






        return new AsyncResult<String>("ok");

    }
}
