package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by sqhe on 14-8-7.
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class UrgentLevel extends BaseEntity {

    private String value;


    private boolean isDel;

    @OneToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="urgentLevel")
    private List<Missive> missives;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean isDel) {
        this.isDel = isDel;
    }

    public List<Missive> getMissives() {
        return missives;
    }

    public void setMissives(List<Missive> missives) {
        this.missives = missives;
    }
}
