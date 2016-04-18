package cn.edu.shou.missive.service;


import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.domain.TaskName;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "processtype", path = "processtype")
public interface ProcessTypeRepository extends PagingAndSortingRepository<ProcessType, Long> {

      List<ProcessType>findAll();
        //ProcessType findByName(String name);

    //@Query("select p from ProcessType p where p.name=:name")
    public ProcessType findByName(String name);

    //根据ID获取处理流程实例
    @Query("select  process from ProcessType process where process.id=:id")
    public  ProcessType getProcessByID(@Param("id")Long id);
    //根据流程名称获取流程实例
    @Query("select process from ProcessType process where process.name=:name")
    public ProcessType getProcessByName(@Param("name")String name);





}
