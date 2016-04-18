package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;


/**
 * Created by sqhe on 14-7-7.
 */
public class MissiveFrom extends BaseEntityForm {
    public String name;
    public List<MissiveVersionFrom> versions;//版本號
    public MissiveTypeFrom missiveType;//公文類型
    public UrgentLevelForm urgentLevel;
    public SecretLevelFrom secretLevel;//密級
    public String missiveNum;//公文號
    public String missiveNum1;//签报已拆分公文號
    public UserFrom MissiveCreateUser;
    public List<UserFrom> copyToUsers;//主送与抄送人员

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MissiveVersionFrom> getVersions() {
        return versions;
    }

    public void setVersions(List<MissiveVersionFrom> versions) {
        this.versions = versions;
    }

    public MissiveTypeFrom getMissiveType() {
        return missiveType;
    }

    public void setMissiveType(MissiveTypeFrom missiveType) {
        this.missiveType = missiveType;
    }

    public UrgentLevelForm getUrgentLevel() {
        return urgentLevel;
    }

    public void setUrgentLevel(UrgentLevelForm urgentLevel) {
        this.urgentLevel = urgentLevel;
    }

    public SecretLevelFrom getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(SecretLevelFrom secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getMissiveNum() {
        return missiveNum;
    }

    public void setMissiveNum(String missiveNum) {
        this.missiveNum = missiveNum;
    }

    public UserFrom getMissiveCreateUser() {
        return MissiveCreateUser;
    }

    public void setMissiveCreateUser(UserFrom missiveCreateUser) {
        MissiveCreateUser = missiveCreateUser;
    }

    public List<UserFrom> getCopyToUsers() {
        return copyToUsers;
    }

    public void setCopyToUsers(List<UserFrom> copyToUsers) {
        this.copyToUsers = copyToUsers;
    }

    public String getMissiveNum1() {
        return missiveNum1;
    }

    public void setMissiveNum1(String missiveNum1) {
        this.missiveNum1 = missiveNum1;
    }
}
