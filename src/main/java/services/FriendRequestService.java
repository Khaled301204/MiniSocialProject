package services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import entities.FriendRequest;
import entities.User;

@Stateless
public class FriendRequestService {
	@PersistenceContext
    private EntityManager em;

    // Send a friend request
    public void sendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Sender and receiver must not be null.");
        }
        if (sender.getId() == receiver.getId()) {
            throw new IllegalArgumentException("Cannot send a request to yourself.");
        }

        Long count = em.createQuery(
            "SELECT COUNT(fr) FROM FriendRequest fr WHERE " +
            "(fr.sender = :sender AND fr.receiver = :receiver) OR " +
            "(fr.sender = :receiver AND fr.receiver = :sender)", Long.class)
            .setParameter("sender", sender)
            .setParameter("receiver", receiver)
            .getSingleResult();

        if (count > 0) {
            throw new IllegalStateException("Request already exists or users are already connected.");
        }

        FriendRequest fr = new FriendRequest();
        fr.setSender(sender);
        fr.setReceiver(receiver);
        fr.setStatus("PENDING");
        em.persist(fr);
    }

    // Accept a pending friend request
    public void acceptRequest(int requestId, User actingUser) {
        FriendRequest fr = em.find(FriendRequest.class, requestId);
        if (fr == null) throw new IllegalArgumentException("Request not found.");
        if (!fr.getReceiver().equals(actingUser))
            throw new SecurityException("You can't accept requests not sent to you.");
        if (!"PENDING".equals(fr.getStatus()))
            throw new IllegalStateException("Request is not pending.");

        fr.setStatus("ACCEPTED");
        em.merge(fr);
    }

    // Reject a pending friend request
    public void rejectRequest(int requestId, User actingUser) {
        FriendRequest fr = em.find(FriendRequest.class, requestId);
        if (fr == null) throw new IllegalArgumentException("Request not found.");
        if (!fr.getReceiver().equals(actingUser))
            throw new SecurityException("You can't reject requests not sent to you.");
        if (!"PENDING".equals(fr.getStatus()))
            throw new IllegalStateException("Request is not pending.");

        fr.setStatus("REJECTED");
        em.merge(fr);
    }

    // List all friends (where status is ACCEPTED)
    public List<User> getFriends(User user) {
        List<User> friends = new ArrayList<>();
        TypedQuery<FriendRequest> sent = em.createQuery(
            "SELECT fr FROM FriendRequest fr WHERE fr.sender = :user AND fr.status = 'ACCEPTED'", FriendRequest.class
        ).setParameter("user", user);

        TypedQuery<FriendRequest> received = em.createQuery(
            "SELECT fr FROM FriendRequest fr WHERE fr.receiver = :user AND fr.status = 'ACCEPTED'", FriendRequest.class
        ).setParameter("user", user);

        for (FriendRequest fr : sent.getResultList())
            friends.add(fr.getReceiver());
        for (FriendRequest fr : received.getResultList())
            friends.add(fr.getSender());
        return friends;
    }

    // List all pending requests the user needs to accept
    public List<FriendRequest> getPendingToAccept(User user) {
        return em.createQuery(
            "SELECT fr FROM FriendRequest fr WHERE fr.receiver = :user AND fr.status = 'PENDING'", FriendRequest.class
        ).setParameter("user", user).getResultList();
    }

    // List all pending requests the user has sent
    public List<FriendRequest> getPendingSent(User user) {
        return em.createQuery(
            "SELECT fr FROM FriendRequest fr WHERE fr.sender = :user AND fr.status = 'PENDING'", FriendRequest.class
        ).setParameter("user", user).getResultList();
    }

    // Find a FriendRequest by id
    public FriendRequest find(int id) {
        return em.find(FriendRequest.class, id);
    }
}
