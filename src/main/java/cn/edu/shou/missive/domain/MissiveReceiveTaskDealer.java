package cn.edu.shou.missive.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by TISSOT on 2014/9/19.
 */
@Entity
@Table(name="MissiveReceiveTaskDealer")
public class MissiveReceiveTaskDealer extends BaseEntity {
    private Long instanceId;

    private String OfficeRegist;//办公室登记人
    private String OfficeHandle;//办公室拟办人
    private String ChargerCheck;//办公室审核人
    private String LeaderSign;//领导批示
    private String Reado;//阅办人
    private String OfficeDispose;//办公室处理
    private String UndertakeDispose;//承办后处理
    private String ReadoDispose;//阅办后处理

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getOfficeRegist() {
        return OfficeRegist;
    }

    public void setOfficeRegist(String officeRegist) {
        OfficeRegist = officeRegist;
    }

    public String getOfficeHandle() {
        return OfficeHandle;
    }

    public void setOfficeHandle(String officeHandle) {
        OfficeHandle = officeHandle;
    }

    public String getChargerCheck() {
        return ChargerCheck;
    }

    public void setChargerCheck(String chargerCheck) {
        ChargerCheck = chargerCheck;
    }

    public String getLeaderSign() {
        return LeaderSign;
    }

    public void setLeaderSign(String leaderSign) {
        LeaderSign = leaderSign;
    }

    public String getReado() {
        return Reado;
    }

    public void setReado(String reado) {
        Reado = reado;
    }

    public String getOfficeDispose() {
        return OfficeDispose;
    }

    public void setOfficeDispose(String officeDispose) {
        OfficeDispose = officeDispose;
    }

    public String getUndertakeDispose() {
        return UndertakeDispose;
    }

    public void setUndertakeDispose(String undertakeDispose) {
        UndertakeDispose = undertakeDispose;
    }

    public String getReadoDispose() {
        return ReadoDispose;
    }

    public void setReadoDispose(String readoDispose) {
        ReadoDispose = readoDispose;
    }
}
