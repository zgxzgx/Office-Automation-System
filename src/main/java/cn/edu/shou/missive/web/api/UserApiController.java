package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.domain.missiveDataForm.UserSecret;
import cn.edu.shou.missive.service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/7/31.
 */
@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {
    MissivePublishFunction mpf=new MissivePublishFunction();

   private final static Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserRepository usDAO;


    @Autowired
    private JdbcTemplate jdbc;

    @RequestMapping(value="/getalluser", method= RequestMethod.GET)
    public List<UserFrom> getAlluser(){
        List<UserFrom> userlist =  new ArrayList<UserFrom>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserFrom userFrom=mpf.userToUserForm(user);
            userlist.add(userFrom);
        }
        return  userlist;
    }
    @RequestMapping(value="/getuserbyusername", method= RequestMethod.GET)
    public User getuserbyusername(String userName){

        return usDAO.findByUserName(userName);
    }
    @RequestMapping(value="/getuserbyid", method= RequestMethod.GET)
    public User getuserbyid(Long id){

        return usDAO.findOne(id);
    }
    @RequestMapping(value="/getuserbygroupname/{groupName}", method= RequestMethod.GET)
    public List<UserFrom> getuserbygroupname(@PathVariable String groupName){
        List<UserFrom> userlist =  new ArrayList<UserFrom>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserFrom userFrom=mpf.userToUserForm(user);
            if(userFrom.group!=null&&userFrom.group.groupName.equals(groupName)){
                userlist.add(userFrom);
            }
        }
        return  userlist;
    }

    @RequestMapping(value = "/PassWordEdit2", method = RequestMethod.POST)
    public String setPassWord2(Model model, @RequestParam Long id, @RequestParam String password, @AuthenticationPrincipal User currentUser) {
        //修改 郑小罗 20141204 用户名不能修改
        User user = usDAO.findOne(id);
        user.setPassword(password);
        usDAO.save(user);

        //model.addAttribute("ud", user2);


        return "";
    }


    @RequestMapping(value="/getallusersecret", method= RequestMethod.GET)
    public List<UserSecret> getAllusersecret(){
        List<UserSecret> userlist =  new ArrayList<UserSecret>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserSecret userFrom=mpf.userToUserForm_Name_UserName(user);
            userlist.add(userFrom);
        }
        return  userlist;
    }
    @RequestMapping(value="/getusersecretbygroupname/{groupName}", method= RequestMethod.GET)
    public List<UserSecret> getusersecretbygroupname(@PathVariable String groupName){
        List<UserSecret> userlist =  new ArrayList<UserSecret>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserSecret userFrom=mpf.userToUserForm_Name_UserName(user);
            if(userFrom.GroupName!=null&&userFrom.GroupName.equals(groupName)){
                userlist.add(userFrom);
            }
        }
        return  userlist;
    }
    @RequestMapping(value="/pad_enable/{userId}", method= RequestMethod.GET)
    public boolean getAlluser(@PathVariable int userId){
        Boolean abc=false;
        List<Map<String,Object>> enable = this.jdbc.queryForList("select pad_enable from users where id="+userId);
        if(enable.size()!=0&&enable.get(0)!=null&&enable.get(0).get("pad_enable")!=null){
            try{
                abc= Boolean.parseBoolean(enable.get(0).get("pad_enable").toString());
            }
            catch (Exception e){
//                logger.info("the user not exist!");
            }
        }
        return abc;
    }
    @RequestMapping(value="/validate", method= RequestMethod.GET)
    public boolean validateUser(String username,String password){
        Boolean res=false;
        String res_password="nothing";
        String qureyString="select password from users where username= \'"+username+"\'";
//        qureyString="\'";
        List<Map<String,Object>> result = this.jdbc.queryForList(qureyString);
        if(result.size()!=0&&result.get(0)!=null&&result.get(0).get("password")!=null){
            try{
                res_password= result.get(0).get("password").toString();
                if(res_password.equals(password)){
                    res=true;
                }
            }
            catch (Exception e){
//                logger.info("the user not exist!");
            }
        }
        return res;
    }
}
