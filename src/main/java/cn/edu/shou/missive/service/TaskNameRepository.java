package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.domain.TaskName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by jiliwei on 2014/7/18.
 */
//@RepositoryRestResource(collectionResourceRel = "taskName", path = "taskName")
public interface TaskNameRepository extends PagingAndSortingRepository<TaskName, Long> {

    List<TaskName>findAll();

    TaskName findByTaskName(String taskName);

    //@Query("select t from TaskName t where t.taskName=:taskName")
    public List<TaskName> findByTaskNameAndProcessType(String taskName,ProcessType processType);

    @Query("select a from TaskName a where a.taskName=:tn and a.processType=:pt")
    public TaskName findByName(@Param("tn")String taskName,@Param("pt")ProcessType pt );

    //根据ID获取任务实例
    @Query("select tsk from TaskName tsk where tsk.id=:id")
    public TaskName getTaskNameByID(@Param("id")Long id);
    //根据名称获取任务实例
    @Query("select tsk from TaskName tsk where tsk.taskName=:tskName and tsk.processType=:processType")
    public TaskName getTaskNameByName(@Param("tskName")String tskName,@Param("processType")ProcessType processType);



}
