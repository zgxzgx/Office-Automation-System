package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.ACT_RU_TASK;
import cn.edu.shou.missive.domain.SendConfig;
import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by seky on 14/12/5.
 */
@EnableScheduling
public class ScheduledTasks {
    @Autowired
    private ActivitiService actS;
    @Autowired
    private ACT_RU_TASK_Repository actRunService;
    @Autowired
    private SendConfigRepository sendService;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    ActivitiService actService;
    @Autowired
    UserRepository userService;

    @Autowired
    PdfService pdfService;



//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /*
    CRON表达式    含义
    "0 0 12 * * ?"    每天中午十二点触发
    "0 15 10 ? * *"    每天早上10：15触发
    "0 15 10 * * ?"    每天早上10：15触发
    "0 15 10 * * ? *"    每天早上10：15触发
    "0 15 10 * * ? 2005"    2005年的每天早上10：15触发
    "0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发
    "0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发
    "0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发
    "0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发
    "0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发
    "0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发 */


    static final String scanTime="0 40 8 * * ?";//设定的为每天早上9点32触发

    @Scheduled(cron =scanTime)
    public void dealCurrentAllTasksList() throws IOException {
//        String A="E:\\esicmissive_springboot0203\\esicmissive_springboot0203\\upload\\faxCablePublish\\77501\\1\\html2pdf\\77506.pdf";
//        String B="E:\\esicmissive_springboot0203\\esicmissive_springboot0203\\upload\\faxCablePublish\\77501\\1\\mingzhu.pdf";
//
//        this.pdfService.overLayerRedTitle(A,B);


        List<ACT_RU_TASK> actRuTasks = new ArrayList<ACT_RU_TASK>();
        actRuTasks=  actRunService.findAll();

        List<Map<String,Object>> taskAssignee= jdbc.queryForList("SELECT * " +
                "FROM act_ru_task AS A " +
                "WHERE CREATE_TIME_=(SELECT MAX(CREATE_TIME_) FROM act_ru_task WHERE ASSIGNEE_=A.ASSIGNEE_)  ");
        List<Object> newtaskAssignees=new ArrayList<Object>();
        // taskAssignee.get(i).get("missive_tittle").toString();
        if(taskAssignee!=null&&taskAssignee.size()!=0) {
            for (int i = 0; i < taskAssignee.size(); i++) {
                Date dNow = new Date();   //当前时间
                Date dBefore = new Date();
                Calendar calendar = Calendar.getInstance(); //得到日历
                calendar.setTime(dNow);//把当前时间赋给日历
                calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                dBefore = calendar.getTime();   //得到前一天的时间
                String defaultStartDate = dateFormat.format(dBefore);    //格式化前一天
                String Name=taskAssignee.get(i).get("ASSIGNEE_").toString();
                int totalTaskCount = jdbc.queryForInt("SELECT count(*) " +
                        "FROM act_ru_task AS A " +
                        "WHERE A.ASSIGNEE_= '"+Name +"' and A.CREATE_TIME_<'"+defaultStartDate+"'");
                User user= userService.findByUserName(Name);
                SendConfig flag2=sendService.findByName("messageSend");  //03-04孙乐
                String messageFlag=flag2.getValue();
                //if(messageFlag.equals("true")) {
                    try {    //03-03注释by sl
                        actService.msgSenderbyCount(user, Integer.toString(totalTaskCount));//发送短信提醒
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //}
            }
         }
       // fullSearchService.setSearchContent("1","1","1", null);




//        System.out.println("The time is start " + dateFormat.format(new Date()));
//        actS.dealCurrentAllTasksList();
//        System.out.println("The time is end " + dateFormat.format(new Date()));
    }

}
