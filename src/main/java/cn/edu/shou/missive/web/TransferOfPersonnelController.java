package cn.edu.shou.missive.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by shou on 2016/1/22.
 */
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value = "/userMag")
public class TransferOfPersonnelController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    //人事调动公文交接
    @RequestMapping(value = "/transferOfPer/{currentHolder}/{transferTo}")
    @ResponseBody
    public String updateMissiveHolder(Model model,@PathVariable("currentHolder") String currentHolder,@PathVariable("transferTo") String transferTo) {
        jdbcTemplate.update("update oa3.act_ru_task SET oa3.act_ru_task.ASSIGNEE_ = '"+ transferTo + "' where oa3.act_ru_task.ASSIGNEE_ = '"+currentHolder +"'");
        jdbcTemplate.update("update oa3.act_hi_taskinst SET oa3.act_hi_taskinst.ASSIGNEE_ = '"+ transferTo + "' where oa3.act_hi_taskinst.ASSIGNEE_ = '"+currentHolder+"'");
        return "success";
    }
}
