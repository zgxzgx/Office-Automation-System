package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by zgx on 2015/6/29.
 */
@Entity
public class publish {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Getter
    @Setter
    public String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
