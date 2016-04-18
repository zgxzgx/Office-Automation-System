package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by TISSOT on 2014/7/17.
 */
@Entity
@Table(name="notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter  @Setter private Long id;//通知编号

    @Getter  @Setter private String writer;//通知作者
    @Getter  @Setter private String title;//通知标题

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter  @Setter private String content;//通知文本内容

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter  @Setter private String contentHtml;//通知网页内容

    @Getter  @Setter private String time;//通知发布时间
    @Getter  @Setter private Date createTime;//创建时间
    @Getter  @Setter private Date updateTime;//更新时间
    @Getter  @Setter private String isDel;//删除标识
    //@Getter  @Setter  private boolean status; //已读标志


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "notification")
    @JsonIgnore
    private List<Notification_User> notification_users;


    public Notification() {
    }

    public Notification(Long id, String writer, String title,String time, String content,String contentHtml, Date createTime, Date updateTime, String isDel) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.contentHtml=contentHtml;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDel = isDel;
        this.time=time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public List<Notification_User> getNotification_users() {
        return notification_users;
    }

    public void setNotification_users(List<Notification_User> notification_users) {
        this.notification_users = notification_users;
    }
}

