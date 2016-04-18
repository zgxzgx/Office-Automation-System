package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.Authorities;
import cn.edu.shou.missive.domain.MissivePublishFunction;
import cn.edu.shou.missive.domain.PageableHistoryTaskList;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.ActivitiService;
import cn.edu.shou.missive.service.AuthoritiesRepository;
import cn.edu.shou.missive.service.UserDAO;
import cn.edu.shou.missive.web.api.MyMissiveController;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zgx on 2016/1/20.
 */
@RequestMapping(value="")
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class DepMissiveController {
    @Autowired
    private UserDAO ud;
    @Autowired
    private MyMissiveController mmc;
    @Autowired
    private AuthoritiesRepository authorService;
    @Autowired
    private ActivitiService actService;
    MissivePublishFunction mpf=new MissivePublishFunction();
    //处室任务-进行中任务
    @RequestMapping(value="/csrw/{pageNum}")
    public String html2csrw(Model model,@AuthenticationPrincipal User currentUser,@PathVariable Integer pageNum){

        String authorflag="false";
        User usr = this.ud.findById(currentUser.getId());
        UserFrom userFrom=mpf.userToUserForm(usr);
        model.addAttribute("ud", userFrom);

        if(pageNum==null||pageNum<1){
            pageNum=1;
        }

        //进行中任务
        List<TaskForm> ltfIng=mmc.getTaskFormByTaskList(currentUser);
        List<Authorities> authorities=authorService.getAuthoritiesListByuserID(currentUser.getId());
        if(authorities!=null){
            authorflag="true";
        }

        //已完结任务



        //int taskIngPagesNum=result.getPageTotal()==0?1:result.getPageTotal();
        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",3);
        model.addAttribute("LookPage",pageNum);
        model.addAttribute("AuthorityFlag",authorflag);

        return "depMissive";
    }

    @RequestMapping(value="/csgw/{itemActive}/{pageNum}/{pageNum1}")
    public String html2csgw(Model model,@AuthenticationPrincipal User currentUser,@PathVariable String itemActive,@PathVariable Integer pageNum,@PathVariable Integer pageNum1){

        model.addAttribute("ud", currentUser);

        double pageTotal=1;
        if(pageNum==null){
            pageNum=1;
        }
        if (pageNum1== null)
        {
            pageNum1 = 1;
        }
        int taskDonePagesNum=1;
        int taskIngPagesNum=1;


        //进行中任务
        List<TaskForm> ltfIng=mmc.getTaskFormByTaskList(currentUser);

        //PageableHistoryTaskList unfinishedTask =this.actService.getUncompletedTasksByUser(currentUser,13,pageNum);//未办任务
        //taskIngPagesNum=unfinishedTask.getPageTotal()==0?1:unfinishedTask.getPageTotal();

        PageableHistoryTaskList historyTasks = this.actService.getAllDepProcessDone(currentUser.getGroup().getId(), 13, pageNum1);//完成的任务
        List<TaskForm> lftDone=new ArrayList<TaskForm>();
        if (historyTasks.getTasklist() != null) {
            lftDone = mmc.getTaskFormByHistroyTask(historyTasks.getTasklist());
        }

        pageTotal=historyTasks.getPageTotal();



        taskDonePagesNum=historyTasks.getPageTotal()==0?1:historyTasks.getPageTotal();


//        String jumpReason="";
//        List resultList=this.jdbcTemplate.queryForList("SELECT a.ACTION_ FROM oa3.act_hi_comment as a where a.ACTION_ !='备注' and a.ACTION_ !='rollback'");
//
//        Iterator it = resultList.iterator();
//        while(it.hasNext()) {
//            Map userMap = (Map) it.next();
//
//            jumpReason+=userMap.get("NAME_").toString()+","+currentUser+":"+userMap.get("MESSAGE_").toString()+"\n";
//
//        }
//        model.addAttribute("jumpReason", jumpReason);


        model.addAttribute("tasksIng", ltfIng);
        model.addAttribute("taskIngTotalNum",3);
        model.addAttribute("LookPage",pageNum1);
        model.addAttribute("activeitem",itemActive);
        model.addAttribute("tasksDone",lftDone);
        model.addAttribute("DonePage",pageNum);
        model.addAttribute("PageTotal",pageTotal);
        //model.addAttribute("lastTaskId", ltfIng);


        model.addAttribute("taskDoneTotalNum",taskDonePagesNum);

        return "depMissive";
    }







}
