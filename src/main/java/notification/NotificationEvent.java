package notification;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
	
    private String eventType;
    private int userId;
    private String userName;
    private String message;

    public NotificationEvent() {}

    public NotificationEvent(String eventType, int userId, String userName, String message) {
        this.eventType = eventType;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
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
    public String getUserName() { 
    	return userName; 
    	}
    public void setUserName(String userName) { 
    	this.userName = userName; 
    	}
    public String getMessage() { 
    	return message; 
    	}
    public void setMessage(String message) { 
    	this.message = message; 
    	}
}