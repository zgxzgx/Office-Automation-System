package cn.edu.shou.missive.domain;

/**
 * Created by sqhe on 15/2/3.
 */
public class SignatureEvent {

    private String eventType;
    private String message;
    private String imageBase64;
    private String id;

    public SignatureEvent() {

    }

    public SignatureEvent(String eventType, String message) {
        this.eventType = eventType;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SignatureEvent{" +
                "id="+ id + '\'' +
                "eventType='" + eventType + '\'' +
                ", message='" + message + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
