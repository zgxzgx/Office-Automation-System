package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by zgx on 2016/1/12.
 */
@Controller
@RequestMapping(value="/expired")
@SessionAttributes(value = {"userbase64","user"})
public class LimitLoginController {
    public String limitLogin(Model model, @AuthenticationPrincipal User currentUser){


        model.addAttribute("user",currentUser);

        return "limitLogin";
    }

}
