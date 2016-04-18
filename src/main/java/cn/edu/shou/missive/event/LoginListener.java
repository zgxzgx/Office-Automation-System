package cn.edu.shou.missive.event;

import cn.edu.shou.missive.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * Created by sqhe on 15/2/3.
 * The Authenication Success Listener
 * When user authenicaten success add User object to session
 */
@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {


    @Autowired
    private HttpSession httpSession;
    @Autowired
    private UserDAO userDAO;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event)
    {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        httpSession.setAttribute("user",userDAO.findByUserName(userDetails.getUsername()));

    }


}
