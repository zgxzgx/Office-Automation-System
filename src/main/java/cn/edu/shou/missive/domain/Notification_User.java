package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonView;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

/**
 * Created by jiliwei on 2014/8/30.
 */

@Entity
@Table(name="notification_user")
//@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","operations","roles","menus"})
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
@Proxy(lazy = false)
public class Notification_User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    public long id;

    @Getter
    @Setter
    @ManyToOne
   // @JsonIgnore
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Getter
    @Setter
    @ManyToOne
   // @JsonIgnore
    private Notification notification;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private boolean status; //已读标志

}
