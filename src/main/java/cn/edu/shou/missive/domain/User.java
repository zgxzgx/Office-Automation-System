package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-7-12.
 */
@Entity
@Table(name = "users")

//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class User extends BaseEntity implements UserDetails   {



    @Column(name = "username", unique = true)
    public String userName;
    public String Name;
    public String sex;
    public String tel;
    public String email;
    public String password;
    public String abrev;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public String imagePath;

    public Date lastLoginTime;
    public boolean enabled;
    public String description;



    public int delaynum;//设定延时的时间，一小时为单元
    public String delayWarm;//流程超过设定延时，将以何种形式通知，m:代表短信，e代表邮件，n:代表不提示
    public boolean emailSend;//是否发送邮件
    public boolean msgSend;//是否发送短信

    public boolean padEnable;
    public boolean isDepLeader;

    //2014/12/14 郑小罗
    //public byte[] signImg;//签名字符

    @Column(columnDefinition = "LONGBLOB")
    public byte[] signImg;//签名字符


    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    @JsonManagedReference
    public Group group;

    /*@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "authority_id")
    @JsonManagedReference
    public Authority authority;*/


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "user")
    @JsonManagedReference
    private List<Authorities> authoritiesList;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAbrev() {
        return abrev;
    }

    public void setAbrev(String abrev) {
        this.abrev = abrev;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isPadEnable() {
        return padEnable;
    }

    public void setPadEnable(boolean padEnable) {
        this.padEnable = padEnable;
    }


    /*public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }*/

    public List<Authorities> getAuthoritiesList() {
        return authoritiesList;
    }

    public void setAuthoritiesList(List<Authorities> authoritiesList) {
        this.authoritiesList = authoritiesList;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.getAuthoritiesList();
    }

    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return this.userName;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid
     * (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {

        return this.enabled;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer
     * valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public boolean getMsgSend() {
        return msgSend;
    }

    public void setMsgSend(boolean msgSend) {
        this.msgSend = msgSend;
    }

    public boolean getEmailSend() {
        return emailSend;
    }

    public void setEmailSend(boolean emailSend) {
        this.emailSend = emailSend;
    }

    public String getDelayWarm() {
        return delayWarm;
    }

    public void setDelayWarm(String delayWarm) {
        this.delayWarm = delayWarm;
    }

    public int getDelaynum() {
        return delaynum;
    }

    public void setDelaynum(int delaynum) {
        this.delaynum = delaynum;
    }

    public byte[] getSignImg() {
        return signImg;
    }

    public void setSignImg(byte[] signImg) {
        this.signImg = signImg;
    }

    public boolean isAdmin()
    {
        for (Authorities auth : this.authoritiesList)
        {
            if(auth.getAuthority().toUpperCase().equals("ADMIN"))
                return true;
        }
        return false;
    }

    public boolean isEmailSend() {
        return emailSend;
    }

    public boolean isMsgSend() {
        return msgSend;
    }

    public boolean isDepLeader() {
        return isDepLeader;
    }

    public void setDepLeader(boolean isDepLeader) {
        this.isDepLeader = isDepLeader;
    }
}
