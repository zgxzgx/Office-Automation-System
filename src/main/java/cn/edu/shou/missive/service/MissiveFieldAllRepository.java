package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveFieldAll;
import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by jiliwei on 2014/7/20.
 */
//@RepositoryRestResource(collectionResourceRel = "missiveFieldAll", path = "missiveFieldAll")
public interface MissiveFieldAllRepository extends PagingAndSortingRepository<MissiveFieldAll, Long> {

    List<MissiveFieldAll> findAll();

}
