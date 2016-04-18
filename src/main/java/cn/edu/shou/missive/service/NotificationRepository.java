package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by TISSOT on 2014/7/17.
 */
public interface NotificationRepository extends PagingAndSortingRepository <Notification,Long>{
    @Query("select a from Notification a where a.isDel='0'" )
    public List<Notification> findByisDel();
    @Query("select e from Notification e where e.writer=?1")
    public Page<Notification> findSendResult(String writer, Pageable pageable);

}
