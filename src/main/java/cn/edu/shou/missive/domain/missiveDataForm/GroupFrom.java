package cn.edu.shou.missive.domain.missiveDataForm;


import java.util.List;

/**
 * Created by sqhe on 14-7-12.
 */
public class GroupFrom extends BaseEntityForm {
    public String groupName;
    public String description;

    public List<UserFrom> users;

    public boolean isDel;

    public List<GroupFrom> groupList;

    public GroupFrom group;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserFrom> getUsers() {
        return users;
    }

    public void setUsers(List<UserFrom> users) {
        this.users = users;
    }

    public boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(boolean isDel) {
        this.isDel = isDel;
    }

    public List<GroupFrom> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupFrom> groupList) {
        this.groupList = groupList;
    }

    public GroupFrom getGroup() {
        return group;
    }

    public void setGroup(GroupFrom group) {
        this.group = group;
    }
}
