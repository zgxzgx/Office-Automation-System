package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.MissivePublishFunction;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.domain.missiveDataForm.UserSecret;
import cn.edu.shou.missive.service.UserConfigRepository;
import cn.edu.shou.missive.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.ParseException;

/**
 * Created by zgx on 2016/3/30.
 */
@Controller
@RequestMapping(value = "/slider")
@SessionAttributes(value = {"userbase64", "user_id", "user"})
public class SliderController {
    @Autowired
    private UserConfigRepository userConfigR;
    MissivePublishFunction mpf=new MissivePublishFunction();
    @RequestMapping(value = "/getReceiver")
    @ResponseBody
    public String getReceiverUserName ()  {
        String receiveUserName="";

        if(userConfigR.findByName("missiveReceiver")!=null){
            receiveUserName=userConfigR.findByName("missiveReceiver").getValue();
        }
        return receiveUserName;
    }

    @RequestMapping(value = "/getRegister")
    @ResponseBody
    public String getRegisterUserName ()  {
        String registerUserName="";

        if(userConfigR.findByName("receiveMissiveRegister")!=null){
            registerUserName=userConfigR.findByName("receiveMissiveRegister").getValue();
        }
        return registerUserName;
    }
    @RequestMapping(value = "/getNews")
    @ResponseBody
    public String getNewsUserName ()  {
        String newsUserName="";

        if(userConfigR.findByName("ExternalNewsReceiver")!=null){
            newsUserName=userConfigR.findByName("ExternalNewsReceiver").getValue();
        }
        return newsUserName;
    }
}
