package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.UrgentLevel;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by XQQ on 2014/9/8.
 */
public interface UrgentLevelRepository extends PagingAndSortingRepository<UrgentLevel, Long> {

    public List<UrgentLevel> findAll();
    public UrgentLevel findOne(Long id);
}