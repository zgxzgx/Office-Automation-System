package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;



import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-7-18.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MissiveType extends BaseEntity{

    @Getter @Setter public String typeName;



    /*@OneToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="missiveType")
    @JsonIgnore
    private List<Missive> missives;*/
    @Getter @Setter public boolean isDeleted;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

   /* public List<Missive> getMissives() {
        return missives;
    }

    public void setMissives(List<Missive> missives) {
        this.missives = missives;
    }*/

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
