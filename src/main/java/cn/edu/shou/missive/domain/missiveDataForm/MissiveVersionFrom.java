package cn.edu.shou.missive.domain.missiveDataForm;


import java.util.List;

/**
 * Created by sqhe on 14-7-13.
 */
public class MissiveVersionFrom extends BaseEntityForm {
    public long versionNumber;
    public String missiveTittle;
    public String docFilePath;   //doc文件位置
    public String pdfFilePath;   //相应pdf文件位置
    public List<AttachmentFrom> attachments;
    public MissiveFrom missive;

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

    public List<AttachmentFrom> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentFrom> attachments) {
        this.attachments = attachments;
    }

    public MissiveFrom getMissive() {
        return missive;
    }

    public void setMissive(MissiveFrom missive) {
        this.missive = missive;
    }
}
