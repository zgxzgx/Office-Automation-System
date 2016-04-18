package cn.edu.shou.missive.domain.missiveDataForm;


/**
 * Created by sqhe on 14-7-12.
 */
public class UserFrom {

    public long id;

    public String userName;
    public String Name;
    public String  sex;
    public String tel;
    public String email;

    public String password;

    public String imagePath;

    public String lastLoginTime;
    public boolean enabled;
    public String description;
    public GroupFrom group;
    public boolean isDepLeader;



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

    public String getPassword() {
        return password;
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

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
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

    public GroupFrom getGroup() {
        return group;
    }

    public void setGroup(GroupFrom group) {
        this.group = group;
    }

    public boolean isDepLeader() {
        return isDepLeader;
    }

    public void setDepLeader(boolean isDepLeader) {
        this.isDepLeader = isDepLeader;
    }
}
