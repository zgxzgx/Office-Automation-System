package cn.edu.shou.missive.domain;
import javax.persistence.*;
import java.util.Date;
/**
 * Created by TISSOT on 2014/7/13.
 */
@Entity
@Table(name ="schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//编号
    @Column(name = "useId")
    private String user;//日程创建人
    @Column(name = "startTime")
    private String startTime;//日程开始时间
    @Column(name = "endTime")
    private String endTime;//日程结束时间
    @Column(name = "note")
    private String title;//日程名
    private String place;//地点
    private String content;//日程内容

    @Column(name = "createTime")
    private Date createTime;//创建时间
    @Column(name = "updateTime")
    private Date updateTime;//更新时间
    @Column(name = "isDel")
    private String isDel;//删除标识

    public Schedule(Long id, String user, String startTime, String endTime, String title, String place, String content, Date createTime, Date updateTime, String isDel) {
        this.id = id;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.place = place;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDel = isDel;
    }

    public Schedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

}
