package cn.edu.shou.missive.domain.missiveDataForm;

public class AttachmentFrom extends BaseEntityForm {

    public String attachmentTittle;
    public String attachmentFilePath;
    public boolean isOriginalFile;
    public MissiveVersionFrom missiveVersion;

    public boolean isOriginalFile() {
        return isOriginalFile;
    }

    public void setOriginalFile(boolean isOriginalFile) {
        this.isOriginalFile = isOriginalFile;
    }

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

    public MissiveVersionFrom getMissiveVersion() {
        return missiveVersion;
    }

    public void setMissiveVersion(MissiveVersionFrom missiveVersion) {
        this.missiveVersion = missiveVersion;
    }
}
