//package services;
//
//import notification.NotificationEvent;
//
//import javax.annotation.Resource;
//import javax.inject.Inject;
//import javax.jms.*;
//import javax.ejb.MessageDriven;
//import javax.ejb.ActivationConfigProperty;
//import javax.jms.MapMessage;
//import javax.jms.Message;
//import javax.jms.MessageListener;
//
//import entities.User;
//
//@MessageDriven(
//	    activationConfig = {
//	        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationsQueue"),
//	        @ActivationConfigProperty(propertyName = "destinationType",       propertyValue = "javax.jms.Queue"),
//	        @ActivationConfigProperty(propertyName = "acknowledgeMode",       propertyValue = "Auto-acknowledge")
//	    }
//	)public class NotificationConsumer implements MessageListener{
//
//    @Inject
//    private JMSContext jmsContext;
//
//    @Resource(lookup = "java:/jms/queue/NotificationQueue")
//    private Queue notificationQueue;
//
//    @Override
//    public void onMessage(Message message) {
//    	if (!(message instanceof MapMessage)) {
//    		return;
//    	}
//    	try {
//    	MapMessage m = (MapMessage) message;
//    	User user = (User)m.getObject("user");
//    	String eventType = m.getString("eventType");
//    	String msgcontent = m.getString("message");
//    	NotificationEvent notification = new NotificationEvent(eventType,user,msgcontent);
//    	}
//    	catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}