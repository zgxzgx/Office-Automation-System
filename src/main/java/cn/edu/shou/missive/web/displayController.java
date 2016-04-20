package cn.edu.shou.missive.web;


import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.CommentFrom;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.api.MyMissiveController;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zgx on 2015/7/1.
 */
@RequestMapping(value = "")
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class displayController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mpr;

    @Autowired
    private ActivitiService actService;
    @Autowired
    private MyMissiveController mmc;
    @Autowired
    private FileUploadController fileupc;




    public String getMissiveTitle(String processDeId,String instanceId){


        String missiveTitle="无标题";
        String  verType="";

        if(processDeId.contains("ReceiptId")){
            verType="missiveReceive";
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
            if(mrsc!=null&&mrsc.getTitle()!=null){
                missiveTitle=mrsc.getTitle();

            }

        }
        else if(processDeId.contains("PublishMissiveId")){
            verType="missivePublish";
            MissivePublish mp =mpr.findByProcessID(Long.parseLong(instanceId));
            if(mp!=null&&mp.getMissiveTittle()!=null){
                missiveTitle=mp.getMissiveTittle();
            }

        }

        int versionNum = fileupc.getMaxMissiveVersion(Long.parseLong(instanceId), verType);
        String parameter=missiveTitle+"|"+verType+"|"+String.valueOf(versionNum);


        return  parameter;


    }

    //zgx 01-09
    //根据流程类型和流程ID获取公文名称并设置公文类型
    public String getMissiveTitles(String fileType,String instanceId){

        String missiveTitle="无标题";
        String  missiveType="";

        if(fileType.contains("missiveReceive")){
            missiveType="收文流程";
            MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(instanceId));
            if(mrsc!=null&&mrsc.getTitle()!=null){
                missiveTitle=mrsc.getTitle();

            }

        }
        else if(fileType.contains("missivePublish")){
            missiveType="发文流程";
            MissivePublish mp =mpr.findByProcessID(Long.parseLong(instanceId));
            if(mp!=null&&mp.getMissiveTittle()!=null){
                missiveTitle=mp.getMissiveTittle();
            }

        }

        return  missiveTitle+"|"+missiveType;
    }

    public int getPageTotalNum(int totalPageForRollback){
        int taskSendPageNum=1;
        if (totalPageForRollback % 13 == 0) {
            taskSendPageNum=totalPageForRollback / 13;
        } else {
            taskSendPageNum= totalPageForRollback / 13 + 1;
        }

        taskSendPageNum=taskSendPageNum==0?1:taskSendPageNum;
        return  taskSendPageNum;
    }

    @RequestMapping(value="/displayInfo/{activeItem}/{pageNum1}/{pageNum2}/{pageNum3}")
    public String  InfoDisplayListViewer(Model model,@AuthenticationPrincipal User user,@PathVariable String activeItem,@PathVariable Integer pageNum1,@PathVariable Integer pageNum2,@PathVariable Integer pageNum3 )
    {
        double pageTotal=1;
        if (pageNum1== null)
        {
            pageNum1 = 1;
        }
        if(pageNum2==null){
            pageNum2=1;
        }
        if(pageNum3==null){
            pageNum3=1;
        }

        List<CommentFrom> rollbackList=new ArrayList<CommentFrom>();
        List<CommentFrom> deletedLsit=new ArrayList<CommentFrom>();
        List<CommentFrom> delegatorList=new ArrayList<CommentFrom>();
        rollbackList=getCommentList("rollback",pageNum1);
        deletedLsit=getCommentList("delete",pageNum2);
        delegatorList=getCommentList("delegator",pageNum3);
        int totalTaskForRollback=this.jdbcTemplate.queryForInt("SELECT count(*) FROM oa4.act_hi_comment where ACTION_='rollback'");
        int totalTaskFordeleted=getDeletedListNum();
        int totalTaskFordelegator=this.jdbcTemplate.queryForInt("SELECT count(*) FROM oa4.act_hi_comment where ACTION_='delegator'");
        int totalPageForRollback=getPageTotalNum(totalTaskForRollback);
       int totalPageFordeleted=getPageTotalNum(totalTaskFordeleted);
       int totalPageFordelegator=getPageTotalNum(totalTaskFordelegator);

        model.addAttribute("user",user);
        model.addAttribute("rollbackList",rollbackList);
        model.addAttribute("deletedLsit",deletedLsit);
        model.addAttribute("delegatorList",delegatorList);
        //model.addAttribute("pageTotal",pageTotal);
        model.addAttribute("activeitem",activeItem);
        model.addAttribute("pageNum1",pageNum1);
        model.addAttribute("pageNum2",pageNum2);
        model.addAttribute("pageNum3",pageNum3);
        model.addAttribute("totalPageForRollback",totalPageForRollback);
        model.addAttribute("totalPageFordeleted",totalPageFordeleted);
        model.addAttribute("totalPageFordelegator",totalPageFordelegator);
        model.addAttribute("totalTaskForRollback",totalTaskForRollback);
        model.addAttribute("totalTaskFordeleted",totalTaskFordeleted);
        model.addAttribute("totalTaskFordelegator",totalTaskFordelegator);





        return "InfoDisplay";
      //  model.addAttribute("userNameList", userNameList);



    }


    public List<CommentFrom> getCommentList(String Action,int currentPage){

        List<CommentFrom> rollbackList=new ArrayList<CommentFrom>();


        int NUM_PER_PAGE=13;


        String  processDeId="";
        String faxList="";
        String LastTaskId="";
        String instantId="";
        List faxResult=this.jdbcTemplate.queryForList("SELECT TIME_ ,TYPE_,ACTION_,USER_ID_,PROC_INST_ID_,MESSAGE_,TASK_ID_ FROM oa4.act_hi_comment  where ACTION_="+"'"+Action+"'"+" order by TIME_ desc limit ?,?",
                new Object[] { (currentPage * NUM_PER_PAGE - NUM_PER_PAGE), NUM_PER_PAGE });

        Iterator it = faxResult.iterator();
        while(it.hasNext()) {
            Map userMap = (Map) it.next();
            if (userMap.get("ACTION_")!= null) {
                 instantId=userMap.get("PROC_INST_ID_").toString();
                CommentFrom commentFrom=new CommentFrom();

                //String LastTaskId;
                List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceEndTime().desc().list();

                //return previousTasklist.get(0).getId();
                if(!previousTasklist.isEmpty()) {

                    LastTaskId=previousTasklist.get(0).getId();
                    commentFrom.setLastTaskId(LastTaskId);
                }


                String acction=userMap.get("ACTION_").toString();

                commentFrom.setAcction(acction);
                commentFrom.setInstantId(instantId);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String Time=format.format(userMap.get("TIME_"));
                commentFrom.setOperationTime(Time);
                String userName=userRepository.findByUserName(userMap.get("USER_ID_").toString()).getName();
                commentFrom.setOperator(userName);
                if(userMap.get("MESSAGE_")!=null){
                    commentFrom.setComment(userMap.get("MESSAGE_").toString());

                }else{
                    commentFrom.setComment("无评论");

                }
                if(userMap.get("TYPE_")!=null){
                    String[] MSG=userMap.get("TYPE_").toString().split("\\|");
                    if(MSG.length!=0){

                        commentFrom.setOrginalNode(MSG[1]);
                        commentFrom.setJumpTo(MSG[0]);
                    }else{
                        commentFrom.setOrginalNode("暂无信息");
                        commentFrom.setJumpTo("暂无信息");
                    }

                }else{
                    commentFrom.setOrginalNode("暂无信息");
                    commentFrom.setJumpTo("暂无信息");

                }



                String flag=userMap.get("ACTION_").toString();
                if(flag.equals("rollback")){
                    commentFrom.setOperatorName("任务回退");
                    String taskId=userMap.get("TASK_ID_").toString();
                    HistoricTaskInstance task =  this.historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                    if(task!=null){
                        commentFrom.setTaskName(task.getName());
                        processDeId=task.getProcessDefinitionId();

                        String[] MSG=getMissiveTitle(processDeId,instantId).split("\\|");
                        if(MSG.length!=0){

                            commentFrom.setMissiveTitle(MSG[0]);
                            commentFrom.setVerType(MSG[1]);
                            commentFrom.setVersionNum(MSG[2]);
                        }
//                        String missiveTitle=getMissiveTitle(processDeId,instantId).get(0);
//                        String verType=getMissiveTitle(processDeId,instantId).get(1);
//                        String versionNum=getMissiveTitle(processDeId,instantId).get(2);


                        rollbackList.add(commentFrom);

                    }

                }else if(flag.equals("delegator")){
                    commentFrom.setOperatorName("任务跳转");
                    List<HistoricTaskInstance> tasklist =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceEndTime().desc().list();
                    if(tasklist!=null){
                        commentFrom.setTaskName(tasklist.get(0).getName());

                        processDeId=tasklist.get(0).getProcessDefinitionId();
                        //String missiveTitle=getMissiveTitle(processDeId,instantId);
//                        String missiveTitle=getMissiveTitle(processDeId,instantId).get(0);
//                        String verType=getMissiveTitle(processDeId,instantId).get(1);
//                        String versionNum=getMissiveTitle(processDeId,instantId).get(2);
//
//                        commentFrom.setMissiveTitle(missiveTitle);
//                        commentFrom.setMissiveTitle(verType);
//                        commentFrom.setMissiveTitle(versionNum);
                        String[] MSG=getMissiveTitle(processDeId,instantId).split("\\|");
                        if(MSG.length!=0){

                            commentFrom.setMissiveTitle(MSG[0]);
                            commentFrom.setVerType(MSG[1]);
                            commentFrom.setVersionNum(MSG[2]);
                        }

                    }
                    rollbackList.add(commentFrom);
                }else if(flag.equals("delete")){
                    commentFrom.setOperatorName("任务删除");
                    List<HistoricTaskInstance> tasklist =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceEndTime().desc().list();
                    if(tasklist!=null){
                        commentFrom.setTaskName(tasklist.get(0).getName());

                        processDeId=tasklist.get(0).getProcessDefinitionId();
                        //String missiveTitle=getMissiveTitle(processDeId,instantId);
//                        String missiveTitle=getMissiveTitle(processDeId,instantId).get(0);
//                        String verType=getMissiveTitle(processDeId,instantId).get(1);
//                        String versionNum=getMissiveTitle(processDeId,instantId).get(2);
//
//                        commentFrom.setMissiveTitle(missiveTitle);
//                        commentFrom.setMissiveTitle(verType);
//                        commentFrom.setMissiveTitle(versionNum);
                        String[] MSG=getMissiveTitle(processDeId,instantId).split("\\|");
                        String missiveTitle=MSG[0];
                        if(MSG.length!=0){

                            commentFrom.setMissiveTitle(MSG[0]);
                            commentFrom.setVerType(MSG[1]);
                            commentFrom.setVersionNum(MSG[2]);
                        }
                        if(!missiveTitle.equals("无标题")){
                            commentFrom.setMissiveTitle(missiveTitle);
                            rollbackList.add(commentFrom);
                        }
                    }
                }
            }
        }

        return  rollbackList;
    }

    public int getDeletedListNum(){


        List faxResult=this.jdbcTemplate.queryForList("SELECT TIME_ ,ACTION_,USER_ID_,PROC_INST_ID_,MESSAGE_,TASK_ID_ FROM oa4.act_hi_comment where ACTION_='delete'");
        int flag=0;

        Iterator it = faxResult.iterator();
        while(it.hasNext()) {
            Map userMap = (Map) it.next();
                String instantId=userMap.get("PROC_INST_ID_").toString();
                List<HistoricTaskInstance> tasklist =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instantId).orderByHistoricTaskInstanceEndTime().desc().list();
                    if(tasklist!=null&&tasklist.size()<=1){
                        flag++;
                }
        }

        int listNum=faxResult.size()-flag;

        return  listNum;
    }




}

