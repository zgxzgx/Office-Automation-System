package cn.edu.shou.missive.domain;
import javax.persistence.*;
import java.util.Date;
/**
 * Created by TISSOT on 2014/7/13.
 */
@Entity
@Table(name ="UploadFlag")
public class UploadFlag   {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//编号
    @Column(name = "useId")
    private String user;//日程创建人


    private String instanceId;
    private String content;

    @Column(name = "createTime")
    private Date createTime;//创建时间

    @Column(name = "isDel")
    private String isDel;//识

    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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


}
