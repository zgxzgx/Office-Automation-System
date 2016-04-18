package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.Schedule;
import cn.edu.shou.missive.domain.UploadFlag;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-8-14.
 */
@RequestMapping(value = "/process")
@RestController
@SessionAttributes(value = {"userbase64","user_id","user"})
public class ProcessController {

    @Autowired
    TaskDAO taskDao;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    ActivitiService activitiService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UploadFlagRepository uploadFlagRepository;


    @RequestMapping(value = "/{processInstanceId}/{taskId}")
    public String getTask(Long processInstanceId,Long taskId)
    {

        List<String> editableFields = taskDao.getCurrentEditableFieldsByTaskId(taskId);
        String taskName = taskDao.getTaskNameById(taskId);


        return null;
    }


    @RequestMapping(value = "/new")
    public String startProcess()
    {

        return null;
    }

    @RequestMapping(value = "/updateFile", method = RequestMethod.POST)
    public String setPassWord2(Model model, @RequestParam Long taskid, @RequestParam Long instantid, @AuthenticationPrincipal User currentUser) {

        UploadFlag schedule1=uploadFlagRepository.findByContent(taskid.toString());
        //List<String> assignStr= activitiService.getProcessUsersByProcessID(instantid.toString());//根据用户，获取到用所参与的所有任务

        if(schedule1==null){
            //  for(String uu:assignStr) {
            UploadFlag uploadFlag = new UploadFlag();
            //schedule1.setId(instantid.toString());
            uploadFlag.setInstanceId(instantid.toString());
            uploadFlag.setIsDel("1");
            uploadFlag.setCreateTime(new Date());
            uploadFlag.setContent(taskid.toString());
            uploadFlag.setUser(currentUser.getUserName());
            uploadFlagRepository.save(uploadFlag);
            // }
        }




        return null;
    }



}
