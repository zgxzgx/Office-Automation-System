package cn.edu.shou.missive.web;


import cn.edu.shou.missive.domain.User;

import cn.edu.shou.missive.service.UserRepository;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zgx on 2015/7/1.
 */
@RequestMapping(value = "/analysis")
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class analysisController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value="/staff")
    public String staticAnalysis(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        List<Object> columnList=userName();
        model.addAttribute("dataSource",columnList);


        return "analysis";
    }

    public List<Object>  userName()
    {
        List<Object> nodeValue=new ArrayList<Object>();
        String userNameList="";
        List resList=this.jdbcTemplate.queryForList("SELECT distinct ASSIGNEE_ FROM oa3.act_hi_taskinst");

        Iterator it = resList.iterator();
        while(it.hasNext()) {
            Map userMap = (Map) it.next();
            if (userMap.get("ASSIGNEE_") != null) {

                String userName = userMap.get("ASSIGNEE_").toString();

                User user = userRepository.findByUserName(userName);

                userNameList += userMap.get("ASSIGNEE_").toString() + "\n";
                long ybrwNum = this.historyService.createNativeHistoricTaskInstanceQuery()
                        .sql("select * from oa3.act_hi_taskinst where ID_ in( SELECT max(ID_) FROM oa3.act_hi_taskinst where ASSIGNEE_='" + userMap.get("ASSIGNEE_").toString() + "'  and END_TIME_!='' and DELETE_REASON_='completed'  group by PROC_INST_ID_ ) and PROC_INST_ID_ NOT IN(  select distinct d.PROC_INST_ID_ from oa3.act_hi_actinst d where d.PROC_INST_ID_  not in(  SELECT  b.PROC_INST_ID_ from oa3.act_hi_actinst b where b.ACT_NAME_='流程结束') and d.PROC_INST_ID_ NOT IN ( SELECT distinct c.PROC_INST_ID_ FROM oa3.act_ru_task c)) order by END_TIME_ desc").list().size();
                int wbrwNum = this.taskService.createTaskQuery().active().taskAssignee(userMap.get("ASSIGNEE_").toString()).orderByTaskCreateTime().desc().list().size();
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("ybrwNum", ybrwNum);
                result.put("wbrwNum", wbrwNum);
                if (user != null) {
                    result.put("userName", user.getName());
                } else {
                    result.put("userName", "未指定");

                }
                nodeValue.add(result);
            }

        }
        return nodeValue;
      //  model.addAttribute("userNameList", userNameList);



    }



}

