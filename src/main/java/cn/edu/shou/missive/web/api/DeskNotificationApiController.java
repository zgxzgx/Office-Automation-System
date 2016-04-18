package cn.edu.shou.missive.web.api;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.ActivitiService;
import cn.edu.shou.missive.service.Notification_UserRepository;
import cn.edu.shou.missive.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sqhe18 on 15-4-8.
 */


@RestController
@RequestMapping(value = "/api/desknotification")
public class DeskNotificationApiController {
    @Value("${spring.main.deskNotificationDataScanTime}")
    String DataScanTime;
    @Value("${spring.main.deskNotificationReNotificationTime}")
    String ReNotificationTime;
    @Autowired
    private UserRepository userR;

    @Autowired
    private ActivitiService actService;

    @Autowired
    private Notification_UserRepository notifi_u_R;

    @RequestMapping(value="/getnotifications/{userName}", method= RequestMethod.GET)
//    public List<Map<String,Long>> getAllgroup(@PathParam("userName") String userName){
    public Map<String,Long> getNotifications(@PathVariable String userName){
//        List<Map<String,Long>> notifications=new ArrayList<Map<String, Long>>();
        Map<String,Long> notifications=new HashMap<String, Long>();
        User user=userR.findByUserName(userName);
        if(user!=null){
            Long taskNum=actService.getCurrentTasksByUser(userName);
//            if(taskNum!=0){
//                Map<String,Long> tasks=new HashMap<String, Long>();
                notifications.put("taskNum",taskNum);
//            }
            Long unReadCount=notifi_u_R.getUnReadCount(user.getId());
            notifications.put("notiNum",unReadCount);
        }


        return notifications;
    }

    @RequestMapping(value="/getnotifications/config/getConfigInfo", method= RequestMethod.GET)
    public Map<String,Long> getNotificationsInfo(){
        Map<String,Long> notificationInfo=new HashMap<String, Long>();
        Long dataScanTime=Long.parseLong(DataScanTime);
        notificationInfo.put("dataScanTime",dataScanTime);
        Long reNotificationTime=Long.parseLong(ReNotificationTime);
        notificationInfo.put("reNotificationTime",reNotificationTime);
        return notificationInfo;
    }

}
