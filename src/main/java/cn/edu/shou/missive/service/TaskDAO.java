package cn.edu.shou.missive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqhe on 14-7-31.
 */
@Repository
public class TaskDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getCurrentEditableFieldsByTaskId(int taskid)
    {
        List<String> result=new ArrayList<String>();
        String taskName=null;
        try {
            taskName = this.jdbcTemplate.queryForObject(
                    "select ACT_RU_TASK.NAME_ from ACT_RU_TASK where ID_="+taskid,String.class);
        }
        catch (Exception e){
            taskName=null;
        }
        if (taskName!=null){
            String sqlstring = "select missive_field.input_id from  missive_field ,task_name where missive_field.task_id=task_name.id and"
                    +" task_name.process_type_id = 1 and task_name.task_name='"+taskName+"'";
            result = this.jdbcTemplate.queryForList(sqlstring,String.class);
        }
//        else result=null;


        return  result;
    }

    public List<String> getCurrentEditableFieldsByTaskId(Long taskid)
    {
        return this.getCurrentEditableFieldsByTaskId(Integer.parseInt(taskid.toString()));
    }


    public List<String> getCurrentEditableFieldsByTaskId(int taskid,int processType)
    {
        List<String> result=new ArrayList<String>();
        String taskName=null;
        try {
            taskName = this.jdbcTemplate.queryForObject(
                    "select ACT_RU_TASK.NAME_ from ACT_RU_TASK where ID_="+taskid,String.class);
        }
        catch (Exception e){
            taskName=null;
        }
        if (taskName!=null){
            result = this.jdbcTemplate.queryForList(
                    "select missive_field.input_id from  missive_field ,task_name where missive_field.task_id=task_name.id and"
                            +" task_name.process_type_id = "+processType+" and task_name.task_name='"+taskName+"'",String.class);
        }
//        else result=null;


        return  result;
    }


    public List<String> getCurrentEditableFieldsByTaskId(long taskid,int processType)
    {
        List<String> result=new ArrayList<String>();
        String taskName=null;
        try {
            taskName = this.jdbcTemplate.queryForObject(
                    "select ACT_RU_TASK.NAME_ from ACT_RU_TASK where ID_="+taskid,String.class);
        }
        catch (Exception e){
            taskName=null;
        }
        if (taskName!=null){
            result = this.jdbcTemplate.queryForList(
                    "select missive_field.input_id from  missive_field ,task_name where missive_field.task_id=task_name.id and"
                            +" task_name.process_type_id = "+processType+" and task_name.task_name='"+taskName+"'",String.class);
        }
//        else result=null;


        return  result;
    }

    public String getTaskNameById(Long taskid)
    {
        String taskName = this.jdbcTemplate.queryForObject(
                "select ACT_RU_TASK.NAME_ from ACT_RU_TASK where ID_="+taskid,String.class);
        return taskName;
    }


    public String getCurrentTaskName(int taskid){
        String taskName=null;
        try {
            taskName = this.jdbcTemplate.queryForObject(
                    "select ACT_RU_TASK.NAME_ from ACT_RU_TASK where ID_="+taskid,String.class);
        }
        catch (Exception e){}
        return taskName;
    }
    public String getTaskAssigeeUserName(int instanceid,int taskid){
        String UserName=null;
        try {
            UserName = this.jdbcTemplate.queryForObject(
                    "select ACT_RU_TASK.ASSIGNEE_ from ACT_RU_TASK where PROC_INST_ID_="+instanceid+" and ID_="+taskid,String.class);
        }
        catch (Exception e){}
        return UserName;
    }
}
