package cn.edu.shou.missive.web.api;


import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Created by jiliwei on 2014/9/5.
 */
@RestController
@RequestMapping(value = "/process/history")
public class HistoryMissiveController {

    @Autowired
    ActivitiService activitiService;
    @Autowired
    private UserDAO ud;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;



    @RequestMapping(value = "/instance",method = RequestMethod.POST)
    public List<HistoricActivityInstance> getHistoryInstance(@RequestParam("instanceId") Long instanceId){

        List<HistoricActivityInstance> historyResult=this.activitiService.getHistoryByProcessId(instanceId);


        return historyResult;
    }

    @RequestMapping(value = "/documentation",method = RequestMethod.POST)
    public String getProcessDocumentation(@RequestParam("instanceId")Long instanceId){
        String Title="";
        String processDefId="";


        String documentation=this.activitiService.getDocumentationByProcessId(instanceId);
//        ProcessInstance instance = this.runtimeService.createProcessInstanceQuery().processInstanceId(instanceId.toString()).singleResult();
//        processDefId = instance.getProcessDefinitionId();
//        if(processDefId.contains("ReceiptId")){
//            Title= mrscr.getMissData(String.valueOf(instanceId)).getTitle();
//        }
//        else if(processDefId.contains("PublishMissiveId")){
//            Title=mpr.findByProcessID(instanceId).getMissiveTittle();
//        }
//        else if(processDefId.contains("SignId")){
//            Title = msr.findByProcessID(instanceId).getMissiveTittle();
//        }
//        Title= documentation+"     \r\n公文标题："+Title;


        return documentation;
    }

    @RequestMapping(value="/getNamebyUserName/{userName}")
    public String getNameByUserName(@PathVariable String userName){
        User user= ud.findByUserName(userName);
         String name;
        if(user!=null){
        name=user.getName();
        return  name;
        }else return "";
    }
}
