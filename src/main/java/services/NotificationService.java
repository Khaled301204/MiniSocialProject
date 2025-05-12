package services;

import notification.NotificationEvent;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;

@ApplicationScoped
public class NotificationService {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;

    public void sendEvent(NotificationEvent event) {
        try {
            // Use manual toJsonString implementation from the event class
            String json = event.toJsonString();
            jmsContext.createProducer().send(notificationQueue, json);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}