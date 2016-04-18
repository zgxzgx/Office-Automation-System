package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Notification;
import cn.edu.shou.missive.domain.Notification_User;
import cn.edu.shou.missive.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jiliwei on 2014/9/1.
 */
public interface Notification_UserRepository extends PagingAndSortingRepository<Notification_User,Long> {

    public Notification_User findByNotificationAndUser(Notification nf, User user);

    public Page<Notification_User> findByUser(User user, Pageable pageable);

    //查询未读公告的条数
    @Query("select count(*) from Notification_User b where b.status=false and b.user=?1")
    public Long getUnreadCount(User user);

    @Query("select count(*) from Notification_User b where b.status=false and b.user.id=?1")
    public Long getUnReadCount(Long id);


    //删除多条数据
    @Modifying
    @Transactional
    @Query("delete FROM Notification_User c where c.id in :multiID")
    public void delMultiData(@Param("multiID") List<Long> multiID);

    //模糊查询 按照字符串查询
    @Query("select d from Notification_User d where d.user=?1 and d.notification.title like ?2")
    public Page<Notification_User> findSearchResult(User user, String searchValue, Pageable pageable);

    //模糊查询 按照字符串查询
    @Query("select e from Notification_User e where e.notification.writer=?1")
    public Page<Notification_User> findSendResult(String writer, Pageable pageable);

}
