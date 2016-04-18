package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SecretLevel extends BaseEntity{
    public String secretLevelName;


    /*@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="secretLevel")
    public List<Missive> missives;*/

    public String getSecretLevelName() {
        return secretLevelName;
    }

    public void setSecretLevelName(String secretLevelName) {
        this.secretLevelName = secretLevelName;
    }

    /*public List<Missive> getMissives() {
        return missives;
    }

    public void setMissives(List<Missive> missives) {
        this.missives = missives;
    }*/
}
