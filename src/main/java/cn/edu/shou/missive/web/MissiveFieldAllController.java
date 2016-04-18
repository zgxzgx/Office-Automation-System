package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.MissiveFieldAllRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/20.
 */

@RestController
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value="/missiveFieldAll")
public class MissiveFieldAllController {

    @Autowired
    private MissiveFieldAllRepository mfar;

    private final static Logger logger = LoggerFactory.getLogger(MissiveFieldAllController.class);


    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response) {


        List<MissiveFieldAll> mfa = mfar.findAll();

        String MissiveFieldAllList = "[";

        for (MissiveFieldAll mm : mfa) {

            String fieldName = mm.getFieldName();
            String name=mm.getProcessType().getName();

            MissiveFieldAllList += "{fieldName:'" + fieldName + "',name:'"+name+"'},";
        }

        MissiveFieldAllList = MissiveFieldAllList.substring(0, MissiveFieldAllList.length() - 1) + "]";

        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write(MissiveFieldAllList);
        } catch (Exception ee) {
            logger.error("getMissiveFields exception:",ee);

            // throw ee;
            ee.printStackTrace();
        }

    }

  }
