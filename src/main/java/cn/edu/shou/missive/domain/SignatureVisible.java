package cn.edu.shou.missive.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by sqhe18 on 14-12-29.
 */

@Entity

public class SignatureVisible {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    private long processID;//
    private String officeCheckUser;
    private String depLeaderCheckUser;
    private String CheckReaderUser;
    private String composeUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProcessID() {
        return processID;
    }

    public void setProcessID(long processID) {
        this.processID = processID;
    }

    public String getOfficeCheckUser() {
        return officeCheckUser;
    }

    public void setOfficeCheckUser(String officeCheckUser) {
        this.officeCheckUser = officeCheckUser;
    }

    public String getDepLeaderCheckUser() {
        return depLeaderCheckUser;
    }

    public void setDepLeaderCheckUser(String depLeaderCheckUser) {
        this.depLeaderCheckUser = depLeaderCheckUser;
    }

    public String getCheckReaderUser() {
        return CheckReaderUser;
    }

    public void setCheckReaderUser(String checkReaderUser) {
        CheckReaderUser = checkReaderUser;
    }

    public String getComposeUser() {
        return composeUser;
    }

    public void setComposeUser(String composeUser) {
        this.composeUser = composeUser;
    }
}
