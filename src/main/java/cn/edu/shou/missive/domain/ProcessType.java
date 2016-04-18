package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by sqhe on 14-7-8.
 */
@Entity
public class ProcessType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Transient
    private String DefinitionId;

    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public  ProcessType(){}

    public ProcessType(String name){

        this.name=name;

    }

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="processType")
    private List<MissiveField> missiveFieldList;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="processType")
    private List<TaskName> taskNameList;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="processType")
    private List<MissiveFieldAll> missiveFieldAllList;


}
