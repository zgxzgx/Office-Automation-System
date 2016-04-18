package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Group;
import cn.edu.shou.missive.domain.MissivePublish;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MissivePublishRepository extends CrudRepository<MissivePublish, Long> {
    public MissivePublish findByProcessID(long processID);
    public List<MissivePublish> findAll();


}