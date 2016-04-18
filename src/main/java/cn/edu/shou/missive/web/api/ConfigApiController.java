package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.web.MissiveReceiveFunction;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.activiti.engine.task.Task;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;


/**
 * Created by XQQ on 2014/9/7.
 */
@RestController
@RequestMapping(value = "/api/config")
public class ConfigApiController {
    @Autowired
    MissiveTypeRepository mt;
    @Autowired
    SecretLevelRepository sl;
    @Autowired
    UrgentLevelRepository ul;
    @Autowired
    DeployRepository de;
    @Autowired
    UserConfigRepository ucr;
    @Autowired
    private ActivitiService actService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SendConfigRepository sendService;
    @Autowired
    private MyMissiveController mmc;

    @Autowired
    private JdbcTemplate jdbc;



    @Autowired
    UserDAO ud;


    MissivePublishFunction mpf=new MissivePublishFunction();





    //read获取所有公文类型
    @RequestMapping(value = "/missiveType",method = RequestMethod.GET)
    public List<MissiveType> getMissiveType(){
        return  mt.findAll();

    }
    //read获取所有密级
    @RequestMapping(value = "/secretLevel",method = RequestMethod.GET)
    public List<SecretLevel> getSecretLevel(){
        return sl.findAll();
    }

    //read获取所有紧急程度
    @RequestMapping(value = "/urgentLevel",method = RequestMethod.GET)
    public List<UrgentLevel> getUrgentLevel(){
        return ul.findAll();
    }
    //read获取所有 设置
    @RequestMapping(value = "/deploy",method = RequestMethod.GET)
    public List<Deploy> getDeploy(){
        return de.findAll();
    }



    //创建公文类型
    @RequestMapping(value = "/createMissiveType",method = RequestMethod.GET)
    public List<MissiveType> createMissiveType(@RequestParam String typeName){
        MissiveType mm=new MissiveType();
        mm.setTypeName(typeName);
        //mm.setDeleted(false);

        mt.save(mm);
        List<MissiveType> list=new ArrayList<MissiveType>();
        list.add(mm);
        return list;
    }
    //创建密级
    @RequestMapping(value = "/createSecretLevel",method =RequestMethod.GET)
    public List<SecretLevel> createSecretLevel(@RequestParam String secretLevelName){
        SecretLevel ss=new SecretLevel();
        ss.setSecretLevelName(secretLevelName);
        sl.save(ss);
        List<SecretLevel> list=new ArrayList<SecretLevel>();
        list.add(ss);
        return list;

    }

    //创建紧急程度
    @RequestMapping(value = "/createUrgentLevel",method =RequestMethod.GET)
    public List<UrgentLevel> createUrgentLevel(@RequestParam String value){
        UrgentLevel uu=new UrgentLevel();
        uu.setValue(value);
        ul.save(uu);
        List<UrgentLevel> list=new ArrayList<UrgentLevel>();
        list.add(uu);
        return list;

    }
    //创建 设置
    @RequestMapping(value = "/createDeploy",method =RequestMethod.GET)
    public List<Deploy> createDeploy(@RequestParam String name,@RequestParam String value,@RequestParam String description){
        Deploy dd=new Deploy();
        dd.setName(name);
        dd.setValue(value);
        dd.setDescription(description);
        de.save(dd);
        List<Deploy> list=new ArrayList<Deploy>();
        list.add(dd);
        return list;

    }
    //更新公文类型
    @RequestMapping(value = "updateMissiveType")
    public List<MissiveType> updateMissiveType(@RequestParam long id,@RequestParam String typeName){
        MissiveType mm=mt.findOne(id);
        mm.setTypeName(typeName);
        //mm.setDeleted(false);
        MissiveType updateMissiveType=mt.save(mm);
        List<MissiveType> missiveTypeList=new ArrayList<MissiveType>();
        missiveTypeList.add(updateMissiveType);
        return missiveTypeList;
    }
    //更新密级
    @RequestMapping(value = "updateSecretLevel")
    public List<SecretLevel> updateSecretLevel(@RequestParam long id,@RequestParam String secretLevelName){
        SecretLevel ss=sl.findOne(id);
        ss.setSecretLevelName(secretLevelName);
        SecretLevel updateSecretLevel=sl.save(ss);
        List<SecretLevel> SecretLevelList=new ArrayList<SecretLevel>();
        SecretLevelList.add(updateSecretLevel);
        return SecretLevelList;
    }
    //更新紧急程度
    @RequestMapping(value = "updateUrgentLevel")
    public List<UrgentLevel> updateUrgentLevel(@RequestParam long id,@RequestParam String value){
        UrgentLevel uu=ul.findOne(id);
        uu.setValue(value);
        UrgentLevel updateUrgentLevel=ul.save(uu);
        List<UrgentLevel> UrgentLevelList=new ArrayList<UrgentLevel>();
        UrgentLevelList.add(updateUrgentLevel);
        return UrgentLevelList;
    }
    //更新设置
    @RequestMapping(value = "updateDeploy")
    public List<Deploy> updateDeploy(@RequestParam long id,@RequestParam String name,@RequestParam String value){
        Deploy dd=de.findOne(id);
        dd.setValue(value);
        dd.setName(name);
        de.save(dd);
        List<Deploy> DeployList=new ArrayList<Deploy>();
        DeployList=de.findAll();    //没有返回当前值，返回所有数组
        return DeployList;
    }

    //删除公文类型
    @RequestMapping(value = "deleteMissiveType")
    public List<MissiveType> deleteMissiveType(@RequestParam long id){
        MissiveType mm=mt.findOne(id);
        mt.delete(mm);
        List<MissiveType> missiveTypeList=new ArrayList<MissiveType>();
        missiveTypeList.add(mm);
        return missiveTypeList;

    }
    //删除密级
    @RequestMapping(value = "deleteSecretLevel")
    public List<SecretLevel> deleteSecretLevel(@RequestParam long id){
        SecretLevel ss=sl.findOne(id);
        sl.delete(ss);
        List<SecretLevel> SecretLevelList=new ArrayList<SecretLevel>();
        SecretLevelList.add(ss);
        return SecretLevelList;

    }

    //删除紧急程度
    @RequestMapping(value = "deleteUrgentLevel")
    public List<UrgentLevel> deleteUrgentLevel(@RequestParam long id){
        UrgentLevel uu=ul.findOne(id);
        ul.delete(uu);
        List<UrgentLevel> UrgentLevelList=new ArrayList<UrgentLevel>();
        UrgentLevelList.add(uu);
        return UrgentLevelList;

    }
    //删除设置
    @RequestMapping(value = "deleteDeploy")
    public List<Deploy> deleteDeploy(@RequestParam long id){
        Deploy dd=de.findOne(id);
        de.delete(dd);
        List<Deploy> DeployList=new ArrayList<Deploy>();
        DeployList.add(dd);
        return DeployList;

    }

    //用户公文配置增删改查
    @RequestMapping(value = "/configGrid")
    public List<UserConfig> getConfigGrid(@AuthenticationPrincipal User currentUser){
        List<UserConfig> dlist;
        dlist=ucr.findAll();
        return dlist;
    }

    @RequestMapping(value = "/createUserConfig")
    public List<UserConfig> createMissiveConfig(@RequestParam String name,@RequestParam String value,@RequestParam String description,@AuthenticationPrincipal User currentUser){
        UserConfig uu=new UserConfig();
        uu.setName(name);
        uu.setValue(value);
        uu.setDescription(description);
        ucr.save(uu);
        List<UserConfig> list=new ArrayList<UserConfig>();
        list.add(uu);
        return list;
    }

    //更新用户词条
    @RequestMapping(value = "updateUserConfig")
    public List<UserConfig> updateMissiveList(@RequestParam long id,@RequestParam String name,@RequestParam String value){
        UserConfig uu=ucr.findOne(id);
        uu.setName(name);
        uu.setValue(value);
        UserConfig updateConfigList=ucr.save(uu);
        List<UserConfig> ConfigList=new ArrayList<UserConfig>();
        ConfigList.add(updateConfigList);
        return ConfigList;
    }

    //删除用户词条
    @RequestMapping(value = "deleteUserConfig")
    public List<UserConfig> deleteUserMissInfo(@RequestParam long id){
        UserConfig uu=ucr.findOne(id);
        ucr.delete(uu);
        List<UserConfig> ConfigList=new ArrayList<UserConfig>();
        ConfigList.add(uu);
        return ConfigList;

    }
   //name重复检测
    @RequestMapping(value="/getName")
    public String getUserName(@RequestParam String name){

        if(ucr.findByName(name)!=null) {
            return "true";
        }else return  "false";

    }
    @RequestMapping(value="/getUser")
    public List<UserFrom> getUser(){

        MissiveReceiveFunction mrf=new MissiveReceiveFunction();
        List<UserFrom> lists=new ArrayList<UserFrom>();
        Iterable<User> users = ud.findAll();
        /*List<User> users=ud.findAll();*/
        for(User user:users){
            UserFrom uf= mrf.userToUserForm(user);
            String name = user.getName();
            String userName=user.getUserName();
            uf.setName(name);
            uf.setUserName(userName);
            lists.add(uf);
        }
        return lists;


    }

    @RequestMapping(value="/actRuTaskInsert")
    public String ClickUpMove(@AuthenticationPrincipal User currentUser,@RequestParam int  isTop,@RequestParam Long data){
        String s;
        Task task = actService.getCurrentTasksByProcessInstanceId(data);
        Date date=task.getCreateTime();
        s=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);



       //jdbc.update("update act_ru_task set description_=" + isTop + " where PROC_INST_ID_=" + data); //更新置顶标志
       jdbc.update("update act_ru_task set description_=" + isTop + ",create_time_='"+s+"' where PROC_INST_ID_=" + data); //更新置顶标志
        System.out.println("update act_ru_task set description_=" + isTop + ",create_time_='"+s+"' where PROC_INST_ID_=" + data);
        return "true";

    }

    @RequestMapping(value = "/getTaskName/{processId}")
    public TaskForm getMissiveType(@PathVariable String processId){
        String taskName="";
        Task task = this.taskService.createTaskQuery().active().processInstanceId(processId).singleResult();
        List<Task> taskList=new ArrayList<Task>();
        List<TaskForm> ltfIng=new ArrayList<TaskForm>();

        taskList.add(task);
        if(taskList!=null&&task!=null){
            taskName=task.getName();
            ltfIng = mmc.getTaskFormByTask(taskList);

        }
        return ltfIng.get(0);

    }




}
