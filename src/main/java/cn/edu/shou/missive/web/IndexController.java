package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.api.MyMissiveController;
//import com.sun.javafx.tk.Toolkit;
import jodd.util.Base64;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqhe on 14-7-7.
 */
@Controller
@RequestMapping(value="")
@SessionAttributes(value = {"userbase64","user"})
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);


    @Autowired
    private AsyncTask asynctask;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private MyMissiveController mmc;

    @ModelAttribute(value = "userbase64")
    public String addUserBase64toPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()== org.springframework.security.core.userdetails.User.class )
        {
            User myUser = (User) temp;
            String base64Code = "Basic " + Base64.encodeToString(myUser.getUsername() + ":" + myUser.getUsername());
            return base64Code;
        }
        else
            return "";

    }

//    @ModelAttribute(value = "user")
//    public User addUsertoPage()
//    {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
//        if (temp!=null && temp.getClass()==User.class )
//        {
//            return (User)temp;
//        }
//        else
//            return null;
//
//    }


    @RequestMapping(value="/index.html")
    public @ResponseBody String getIndex() {
        try {
            asynctask.dosomething();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @RequestMapping(value="")
    public String getDefaultIndex() {
        try {
            asynctask.dosomething();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "index";
    }


    @Autowired
    private NotificationRepository nrDAO;

    @RequestMapping(value="/notification")
    public String getNotification() {

        return "notification";
    }

    @RequestMapping(value = "/groupTree")
    public String getGroupTree(Model model)
    {
        return "groupTree2";
    }


    @RequestMapping(value="/fullSearch/{searchValue}")
    public String getFullSearch(@PathVariable("searchValue")String searchValue,Model model) {

        model.addAttribute("searchValue", searchValue);
        return "FullSearch";
    }

    @RequestMapping(value="/historyProcess/{instanceId}")
    public String getHistoryProcess(@PathVariable("instanceId") Long instanceId,Model model) {
        String Title="";
        String processDefId="";
        model.addAttribute("instanceId",instanceId);

        ProcessInstance instance = this.runtimeService.createProcessInstanceQuery().processInstanceId(instanceId.toString()).singleResult();
        HistoricProcessInstance hiinstance= this.historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId.toString()).singleResult();
        if(instance!=null){
            processDefId = instance.getProcessDefinitionId();
       }else processDefId=hiinstance.getProcessDefinitionId();

        if(processDefId.contains("ReceiptId")){
            Title= mrscr.getMissData(String.valueOf(instanceId)).getTitle();
        }
        else if(processDefId.contains("PublishMissiveId")){
            Title=mpr.findByProcessID(instanceId).getMissiveTittle();
        }


        model.addAttribute("titleName",Title);

        return "HistoryProcess";
    }

    @RequestMapping(value = "/config")
    public String getMissiveType(){
        return "config";
    }



}
