package services;

import notification.NotificationEvent;

import javax.annotation.Resource;
import javax.jms.*;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;


@MessageDriven(
	    activationConfig = {
	        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationsQueue"),
	        @ActivationConfigProperty(propertyName = "destinationType",       propertyValue = "javax.jms.Queue"),
	        @ActivationConfigProperty(propertyName = "acknowledgeMode",       propertyValue = "Auto-acknowledge")
	    }
	)public class NotificationConsumer implements MessageListener{


    @Resource(lookup = "java:/jms/queue/NotificationsQueue")
    private Queue notificationQueue;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof MapMessage)) {
            return;
        }
        try {
            MapMessage m = (MapMessage) message;
            String eventType = m.getString("eventType");
            int userId = m.getInt("userId");
            String userName = m.getString("userName");
            String msgcontent = m.getString("message");
            NotificationEvent notification = new NotificationEvent(eventType, userId, userName, msgcontent);
            // Optionally print
            System.out.println("Notification received for " + notification.getUserName() + ": " + notification.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}