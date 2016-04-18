package cn.edu.shou.missive.domain;

import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Created by sqhe on 14-8-10.
 */
@Entity
public class Tester extends BaseEntity {

    public String name;

}
