package services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entities.FriendRequest;
import entities.User;

@Stateless
public class ConnectionService {
	
	 @PersistenceContext
	    private EntityManager em;

	    public void sendFriendRequest(int senderId, int receiverId) {
	        User sender = em.find(User.class, senderId);
	        User receiver = em.find(User.class, receiverId);

	        if (sender == null || receiver == null) throw new IllegalArgumentException("Invalid users.");

	        FriendRequest request = new FriendRequest();
	        request.setSender(sender);
	        request.setReceiver(receiver);
	        request.setStatus("PENDING");

	        em.persist(request);
	    }

	    public void respondToRequest(int requestId, String response) {
	        FriendRequest request = em.find(FriendRequest.class, requestId);
	        if (request == null) throw new IllegalArgumentException("Request not found.");

	        if (response.equalsIgnoreCase("ACCEPTED") || response.equalsIgnoreCase("REJECTED")) {
	            request.setStatus(response.toUpperCase());
	        }
	    }

	    public List<User> getFriends(int userId) {
	        User user = em.find(User.class, userId);
	        if (user == null) throw new IllegalArgumentException("User not found.");

	        List<User> friends = new ArrayList<>();

	        // Accepted sent requests
	        for (FriendRequest fr : user.getSentRequests()) {
	            if ("ACCEPTED".equals(fr.getStatus())) {
	                friends.add(fr.getReceiver());
	            }
	        }

	        // Accepted received requests
	        for (FriendRequest fr : user.getReceivedRequests()) {
	            if ("ACCEPTED".equals(fr.getStatus())) {
	                friends.add(fr.getSender());
	            }
	        }

	        return friends;
	    }
}
