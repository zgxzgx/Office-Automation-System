package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.ActivitiService;
import cn.edu.shou.missive.service.UserRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by seky on 14-8-24.
 */
@Controller
@RequestMapping(value = "/ActivitiService")
@SessionAttributes(value = {"userbase64","user_id","user"})
public class ActivitiController {

    @Autowired
    ActivitiService actR;
    @Autowired
    UserRepository usrR;

    @RequestMapping(value = "/startProcess",method = RequestMethod.POST)
    public String startProcess(){
        String porcessDefId="FaxId:1:1327";
        String processID="";
        Long businessId=Long.parseLong("12");
        User usr=usrR.findOne(Long.parseLong("4"));
        processID=actR.startProcess(porcessDefId,businessId,usr);

        return processID;
    }
    @RequestMapping(value = "/getNextTaskInfo/{taskID}/{processInstanceID}",method = RequestMethod.POST)
    //public List<Map<String,? extends Object>> getNextTaskInfo(@PathVariable("taskID")int taskID,@PathVariable("processInstanceID")Long processInstanceID){
    public List<Map<String,? extends Object>> getNextTaskInfo(int taskID,Long processInstanceID){
        List<Map<String,? extends Object>> info=null;
        info=actR.getNextTaskInfo(processInstanceID.toString(),taskID);
        return info;
    }
//    @RequestMapping(value = "/complete/{taskID}",method = RequestMethod.POST)
//    public String completeTask(@PathVariable Long taskID)
    public String completeTask( Long taskID,String assineeVal,String conditionVal,String nextUser)
    {
        Map<String,Object> vars = new HashMap<String, Object>();

        vars.put(assineeVal,nextUser);
        vars.put("IsPass",conditionVal);
        this.actR.completeTask(taskID, vars);
        return "success!!";

    }
    public String completeTask( Long taskID,String assineeVal,String conditionVal,String nextUser,String type,Long processID)
    {
        Map<String,Object> vars = new HashMap<String, Object>();

        vars.put(assineeVal,nextUser);
        vars.put("IsPass",conditionVal);
        this.actR.completeTask(taskID, vars,type,processID);
        return "success!!";
    }
    public String completeTask( Long taskID,String multiAssineeVal,String conditionVal,Object nextUserList,String type,Long processID)
    {
        Map<String,Object> vars = new HashMap<String, Object>();
      //  List<String>;

      //  vars.put(assineeVal,nextUser);
        vars.put("IsPass",conditionVal);
        this.actR.completeTask(taskID, vars,type,processID);
        return "success!!";
    }



}
