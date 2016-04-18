package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

/**
 * Created by sqhe on 14-8-11.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

//    @Autowired
//    private UserDAO userDAO;

    @Override
    public User getCurrentAuditor() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()==User.class )
        {
            return (User) temp;

            //return userDAO.findByUserName(myUser.getUsername());
        }else
            return null;
    }
}
