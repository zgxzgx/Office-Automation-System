package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.domain.TaskName;
import cn.edu.shou.missive.service.TaskNameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/19.
 */
@RestController
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value="/taskname")
public class TaskNameController {

    @Autowired
    private TaskNameRepository tnr;

    private final static Logger logger = LoggerFactory.getLogger(TaskNameController.class);


    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response){


        List<TaskName> tn=tnr.findAll();

        String taskNameList="[";

        for(TaskName tt:tn){

            String taskName=tt.getTaskName();

            String name=tt.getProcessType().getName();

            taskNameList+="{taskName:'"+taskName+"',name:'"+name+"'},";

        }

        taskNameList= taskNameList.substring(0,taskNameList.length()-1)+"]";

        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(taskNameList);
        }catch (Exception ee){
            logger.error("getMissiveFields exception:",ee);

            // throw ee;
            ee.printStackTrace();
        }

    }

}
