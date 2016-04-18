package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.MissiveField;
import cn.edu.shou.missive.service.MissiveFieldRepository;
import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.service.ProcessTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/18.
 */
@RestController
@RequestMapping(value="/processType")
@SessionAttributes(value = {"userbase64","user_id","user"})
public class ProcessTypeController {
    @Autowired
    private ProcessTypeRepository pr;

    private final static Logger logger = LoggerFactory.getLogger(ProcessTypeController.class);


    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response){


        List<ProcessType> ptype=pr.findAll();

        String ProcessTypeList="[";

        for(ProcessType pp:ptype){

            String name=pp.getName();

            ProcessTypeList+="{name:'"+name+"'},";
        }

        ProcessTypeList= ProcessTypeList.substring(0,ProcessTypeList.length()-1)+"]";

        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(ProcessTypeList);
        }catch (Exception ee){
            logger.error("getMissiveFields exception:",ee);

            // throw ee;
            ee.printStackTrace();
        }

    }

}
