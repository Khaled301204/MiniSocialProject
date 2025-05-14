//package services;
//
//import javax.annotation.Resource;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import javax.jms.ConnectionFactory;
//import javax.jms.JMSContext;
//import javax.jms.MapMessage;
//import javax.jms.Queue;
//
//import notification.NotificationEvent;
//
//@Stateless
//public class NotificationProducer {
//	
//	@Inject
//    private JMSContext jmsContext;
//	
//	@Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
//    private ConnectionFactory cf;
//
//    @Resource(lookup = "java:/jms/queue/NotificationsQueue")
//    private Queue queue;
//    
//    public void sendNotification(NotificationEvent event) {
//    	 try {
//    	        MapMessage mapMessage = jmsContext.createMapMessage();
//    	        mapMessage.setString("eventType", event.getEventType());
//    	        mapMessage.setObject("user", event.getUser());
//    	        mapMessage.setString("message", event.getMessage());
//    	        jmsContext.createProducer().send(queue, mapMessage);
//    	 }
//    	 catch (Exception e) {
//    	        e.printStackTrace();
//    	    }
//    }
//}
