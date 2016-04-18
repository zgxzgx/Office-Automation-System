package cn.edu.shou.missive.domain;

import javax.persistence.Entity;

/**
 * Created by sqhe on 14-8-13.
 */
@Entity
public class FieldType extends BaseEntity {

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
