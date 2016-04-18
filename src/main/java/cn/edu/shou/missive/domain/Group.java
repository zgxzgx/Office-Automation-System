package cn.edu.shou.missive.domain;

import cn.edu.shou.missive.json.JodaTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-7-12.
 */
@Entity
@Table(name = "Groups")
public class Group extends BaseEntity {

    @Getter @Setter public String groupName;
    @Getter @Setter private String description;
    @Getter @Setter private String abbrevname;
    public String abbrev;


    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="group")
    @JsonIgnore
    private List<User> users;


    @Getter @Setter public boolean isDel;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy="group")
    @JsonIgnore
    private List<Group> groupList;



    /*@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="missivePublish_mainSendGroups", joinColumns={@JoinColumn(name="GroupId")}, inverseJoinColumns={@JoinColumn(name="missivePublishId")})
    @Getter @Setter
    @JsonIgnore
    private List<MissivePublish> MainSendPublish;   //主送部门的发文

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="missivePublish_copytoGroups", joinColumns={@JoinColumn(name="GroupId")}, inverseJoinColumns={@JoinColumn(name="missivePublishId")})
    @Getter @Setter
    @JsonIgnore
    private List<MissivePublish> CopytoPublish;   //抄送部门的发文*/

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parentId")
    @JsonIgnore
    public Group group;



    public boolean getHasGroups() {
        if(this.groupList!=null&&this.groupList.size()>0)
            return true;
        else
            return false;
    }



    //private boolean hasGroups;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAbbrevname() {
        return abbrevname;
    }

    public void setAbbrevname(String abbrevname) {
        this.abbrevname = abbrevname;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }



    public boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(boolean isDel) {
        this.isDel = isDel;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}



