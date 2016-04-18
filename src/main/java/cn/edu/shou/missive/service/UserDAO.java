package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by sqhe on 14-7-31.
 */
@Repository
public class UserDAO implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivitiService activitiService;


    public User save(User user)
    {
        this.activitiService.addNewUser(user.getUserName());
        return this.userRepository.save(user);
    }
    public User saveUser(User user)
    {
        return this.userRepository.save(user);
    }

    public User findById(Long userId)
    {
        return this.userRepository.findOne(userId);
    }

    public User findByUserName(String userName)
    {
        return userRepository.findByUserName(userName);
    }


    public List<User> findUserListByGroupList(List<Long> groupIdList)
    {
        return userRepository.getUserListByGroupList(groupIdList);

    }

    public List<User> findUserListByIdList(List<Long> userIdList)
    {
        return this.userRepository.getUserListByIdList(userIdList);
    }

    public List<User> findAll(){
        return this.userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        return user;
    }

    public void deleteUser(User user)
    {
        userRepository.delete(user);
        this.activitiService.deleteUser(user.getUserName());
    }

    @Autowired
    private JdbcTemplate jdbc;

    public List<Map<String, Object>>  getAllUserNameAndGroupName()
    {
        return jdbc.queryForList("select u.username,u.name,u.abrev,g.group_name,g.abbrev,g.abbrevname from users as u,groups as g where u.group_id=g.id;");
    }

    public List<Map<String, Object>>  getDepLeaderUserName(long groupId)
    {
        return jdbc.queryForList("select u.username,u.name,u.abrev,g.group_name,g.abbrev,g.abbrevname from users as u,groups as g where u.group_id="+ groupId +" and u.is_dep_leader=1 and u.group_id=g.id;");

    }
    public List<Map<String, Object>>  getDepLeaderUserNames()
    {
        return jdbc.queryForList("select u.username,u.name,u.abrev,g.group_name,g.abbrev,g.abbrevname from users as u,groups as g where u.is_dep_leader=1 and u.group_id=g.id;");

    }

}
