package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.User;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sqhe on 14-7-20.
 */
//@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {



    public List<User> findAll();

    @Query(value = "select t.userName,t.tel,t.description as name,t.id from User t where t.description IS NOT null and t.description <> ''")
    public List<User> getUsernames();

    public User findByUserName(String username);

    @Query(value="select u from User u where u.userName=?1")
    public User findUserByUserName(String userName);

    @Query(value = "select t.userName from User t where t.id=?1")
    public String findUserNameById(Long id);

    //根据ID 返回List<User对象>
    @Query("select  u from User u where u.id=:id")
    public List<User>getUserInfoByID(@Param("id")Long ID);

    @Query(value = "select t from User t where t.group.id in ?1")
    public List<User> getUserListByGroupList(List<Long> groupIdList);

    @Query(value = "select t from User t where t.id in ?1")
    public List<User> getUserListByIdList(List<Long> userIdList);

    public User findByUserNameAndPassword(String username,String passwork);

    //by zxl 2012-12-07
    //根据用户ID删除记录
    @Modifying
    @Transactional
    @Query(value = "delete from User u where u.id=:id")
    public void deleteUserByID(@Param("id")Long id);

    //根据用户名返回用户编号
    @Query(value = "select u.id from User u where u.userName=:name")
    public String getUserIDByUserName(@Param("name")String name);

    //根据用户名获取用户
    @Query(value = "select u from User u where u.userName=:userName")
    public User getUserInfoByUserName(@Param("userName")String userName);





}
