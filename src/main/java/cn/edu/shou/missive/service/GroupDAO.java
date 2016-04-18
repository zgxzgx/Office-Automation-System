package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by XQQ on 2014/7/25.
 */
//@RepositoryRestResource(collectionResourceRel = "group", path = "group")
@Repository
public class GroupDAO  {

    @Autowired
    private GroupRepository groupRepository;
    private EntityManager entityManager;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbc;


    public Group save(Group group){
        return this.groupRepository.save(group);
    }

    public Group findByGroupId(Long GroupId)
    {
        return this.groupRepository.findOne(GroupId);
    }


    public List<Group> findGroupListByGroup(String groupName)
    {
        return this.groupRepository.getGroupListByGroup(groupName);
    }

    public Group findGroupListByGroupID(Long id)
    {
        return this.groupRepository.getGroupListByGroupID(id);
    }

    public List<Group> findAll(){
        return this.groupRepository.findAll();
    }

    public List<Group> getAllGroups(){
        return this.groupRepository.findAllGroups();

    }

    public List<Map<String,Object>> getAllGroupsJDBC()
    {
       return  this.jdbc.queryForList("select * from groups");
    }


    /*public void deleteUser(Group group)
    {
        groupRepository.delete(group);
        List<User> users= userRepository.getUserListByGroupID(group.getId());
        for(user: group.getUsers()){

            this.activitiService.deleteUser(user.getUserName());
        }

    }*/
    public void deleteGroup(Group group)
    {
        this.groupRepository.delete(group);
        //this.activitiService.deleteUser(user.getUserName());


    }



}
