package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.MissiveFieldRepository;
import cn.edu.shou.missive.service.ProcessTypeRepository;
import cn.edu.shou.missive.service.TaskNameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/13.
 */
@RestController
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value="/missivefield")
public class MissiveFieldController {

    @Autowired
    private MissiveFieldRepository mfr;
    @Autowired
    private ProcessTypeRepository ptr;
    @Autowired
    private TaskNameRepository tnr;

    private final static Logger logger = LoggerFactory.getLogger(MissiveFieldController.class);


//    @RequestMapping(value = "", method = RequestMethod.GET)
//    public Page<MissiveFeld> getMissiveFields(@RequestParam int page, @RequestParam int size) {
//        return mfr.findAll(new PageRequest(page, size));
//    }

    @RequestMapping(value="/read",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveField(HttpServletResponse response){

       List<MissiveField> mf= mfr.findByIsDel("0");

        String MissiveFieldList="[";

        for(MissiveField mm:mf){

            Long id=mm.getId();
            String fieldName = mm.getFieldName();
            String inputId = mm.getInputId();
            String inputType = mm.getInputType();
            String isDel = mm.getIsDel();

            String name=mm.getProcessType().getName();
            String taskName=mm.getTaskName().getTaskName();

            MissiveFieldList+="{id:"+id+",fieldName:'"+fieldName+"',inputId:'"+inputId+"',inputType:'"+inputType+"',name:'"+name+"',taskName:'"+taskName+"',isDel:'"+isDel+"'},";

        }

        MissiveFieldList= MissiveFieldList.substring(0,MissiveFieldList.length()-1)+"]";

        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(MissiveFieldList);
        }catch (Exception ee){
            logger.error("getJsonDataByObject exception:",ee);


            // throw ee;
            ee.printStackTrace();
        }

    }

    @RequestMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void create(String fieldName, String name, String taskName)
    {

        MissiveField mfd=new MissiveField();

        mfd.setFieldName(fieldName);
        mfd.setIsDel("0");
        mfd.setInputType("");
        mfd.setInputId("");

        ProcessType pt=ptr.findByName(name);
        List<TaskName> tns=tnr.findByTaskNameAndProcessType(taskName,pt);
        mfd.setProcessType(pt);
        mfd.setTaskName(tns.get(0));

        //mfd.setProcessType(ptr.findByName(name));
       // mfd.setTaskName(tnr.findByTaskName(taskName));

        mfr.save(mfd);

    }

    @RequestMapping(value = "/update")
    @ResponseStatus(HttpStatus.OK)
    public void update(Long id,String fieldName, String name, String taskName)
    {
        MissiveField mfd =mfr.findOne(id);

        mfd.setFieldName(fieldName);

        //Long processID=mfd.getProcessType().getId();
        ProcessType pt=ptr.findByName(name);
        List<TaskName> tns=tnr.findByTaskNameAndProcessType(taskName,pt);
        mfd.setProcessType(pt);
        mfd.setTaskName(tns.get(0));

        mfr.save(mfd);
    }

    @RequestMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public void delete(Long id)
    {
      //mfr.delete(id);
        MissiveField mfd= mfr.findOne(id);
        mfd.setIsDel("1");
        mfr.save(mfd);

    }

}
