package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by XQQ on 2014/9/7.
 */
@RestController
@RequestMapping(value = "/sendControl")
@SessionAttributes(value = {"userbase64","user_id","user"})
public class SendConfigController {
    @Autowired
    MissiveTypeRepository mt;
    @Autowired
    SecretLevelRepository sl;
    @Autowired
    UrgentLevelRepository ul;
    @Autowired
    DeployRepository de;
    @Autowired
    UserConfigRepository ucr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SendConfigRepository sendService;

    @Autowired
    private JdbcTemplate jdbc;






    @RequestMapping(value="/changeMessageFlag")
    public String changeMessageFlag(@AuthenticationPrincipal User currentUser,@RequestParam String  isTop,@RequestParam String data){

        SendConfig flag = sendService.findByName(data);
        flag.setValue(isTop);
        sendService.save(flag);


        return "true";

    }
    @RequestMapping(value="/changeEmailFlag")
    public String changeEmailFlag(@AuthenticationPrincipal User currentUser,@RequestParam String  isTop,@RequestParam String data){

        SendConfig flag = sendService.findByName(data);
        flag.setValue(isTop);
        sendService.save(flag);


        return "true";

    }



}
