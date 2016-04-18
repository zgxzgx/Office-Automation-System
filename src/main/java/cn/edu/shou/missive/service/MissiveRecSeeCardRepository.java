package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveRecSeeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Created by TISSOT on 2014/7/22.
 */
public interface MissiveRecSeeCardRepository extends JpaRepository<MissiveRecSeeCard,Long> {
    @Query("select a from MissiveRecSeeCard a where a.instanceId=:instanceid")
    public MissiveRecSeeCard getMissData(@Param("instanceid") String instanceid);

    @Query("select a.code from MissiveRecSeeCard a where a.instanceId!=:instanceid")
    public List<String> getMissiveCode(@Param("instanceid") String instanceid);
}
