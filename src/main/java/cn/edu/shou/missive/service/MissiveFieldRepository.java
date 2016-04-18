package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveField;
import cn.edu.shou.missive.domain.TaskName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.awt.print.Pageable;
import java.util.List;


//@RepositoryRestResource(collectionResourceRel = "missiveField", path = "missiveField")
public interface MissiveFieldRepository extends PagingAndSortingRepository<MissiveField, Long> {

    @Query("select a from MissiveField a where a.isDel=:isDel")
    public List<MissiveField>findByIsDel(@Param("isDel") String isDel);

    public List<MissiveField>findAll();

    @Query("select a from MissiveField a where  a.taskName=:tn")
    public List<MissiveField> getEditControl(@Param("tn")TaskName tn);



}
