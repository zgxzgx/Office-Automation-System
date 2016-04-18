package cn.edu.shou.missive.domain;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Attachment extends BaseEntity {

    @Getter
    @Setter
    public String attachmentTittle;
    @Getter
    @Setter
    private String attachmentFilePath;

    public boolean isOriginalFile;


    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="miisiveVersionId")
    private MissiveVersion missiveVersion;

    public String getAttachmentTittle() {
        return attachmentTittle;
    }

    public void setAttachmentTittle(String attachmentTittle) {
        this.attachmentTittle = attachmentTittle;
    }

    public String getAttachmentFilePath() {
        return attachmentFilePath;
    }

    public void setAttachmentFilePath(String attachmentFilePath) {
        this.attachmentFilePath = attachmentFilePath;
    }

    public MissiveVersion getMissiveVersion() {
        return missiveVersion;
    }

    public void setMissiveVersion(MissiveVersion missiveVersion) {
        this.missiveVersion = missiveVersion;
    }

    public boolean isOriginalFile() {
        return isOriginalFile;
    }

    public void setOriginalFile(boolean isOriginalFile) {
        this.isOriginalFile = isOriginalFile;
    }
}
