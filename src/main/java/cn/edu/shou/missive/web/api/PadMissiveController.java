package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.Group;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.ActivitiService;
import cn.edu.shou.missive.service.GroupRepository;

import cn.edu.shou.missive.service.UserDAO;
import cn.edu.shou.missive.service.UserRepository;
import org.activiti.bpmn.model.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sqhe on 14-8-10.
 */
@RestController
@RequestMapping(value = "/api/pad")
public class PadMissiveController {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ActivitiService activitiService;


    @RequestMapping(value="/{userid}/currentmissive", method= RequestMethod.GET)
    public List<Map<String,Object>> getCurrentMissive(@PathVariable("userid") Long userid){
        List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
        List<Task> currentTaskList = this.taskService.createTaskQuery().active().taskAssignee(userDao.findUserNameById(userid)).list();
        for (Task t:currentTaskList)
        {
            if(this.activitiService.isPadTask(t.getProcessDefinitionId(),t.getId())) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("taskId", Integer.parseInt(t.getId()));
                m.put("taskName", t.getName());
                m.put("fileUrl", "/pdf/");
                m.put("buttons", this.activitiService.getNextTaskInfo(t.getProcessInstanceId(), Integer.parseInt(t.getId())));
                result.add(m);
            }
        }
        return result;
    }



    @RequestMapping(value="/task/complete/{taskid}", method= RequestMethod.POST)
    @ResponseBody
    public String completeTask(@PathVariable("taskid") Long taskid){
        this.activitiService.completeTask(taskid,null);
        return "task completed";
    }



    @RequestMapping(value="/login", method= RequestMethod.POST)
    public User getCurrentMissive222(String username,String password){
        return this.userDao.findByUserNameAndPassword(username,password);

    }

    @RequestMapping(value="/{processid}/{userid}", method= RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public String getProcessTask(@PathVariable("processid") Long processid,@PathVariable("userid") Long userid){
        List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processid.toString()).singleResult();
        Task currentTask = this.taskService.createTaskQuery().active().processInstanceId(processid.toString()).taskAssignee(userDao.findUserNameById(userid)).singleResult();
        BpmnModel model= this.repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        String currentTaskId = currentTask.getTaskDefinitionKey();
        UserTask element = (UserTask)model.getMainProcess().getFlowElement(currentTaskId);
        FlowElement element2 = null;
        if (element.getOutgoingFlows().size()>0)
        {
            element2 = model.getMainProcess().getFlowElement(element.getOutgoingFlows().get(0).getTargetRef());
            if (element2.getClass().equals(UserTask.class))
            {
                UserTask userTask = (UserTask)element2;
                result.add(new HashMap<String, Object>()) ;

            }else
            {
                ExclusiveGateway gateway = (ExclusiveGateway)element2;
                gateway.getOutgoingFlows();

            }

        }
        ExclusiveGateway gateway = (ExclusiveGateway)model.getMainProcess().getFlowElement("exclusivegateway8");

        return ((UserTask)element2).getAssignee();
    }
}
