package cn.edu.shou.missive.domain.missiveDataForm;

/**
 * Created by TISSOT on 2014/11/5.
 */
public class NextTask {
    private String text;
    private String variableName;
    private String value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
