package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.publish;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zgx on 2015/6/29.
 */
@Repository
public interface publishRepository extends PagingAndSortingRepository<publish, Long> {
    public List<publish> findAll();

}
