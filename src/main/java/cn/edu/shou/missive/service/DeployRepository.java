package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Deploy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by XQQ on 2014/9/8.
 */
public interface DeployRepository extends PagingAndSortingRepository<Deploy, Long> {
    public List<Deploy> findAll();

    //根据配置名称，获取配置值
    @Query(value = "select de.value from Deploy de where de.name=:name")
    public String getEmailTemplate(@Param("name")String name);
}
