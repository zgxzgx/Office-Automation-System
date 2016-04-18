package cn.edu.shou.missive.domain;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class CommentContent extends BaseEntity{
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter @Setter public String Base30url;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter @Setter public String Imageurl;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter @Setter public String JsignatureBase30url;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter @Setter public String JsignatureImageurl;



    @Getter @Setter public String contentText;
    @Getter @Setter
    @ManyToMany
    @JoinTable(name="CommentContent_signUser", joinColumns={@JoinColumn(name="contentPublishId")}, inverseJoinColumns={@JoinColumn(name="userId")})
    public List<User> ContentUsers;   //

    @Getter @Setter public boolean enabled;

    public String getBase30url() {
        return Base30url;
    }

    public void setBase30url(String base30url) {
        Base30url = base30url;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public String getJsignatureBase30url() {
        return JsignatureBase30url;
    }

    public void setJsignatureBase30url(String jsignatureBase30url) {
        JsignatureBase30url = jsignatureBase30url;
    }

    public String getJsignatureImageurl() {
        return JsignatureImageurl;
    }

    public void setJsignatureImageurl(String jsignatureImageurl) {
        JsignatureImageurl = jsignatureImageurl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public List<User> getContentUsers() {
        return ContentUsers;
    }

    public void setContentUsers(List<User> contentUsers) {
        ContentUsers = contentUsers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
