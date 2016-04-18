package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveReceiveTaskDealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by TISSOT on 2014/9/19.
 */
@Repository
public interface MissiveReceiveTaskDealerRepository extends JpaRepository<MissiveReceiveTaskDealer,Long> {
    @Query("select a from MissiveReceiveTaskDealer a where a.instanceId=:instanceid")
    public MissiveReceiveTaskDealer getTaskDealer(@Param("instanceid")Long instanceid);

}
