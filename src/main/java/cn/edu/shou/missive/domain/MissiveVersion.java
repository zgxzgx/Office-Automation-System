package cn.edu.shou.missive.domain;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-7-13.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MissiveVersion extends BaseEntity{

    @Getter
    @Setter
    public long versionNumber;
    @Getter
    @Setter
    private String missiveTittle;
    @Getter
    @Setter
    private String docFilePath;   //doc文件位置
    @Getter
    @Setter
    private String pdfFilePath;   //相应pdf文件位置

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="missiveVersion")
    public List<Attachment> attachments;

    @Getter
    @Setter

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="missiveId")
    public Missive missive;

    public long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getMissiveTittle() {
        return missiveTittle;
    }

    public void setMissiveTittle(String missiveTittle) {
        this.missiveTittle = missiveTittle;
    }

    public String getDocFilePath() {
        return docFilePath;
    }

    public void setDocFilePath(String docFilePath) {
        this.docFilePath = docFilePath;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Missive getMissive() {
        return missive;
    }

    public void setMissive(Missive missive) {
        this.missive = missive;
    }
}
