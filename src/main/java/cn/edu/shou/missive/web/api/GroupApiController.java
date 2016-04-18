package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.GroupFrom;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by XQQ on 2014/8/5.
 */



@RestController
@RequestMapping(value = "/api/group")
public class GroupApiController{
    @Autowired
    private GroupRepository groupDAO;

    @Autowired
    private GroupDAO GD;

    @Autowired
    private UserRepository userr;

    @Autowired
    private UserDAO ud;

    @Autowired
    private  UserRepository ur;

    @Autowired
    private ActivitiService activitiService;


    @Autowired
    private GroupRepository gr;

  /*  @Autowired
    private AuthorityRepository AuR;*/

    @Autowired
    private AuthoritiesRepository AR;
    @Autowired
    private JdbcTemplate jdbc;    //jdbc查询方法

    private int m;
    private int n;


    MissivePublishFunction msf=new MissivePublishFunction();
    @Autowired
    private GroupRepository gpDAO;
    @RequestMapping(value="/getallgroup", method= RequestMethod.GET)
    public List<GroupFrom> getAllgroup(){
        List<GroupFrom> allGroup= new ArrayList<GroupFrom>();
        List<Group> groups=gpDAO.findAll();
        for(Group g:groups){
            GroupFrom gf=msf.groupToGroupFrom(g);
            allGroup.add(gf);
        }
        return allGroup;
    }

    //所有节点
    @RequestMapping(value = "nodeGroup",method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getAllNodeGroup(){

        List<Object> nodeValue=new ArrayList<Object>();

        //Group parentGroup=groupDAO.findOne((long) 1);  //父节点，东海分局
        List<Group>twoGroupList=groupDAO.getGroupListByGroup("东海分局"); //第二级节点

        //List<Group>parentGroup=this.groupDAO.findAll();
        List<Group>leafGroupList = groupDAO.getAllLeafGroup();   //所有叶子节点

        List<Group>allNodeGroup=new ArrayList<Group>();

        allNodeGroup.addAll(twoGroupList);

        List<Group> middle = new ArrayList<Group>(leafGroupList);
        middle.removeAll(allNodeGroup);
        allNodeGroup.addAll(middle);



        for(Group tempgroup:allNodeGroup)
        {
            Map<String,Object> result = new HashMap<String, Object>();
            result.put("id",tempgroup.getId());
            result.put("groupname",tempgroup.getGroup().getGroupName()+"---"+tempgroup.getGroupName());
            nodeValue.add(result);
        }

        return nodeValue;
    }

    //二级和三级归并
    @RequestMapping(value = "/leafGroup",method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getTwoAndThreeGroup(){

        List<Object> leafValue=new ArrayList<Object>();

        List<Group>twoGroupList=groupDAO.getGroupListByGroup("东海分局");
        List<Group>leafGroupList = groupDAO.getAllLeafGroup();

        for(Group tempgroup:leafGroupList)
        {
            Map<String,Object> result = new HashMap<String, Object>();
            result.put("id",tempgroup.getId());
            result.put("groupname",tempgroup.getGroup().getGroupName()+"---"+tempgroup.getGroupName());
            leafValue.add(result);
        }
        return leafValue;
    }


    //treeView获取所有组
    @RequestMapping(value = "/treeview",method = RequestMethod.GET)
    public List<Group> getGroupByParentId(@RequestParam(value = "id",required = false)Long id){

        List<Group> gplist=null;
        List<Group> gplist2=new ArrayList<Group>();
        if (id==null){
            Group gp=groupDAO.findByGroupName("东海分局");
            gplist=new ArrayList<Group>();
            gplist.add(gp);
        }else {
            gplist=groupDAO.findOne(id).getGroupList();
        }
        gplist2.add(gplist.get(0));
        for(int i=0;i<gplist.size()-1;i++){
            if(gplist.get(i).getId()!=gplist.get(i+1).getId())

                gplist2.add(gplist.get(i+1));
        }
        return gplist2;

    }

    //grid获取组里所有成员信息
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public List<Object> getUser(@RequestParam(value = "groupId")Long id){

        List<Object> newUsers=new ArrayList<Object>();
        List<User> users= new ArrayList<User>();
        if(id==1L){
            users.clear();
            users=userr.findAll();
        }else{
            Group group= groupDAO.findOne(id);
            users.clear();
            this.getUsersByGroup(group,users);
        }

        for(User uu:users)
        {
            Map<String,Object> result = new HashMap<String, Object>();
            result.put("id",uu.getId());
            result.put("name",uu.getName());
            result.put("userName",uu.getUserName());
            result.put("abrev",uu.getAbrev());
            result.put("password",uu.getPassword());
            result.put("sex",uu.getSex());
            result.put("tel",uu.getTel());
            result.put("email", uu.getEmail());
            if(uu.getGroup()!=null){
            result.put("groupName",uu.getGroup().getGroupName());}
            else{
                result.put("groupName","");}
            result.put("description",uu.getDescription());
            List<Authorities> author=AR.getAuthoritiesListByuserID(uu.getId());
            if(author.size()==0){
                result.put("authority", "普通用户");
            }else{
                //result.put("authority", uu.getAuthority().getAuthority());
                result.put("authority","管理员");


            }
            newUsers.add(result);
        }

        return newUsers;

    }

    private void getUsersByGroup(Group group,List<User> userlist)
    {

        if (group!=null)
        {
            for(User uu:group.getUsers()){
                userlist.add(uu);}
            if (group.getGroupList()!=null)
            {
                for (Group childGroup :group.getGroupList())
                {
                    getUsersByGroup(childGroup,userlist);
                }

            }

        }

    }


    //grid获取所有组
    @RequestMapping(value = "/gridGroup",method = RequestMethod.GET)
    public List<Group> getGridGroup(@RequestParam(value = "parentGroup")Long id){
        List<Group> groups = new ArrayList<Group>();
        if (id==1){
            groups=gr.findAllGroups();
        }else {
            Group gn = gr.findOne(id);
            this.getGroupByGroup(gn, groups);
        }
        return groups;
    }
    private void getGroupByGroup(Group group,List<Group> grouplist){

        if (group!=null  )
        {
            grouplist.addAll(group.getGroupList());
            Long groupID=null;
            if (group.getGroupList()!=null){
                for(Group gg:group.getGroupList())
                {   groupID=gg.getId();
                    if (!Long.toString(groupID).equals("2")){    //12-09 2改成1
                        getGroupByGroup(gg, grouplist);
                    }
                }
            }
        }
    }

    //12-8号备注删除用户
    @RequestMapping(value = "/deleteUser")
    @ResponseBody
    public String deleteUser(@RequestParam long id,@RequestParam String userName)
    {

        User user=userr.findOne(id);
        //Group group=gpDAO.findOne(100L);
        Group group = gr.findByGroupName("其他人员");
        user.setGroup(group);
        List<Authorities> authorlist = AR.getAuthoritiesListByuserID(id);
        if(authorlist!=null) {
            for (Authorities uu : authorlist) {
                jdbc.update("delete from Authorities where user_id=" + uu.getUser().getId()); //删除admin的用户
            }
        }
       jdbc.update("delete from users where id=" + id); //删除admin的用户
       activitiService.deleteUser(user.getUserName());

        // ur.delete(user);

        return "true";

    }

   private void IterativeDeleteUser(Long id){

       User user=userr.findOne(id);
       Group group = gr.findByGroupName("其他人员");
       user.setGroup(group);
       List<Authorities> authorlist = AR.getAuthoritiesListByuserID(id);
       if(authorlist!=null) {
           for (Authorities uu : authorlist) {
               jdbc.update("delete from Authorities where user_id=" + uu.getUser().getId()); //删除admin的用户
           }
       }
       jdbc.update("delete from users where id=" + id); //删除admin的用户
       activitiService.deleteUser(user.getUserName());


   }

    //删除组重新定义12-8
    @RequestMapping(value = "/deleteGroup")
    public List<Group> deleteGroup(@RequestParam long id)
    {
        Group group=gr.findOne(id);
        List<User> users =group.getUsers();
        Group unGroup=gr.findByGroupName("其他人员");

        if(group!=null &&group.getHasGroups()){
            for(Group tempGroup : group.getGroupList())
            {
                for(User tempUser:tempGroup.getUsers()){

                    IterativeDeleteUser(tempUser.getId());

                }

            }
        }else {
            for (User temUser : group.getUsers()) {
                IterativeDeleteUser(temUser.getId());

            }
        }

        jdbc.update("delete from groups where id=" + group.getId());
       // GD.deleteGroup(group);
        List<Group> list=new ArrayList<Group>();
        list.add(unGroup);
        return list;
    }



    //更新组
    @RequestMapping(value="/updateGroup")
    public List<Group> updateGroup(@RequestParam long id,@RequestParam String groupName,@RequestParam String description,@RequestParam String abbrevname,@RequestParam String abbrev)
    {
        Group groups=gr.findOne(id);
        groups.setGroupName(groupName);
        groups.setDescription(description);
        groups.setAbbrevname(abbrevname);
        groups.setAbbrev(abbrev);
        Group editedGroup = gr.save(groups);
        List<Group> grouplist = new ArrayList<Group>();
        grouplist.add(editedGroup);
        return grouplist;
    }

    //更新用户
    @RequestMapping(value="/updateUser")
    public List<User> updateUser(@RequestParam(value = "groupId")Long groupId,@RequestParam long id,@RequestParam String name,@RequestParam String userName,@RequestParam String abrev,@RequestParam String password,@RequestParam String tel,@RequestParam String sex,@RequestParam String email,@RequestParam String groupName,@RequestParam String authority)
    {
        //Authority author=AuR.findByAuthority(authority);

        User user=userr.findOne(id);
        Group group=gr.findByGroupName(groupName);
        Group group1=gr.findOne(groupId);
        user.setName(name);
        user.setUserName(userName);
        user.setAbrev(abrev);
        user.setPassword(password);
        user.setTel(tel);
        user.setSex(sex);
        //user.setAuthority(author);
        user.setEmail(email);
        user.setGroup(group);
        User updateUser=ur.save(user);
        if(authority.equals("管理员")){
            Authorities Author=new Authorities();
            Author.setUser(user);
            Author.setAuthority("admin");
            AR.save(Author);
        }else {
            List<Authorities> authorlist = AR.getAuthoritiesListByuserID(id);
            for (Authorities uu : authorlist) {

                jdbc.update("delete from Authorities where user_id=" + uu.getUser().getId()); //删除admin的用户
            }
        }
        List<User> userList=new ArrayList<User>();
        userList.add(updateUser);
        return  userList;

    }


    //更新用户描述
    @RequestMapping(value="/updateUserDes")
    public List<User> updateUserDes(@RequestParam long id,@RequestParam String name,@RequestParam String description )
    {

        User user=userr.findOne(102L);
        user.setName(name);
        user.setDescription(description);
        User updateUser=userr.save(user);
        List<User> userList=new ArrayList<User>();
        userList.add(updateUser);
        return  userList;

    }
    //获取用户
    @RequestMapping(value="/getDesData")
    public User getDesData(@RequestParam Long id){
        User user=userr.findOne(102L);

        return user;

    }



    //添加组
    @RequestMapping(value = "/createGroup")
    public List<Group> createGroup(@RequestParam(value = "parentGroup")Long groupId,@RequestParam String groupName,@RequestParam String description,@RequestParam String abbrevname,@RequestParam String abbrev) {


        Group upGroup=gr.findOne(groupId);
        Group gg = new Group();
        gg.setGroupName(groupName);
        gg.setDescription(description);
        gg.setAbbrevname(abbrevname);
        gg.setAbbrev(abbrev);
        gg.setIsDel(false);
        gg.setGroup(upGroup);
        gr.save(gg);
        List<Group> list = new ArrayList<Group>();
        list.add(gg);
        return list;

    }

    //添加用户
    @RequestMapping(value = "/createUser" )
    @ResponseBody
    public String createUserFunc(@RequestParam(value = "groupId")Long groupId,@RequestParam String name,@RequestParam String userName,@RequestParam String abrev,@RequestParam String password,@RequestParam String sex,@RequestParam String tel,@RequestParam String email,@RequestParam String authority) {


        Group upGroup=gr.findOne(groupId);
        //Authority author=AuR.findByAuthority(authority);
        User user=new User();
        user.setName(name);
        user.setUserName(userName);
        user.setPassword(password);
        user.setAbrev(abrev);
        user.setSex(sex);
        user.setTel(tel);
        user.setEmail(email);
        user.setDelayWarm("m");
        //user.setAuthority(author);
        user.setGroup(upGroup);
        user.setEnabled(true);
        ud.save(user);

        if(authority.equals("管理员")){
            Authorities Author=new Authorities();
            Author.setUser(user);
            Author.setAuthority("Admin");
            AR.save(Author);
        }

        return "true";
    }



    @RequestMapping(value="")
    public Group getDefaultGroup()
    {
        Group gn=gr.findByGroupName("东海分局");
        return gn;
    }

    //用户重复检测
    @RequestMapping(value="/getUsername")
    public String getUserName(@RequestParam String userName){

        if(userr.findByUserName(userName)!=null) {
            return "true";
        }else return  "false";

    }



}

