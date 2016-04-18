package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.Group;
import cn.edu.shou.missive.domain.Feedback;
import cn.edu.shou.missive.domain.MissivePublishFunction;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by sqhe on 14-7-23.
 */
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value = "/user")
public class UserController {

    @Value("${spring.main.homedir}")
    String homedir;

    @Autowired
    UserRepository userDAO;

    @Autowired
    GroupRepository gr;

    @Autowired
    DeployRepository DR;

    @Autowired
    FeedbackRepository FR;
    @Autowired
    private UserDAO ud;
    @Autowired
    private JdbcTemplate jdbcTemplate;
//    @Autowired
//    ScheduledTasks st;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);


    @RequestMapping(value = "/info/{userid}")
    public String getUserInfo(Model model, @PathVariable("userid") Long userid, @AuthenticationPrincipal User currentUser) {
        /*model.addAttribute("user",this.userDAO.findOne(userid));
        return "userinfo";*/


           // st.dealCurrentAllTasksList();


        String delayWarm = "";
        User user = this.userDAO.findOne(userid);
        //添加延时处理信息

        if (user.delayWarm.equals("m")) {
            delayWarm = "邮件提醒：" + user.delaynum + " 小时";
        } else if (user.delayWarm.equals("e")) {
            delayWarm = "短信提醒：" + user.delaynum + " 小时";
        } else {
            delayWarm = "不提醒";
        }

        model.addAttribute("usr", user);
        model.addAttribute("delayWarm", delayWarm);
        model.addAttribute("ud", currentUser);
        return "userinfo";
    }

    @RequestMapping(value = "/groupTree")
    public String getgroupTree(Model model, @AuthenticationPrincipal User currentUser) {
        MissivePublishFunction mpf = new MissivePublishFunction();
        model.addAttribute("ud", currentUser);
        model.addAttribute("userDataSource", mpf.userToUserForm_Name_UserName(ud.getAllUserNameAndGroupName()));

        return "yhgl";
    }

    @RequestMapping(value = "/transfer")
    public String transfer(Model model, @AuthenticationPrincipal User currentUser) {
        MissivePublishFunction mpf = new MissivePublishFunction();
        model.addAttribute("ud", currentUser);
        model.addAttribute("userDataSource", mpf.userToUserForm_Name_UserName(ud.getAllUserNameAndGroupName()));

        return "transferOfPersonnel";
    }

    @RequestMapping(value = "/config/{itemid}.html")
    public String getConfigInfo(Model model, @PathVariable("itemid") Long itemid, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("info", this.DR.findOne(itemid));
        model.addAttribute("user", currentUser);

        return "configpop";
    }

    @RequestMapping(value = "/edit/{userid}/{userId}.html")
    public String getUserInfoForEdit(Model model, @PathVariable Long userid,@PathVariable Long userId, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("usr", userDAO.findOne(userid));
        //model.addAttribute("user", currentUser);
        model.addAttribute("user", userDAO.findOne(userId));
        return "userinfoedit";
    }


    @RequestMapping(value = "/edit/image", method = RequestMethod.POST)
    public String setUserInfo(Model model, @RequestParam String imagePath, @RequestParam Long id, @AuthenticationPrincipal User currentUser) {
        User user = userDAO.findOne(id);

        user.setImagePath(imagePath);

        userDAO.save(user);

        model.addAttribute("usr", user);

        model.addAttribute("user", currentUser);
        return "userinfoedit";


    }

    //郑小罗 2014/12/7 10:20:01
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String setUserInfo(Model model, @RequestParam String userName, @RequestParam String password, @RequestParam String msg, @RequestParam String emailSend
                              ,@RequestParam String padEnable,@RequestParam String delay, @RequestParam String delayNum, @RequestParam String sex, @RequestParam String email, @RequestParam String abrev,
                              @RequestParam String tel, @RequestParam Long id, @RequestParam Long group, @AuthenticationPrincipal User currentUser) {
        //修改 郑小罗 20141204 用户名不能修改
        User user = userDAO.findOne(id);
        user.setPassword(password);

        user.setSex(sex);
        user.setEmail(email);
        user.setTel(tel);
        user.setDelaynum(Integer.parseInt(delayNum));
        user.setEmailSend(emailSend.equals("1") ? true : false);
        user.setMsgSend(msg.equals("1") ? true : false);
        user.setPadEnable(padEnable.equals("1") ? true : false);
        user.setDelayWarm(delay);
        user.setAbrev(abrev);
        Group gp = gr.findOne(group);
        user.setGroup(gp);
        userDAO.save(user);
        model.addAttribute("usr", user);
        model.addAttribute("user", currentUser);


        return "redirect:/user/info/" + id + ".html";
    }


    @RequestMapping(value = "/PassWordEdit", method = RequestMethod.POST)
    public String setPassWord(Model model, @RequestParam Long id, @RequestParam String password2, @AuthenticationPrincipal User currentUser) {
        //修改 郑小罗 20141204 用户名不能修改
        User user = userDAO.findOne(id);
        //user.setPassword(password2);
        userDAO.save(user);
        model.addAttribute("user", currentUser);


        return "redirect:/user/info/" + id + ".html";
    }



    @RequestMapping("/image/{id}")
    public ResponseEntity<byte[]> downFile(@PathVariable Long id) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");

        //默认文件名称
        User user = userDAO.findOne(id);

        String path = "";
        String downFileName = "";
        String filePath = "";//文件路径
        String defaultFile = homedir + "/upload/userImg/default.jpg";

        downFileName = user.getImagePath();

        if (downFileName != null) {
            path = homedir + "/upload/thumbnail/" + id;
        } else {


            //path=req.getSession().getServletContext().getRealPath("/")+"img";

//            path="/esicmissive_springboot/defalutImage";
            path = homedir + "/";
            downFileName = "default.jpg";
        }


        /*try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downFile exception:",e);
            e.printStackTrace();
        }*/

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        if(downFileName.indexOf(".jpg")>-1){
            headers.setContentType(MediaType.IMAGE_JPEG);
        }
        else if(downFileName.indexOf(".png")>-1){
            headers.setContentType(MediaType.IMAGE_PNG);
        }
        else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }


        headers.setContentDispositionFormData("attachment", downFileName);
        filePath = path + "/" + downFileName;//头像文件，判断是否存在，不存在使用默认头像
        if (!FileOperate.exitFile(filePath)) {
            filePath = defaultFile;//如果头像不存在，使用系统默认的头像文件
        }

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("downFile-HttpStatus exception:",e);
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }
    //用户反馈
    @RequestMapping(value = "/edit/feedback/{id}",method = RequestMethod.POST)
    public String submitFeedback(@PathVariable Long id, @RequestParam String sQues,@RequestParam String sFeedType,@RequestParam String sSubmitType,@RequestParam String sFeedCon,@RequestParam String sUserName,@RequestParam String sPhone,@RequestParam String sDepart,@RequestParam String sImagePath) {
        User user = userDAO.findOne(id);

        Feedback feedback=new Feedback();
        feedback.setFeedProblem(sQues);
        feedback.setFeedType(sFeedType);
        feedback.setSubmitType(sSubmitType);
        feedback.setFeedContent(sFeedCon);
        feedback.setUserName(sUserName);
        feedback.setImageString(sImagePath);
        feedback.setPhoneNumber(sPhone);
        feedback.setDepartment(sDepart);
        feedback.setDepartment(user.getGroup().getGroupName());
        feedback.setPhoneNumber(user.getTel());
        feedback.setUserName(user.getName());


        FR.save(feedback);

        return "FeedBackjs";


    }


    @RequestMapping(value = "/feedback")
    public String setFeedBack(Model model, @AuthenticationPrincipal User currentUser) {
        MissivePublishFunction mpf = new MissivePublishFunction();
        model.addAttribute("ud", mpf.userToUserForm(this.userDAO.findOne(currentUser.getId())));
        return "FeedBackjs";
    }


    //修改密码验证
    @RequestMapping(value = "/editPassport")
    public Boolean editPassword(Model model, @AuthenticationPrincipal User currentUser,@RequestParam String password) {

        if(password.equals(currentUser.getPassword())){
            return true;
        }else return false;
    }

    //获取用户签名图片
    @RequestMapping("/getSignImg/{userId}")
    public ResponseEntity<byte[]> getUserSignImg(@PathVariable Long userId) {
        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "");
        byte[] img = new byte[1024];//接收签名字符流
        try {
            //默认文件名称
            User user = userDAO.findOne(userId);
            if (user != null) {
                img = user.getSignImg();

                return new ResponseEntity<byte[]>(img,
                        headers,
                        HttpStatus.OK);
            }
        } catch (Exception e) {

            logger.error("getUserSignImg exception:",e);

            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }




}
