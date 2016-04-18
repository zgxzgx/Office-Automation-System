package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by hy on 2014/12/19.
 */
@Entity
public class DIYphase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String phaseContent;

    @ManyToOne(cascade=CascadeType.DETACH,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhaseContent() {
        return phaseContent;
    }

    public void setPhaseContent(String phaseContent) {
        this.phaseContent = phaseContent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
