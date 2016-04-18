package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by seky on 14-7-28.
 */
@Entity
public class ACT_RU_TASK {
    public String getID_() {
        return ID_;
    }
    public void setID_(String ID_) {
        this.ID_ = ID_;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Getter  @Setter private String ID_;//任务ID

    public String getPROC_INST_ID_() {
        return PROC_INST_ID_;
    }

    public void setPROC_INST_ID_(String PROC_INST_ID_) {
        this.PROC_INST_ID_ = PROC_INST_ID_;
    }

    @Getter  @Setter private String PROC_INST_ID_;//流程实例ID

    public String getNAME_() {
        return NAME_;
    }

    public void setNAME_(String NAME_) {
        this.NAME_ = NAME_;
    }

    @Getter  @Setter private String NAME_;//任务名称
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Getter @Setter private DateTime CREATE_TIME_;



    @Getter @Setter private String ASSIGNEE_;

    public String getASSIGNEE_() {
        return ASSIGNEE_;
    }

    public void setASSIGNEE_(String ASSIGNEE_) {
        this.ASSIGNEE_ = ASSIGNEE_;
    }
}
