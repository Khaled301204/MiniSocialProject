//package notification;
//
//import java.io.Serializable;
//import java.util.Map;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//
//import entities.User;
//
//@Entity
//public class NotificationEvent implements Serializable {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;
//	
//    private String eventType;
//    
//    @ManyToOne
//    @JoinColumn(name = "userId")
//    private User user;
//    
//    private String message;
//    
//    public NotificationEvent() {}
//
//    public NotificationEvent(String eventType, User user, String message) {
//        this.eventType = eventType;
//        this.user = user;
//        this.message = message;
//    }
//
//    public String getEventType() {
//        return eventType;
//    }
//
//    public void setEventType(String eventType) {
//        this.eventType = eventType;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User userId) {
//        this.user = userId;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//}