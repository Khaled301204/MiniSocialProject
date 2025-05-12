package notification;

import java.io.Serializable;
import java.util.Map;

public class NotificationEvent implements Serializable {
    private String eventType;
    private int userId;
    private String message;
    private Map<String, Object> details;

    public NotificationEvent() {}

    public NotificationEvent(String eventType, int userId, String message, Map<String, Object> details) {
        this.eventType = eventType;
        this.userId = userId;
        this.message = message;
        this.details = details;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    // Manual JSON serialization (simple, not robust for complex nested objects)
    public String toJsonString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"eventType\":\"").append(escape(eventType)).append("\",");
        sb.append("\"userId\":").append(userId).append(",");
        sb.append("\"message\":\"").append(escape(message)).append("\",");
        sb.append("\"details\":{");
        if (details != null) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : details.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escape(entry.getKey())).append("\":");
                Object value = entry.getValue();
                if (value instanceof Number || value instanceof Boolean) {
                    sb.append(value);
                } else {
                    sb.append("\"").append(escape(String.valueOf(value))).append("\"");
                }
                first = false;
            }
        }
        sb.append("}}");
        return sb.toString();
    }

    // Basic JSON string escaper
    private String escape(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"");
    }
}