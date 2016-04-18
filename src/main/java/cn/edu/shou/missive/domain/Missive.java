package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


/**
 * Created by sqhe on 14-7-7.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Missive extends BaseEntity{

    @Getter @Setter public String name;
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="missive")
    @Getter @Setter public List<MissiveVersion> versions;//版本號

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="missiveTypeId")
    @Getter @Setter public MissiveType missiveType;//公文類型

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="urgentLevelId")
    @Getter @Setter public UrgentLevel urgentLevel;//紧急程度

    @ManyToMany
    @JoinTable(name="missive_copyToUsers", joinColumns={@JoinColumn(name="missiveId")}, inverseJoinColumns={@JoinColumn(name="UserId")})
    public List<User> copyToUsers;//主送与抄送人员

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="secretLevelId")
    @Getter @Setter public SecretLevel secretLevel;//密級

    @Getter @Setter public String missiveNum=null;//公文號

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="MissiveCreateUserId")
    @Getter @Setter public User MissiveCreateUser;



    //private User createUser;

    public Missive() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MissiveVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<MissiveVersion> versions) {
        this.versions = versions;
    }

    public MissiveType getMissiveType() {
        return missiveType;
    }

    public void setMissiveType(MissiveType missiveType) {
        this.missiveType = missiveType;
    }

    public SecretLevel getSecretLevel() {
        return secretLevel;
    }

    public UrgentLevel getUrgentLevel() {
        return urgentLevel;
    }

    public void setUrgentLevel(UrgentLevel urgentLevel) {
        this.urgentLevel = urgentLevel;
    }

    public void setSecretLevel(SecretLevel secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getMissiveNum() {
        return missiveNum;
    }

    public void setMissiveNum(String missiveNum) {
        this.missiveNum = missiveNum;
    }

    public User getMissiveCreateUser() {
        return MissiveCreateUser;
    }

    public void setMissiveCreateUser(User missiveCreateUser) {
        MissiveCreateUser = missiveCreateUser;
    }

    public List<User> getCopyToUsers() {
        return copyToUsers;
    }

    public void setCopyToUsers(List<User> copyToUsers) {
        this.copyToUsers = copyToUsers;
    }

    @Override
    public String toString() {
        return "Missive{" +
                "name='" + name + '\'' +
                ", versions=" + versions +
                ", missiveType=" + missiveType +
                ", copyToUsers=" + copyToUsers +
                ", secretLevel=" + secretLevel +
                ", missiveNum='" + missiveNum + '\'' +
                ", MissiveCreateUser=" + MissiveCreateUser +
                '}';
    }
}
