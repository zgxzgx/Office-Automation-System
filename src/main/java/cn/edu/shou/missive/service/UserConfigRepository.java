package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.UserConfig;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2014/8/2.
 */
@Repository
public interface UserConfigRepository extends PagingAndSortingRepository<UserConfig,Long>{
    public List<UserConfig> findAll();

    public UserConfig findByName(String name);


}
