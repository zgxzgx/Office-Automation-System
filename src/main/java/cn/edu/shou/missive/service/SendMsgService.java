package cn.edu.shou.missive.service;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 短信发送服务类
 */
@Component
public class SendMsgService {

    private final static Logger logger = LoggerFactory.getLogger(SendMsgService.class);
    @Value("${spring.main.sendSmsType}")
    String smsSendType;
    @Value("${spring.main.convertTool}")
    String baseURL;
    @Value("${spring.main.smsTool}")
    String smsToolpath;
    @Value("${spring.main.comNum}")
    String comNum;
    String MESSAGE_TEMPLATE = "msgTemplate";//获取短息模板名称
    String missiveTitleTem="$missiveTitle$";
    String lastDealerTem = "$lastTasker$";
    String sendTimeTem = "$nowTime$";
    @Autowired
    private DeployRepository deR;
    @Autowired
    private SendConfigRepository sendService;


    public String sendMsg(String smsMobNum, String missiveTitle,String lastTasker)
    {
        String flag = this.smsSendType.toLowerCase();
        String result = "";
        if(flag.equals("net"))
        {
            try {
                result = sendSMSByNet(smsMobNum,missiveTitle,lastTasker);
                logger.info("send sms by net | to"+smsMobNum+" | "+missiveTitle+" | "+lastTasker);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }else if(flag.equals("modem"))
        {
            try {
                sendSMSByCDMAModem(smsMobNum, missiveTitle, lastTasker);
                logger.info("send sms by CDMA modem | to"+smsMobNum+" | "+missiveTitle+" | "+lastTasker);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }else
        {
            logger.error("wrong smsSendType "+this.smsSendType+" must be net or modem;");
        }

        return result;
    }
    public String sendMsgbyCount(String smsMobNum, String missiveCount)
    {
        String flag = this.smsSendType.toLowerCase();
        String result = "";
        if(flag.equals("net"))
        {
            try {
                result = sendSMSByNet2(smsMobNum,missiveCount);
                logger.info("send sms by net | to"+smsMobNum+" | "+missiveCount+" | "+missiveCount);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }else if(flag.equals("modem"))
        {
            try {
                sendSMSByCDMAModem2(smsMobNum, missiveCount);
                logger.info("send sms by CDMA modem | to"+smsMobNum+" | "+missiveCount+" | "+missiveCount);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }else
        {
            logger.error("wrong smsSendType "+this.smsSendType+" must be net or modem;");
        }

        return result;
    }

    public String sendSMSByNet(String smsMobNum, String missiveTitle,String lastTasker) throws IOException {
        String uidStr = "seky";//网站注册帐号用户名
        String keyStr = "142a3feed90737ada207";//接口安全密码
        Date sendtime = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String msgSendTime = sFormat.format(sendtime);

        //String smsMobNum="15692166810";//手机号码，多个号码使用逗号隔开
        String smsTextStr = deR.getEmailTemplate(MESSAGE_TEMPLATE);//获取短信模板内容
        if(smsTextStr.contains(missiveTitleTem)) {
            smsTextStr = smsTextStr.replace(missiveTitleTem, missiveTitle);
        }
        if(smsTextStr.contains(lastDealerTem)) {
            smsTextStr = smsTextStr.replace(lastDealerTem, lastTasker);
        }
        if(smsTextStr.contains(sendTimeTem)) {
            smsTextStr = smsTextStr.replace(sendTimeTem, msgSendTime);
        }
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
        NameValuePair[] data = {new NameValuePair("Uid", uidStr), new NameValuePair("Key", keyStr), new NameValuePair("smsMob", smsMobNum), new NameValuePair("smsText", smsTextStr)};
        post.setRequestBody(data);
        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:" + statusCode);
        for (Header h : headers) {
            System.out.println(h.toString());
        }
        String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
        System.out.println(result); //打印返回消息状态


        post.releaseConnection();

        return result;
    }

    public String sendSMSByNet2(String smsMobNum, String missiveTitle) throws IOException {
        String uidStr = "seky";//网站注册帐号用户名
        String keyStr = "142a3feed90737ada207";//接口安全密码
        Date sendtime = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String msgSendTime = sFormat.format(sendtime);

        //String smsMobNum="15692166810";//手机号码，多个号码使用逗号隔开
        String smsTextStr = deR.getEmailTemplate("msgTemplate2");//获取短信模板内容
        if(smsTextStr.contains(missiveTitleTem)) {
            smsTextStr = smsTextStr.replace(missiveTitleTem, missiveTitle);
        }

        if(smsTextStr.contains(sendTimeTem)) {
            smsTextStr = smsTextStr.replace(sendTimeTem, msgSendTime);
        }
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
        NameValuePair[] data = {new NameValuePair("Uid", uidStr), new NameValuePair("Key", keyStr), new NameValuePair("smsMob", smsMobNum), new NameValuePair("smsText", smsTextStr)};
        post.setRequestBody(data);
        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:" + statusCode);
        for (Header h : headers) {
            System.out.println(h.toString());
        }
        String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
        System.out.println(result); //打印返回消息状态


        post.releaseConnection();

        return result;
    }

    /**
     * @param smsMobNum
     * @param missiveTitle
     * @return
     * @throws java.io.IOException
     */
    public void sendSMSByCDMAModem(String smsMobNum, String missiveTitle,String lastTasker) throws IOException {
        Date sendtime = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String msgSendTime = sFormat.format(sendtime);

        String smsTextString = deR.getEmailTemplate(MESSAGE_TEMPLATE);//获取短信模板内容
        if(smsTextString.contains(missiveTitleTem)) {
            smsTextString = smsTextString.replace(missiveTitleTem, missiveTitle);
        }
        if(smsTextString.contains(lastDealerTem)) {
            smsTextString = smsTextString.replace(lastDealerTem, lastTasker);
        }
        if(smsTextString.contains(sendTimeTem)) {
            smsTextString = smsTextString.replace(sendTimeTem, msgSendTime);
        }

        String command =  smsToolpath +" "+ comNum + " " + smsMobNum + " \"" + smsTextString + "\"";
        this.executeCommands(command);
        logger.info("send sms to " + smsMobNum + "  " + missiveTitle);
    }
    public void sendSMSByCDMAModem2(String smsMobNum, String missiveTitle) throws IOException {
        Date sendtime = new Date();
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String msgSendTime = sFormat.format(sendtime);

        String smsTextString = deR.getEmailTemplate("msgTemplate2");//获取短信模板内容
        if(smsTextString.contains(missiveTitleTem)) {
            smsTextString = smsTextString.replace(missiveTitleTem, missiveTitle);
        }

        if(smsTextString.contains(sendTimeTem)) {
            smsTextString = smsTextString.replace(sendTimeTem, msgSendTime);
        }

        String command =  smsToolpath +" "+ comNum + " " + smsMobNum + " \"" + smsTextString + "\"";
        this.executeCommands(command);
        logger.info("send sms to " + smsMobNum + "  " + missiveTitle);
    }

    private void executeCommands(String command) {

        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            logger.error("executeCommands exception:", e);
            e.printStackTrace();
        }
    }
}
