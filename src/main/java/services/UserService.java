package services;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.WebApplicationException;

import entities.FriendRequest;
import entities.User;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;

    public User registerUser(User user) {
    	if (isEmailTaken(user.getEmail())) {
            throw new WebApplicationException("Email already in use", 400);
        }
        em.persist(user);
        return em.find(User.class, user.getId());
    }
    
    public boolean isEmailTaken(String email) {
        return !em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                  .setParameter("email", email)
                  .getResultList().isEmpty();
    }
    
    public User login(String email, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password",User.class)
                    .setParameter("email", email)
                    .setParameter("password", password) 
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new WebApplicationException("Invalid email or password", 401);
        }
    }

    public User getUserById(int id) {
        return em.find(User.class, id);
    }
    
    public Map<String, Object> getUserMapById(int id) {
        User user = em.find(User.class, id);
        if (user == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("password", user.getPassword());
        map.put("bio", user.getBio());
        map.put("role", user.getRole());
        return map;
    }

    public List<Map<String, Object>> getAllUsersMap() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        cq.from(User.class);
        List<User> users = em.createQuery(cq).getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("password", user.getPassword());
            map.put("bio", user.getBio());
            map.put("role", user.getRole());
            result.add(map);
        }
        return result;
    }

    public User updateUser(int id, User userData) {
        User user = em.find(User.class, id);
        if (user == null) return null;
        user.setEmail(userData.getEmail());
        user.setName(userData.getName());
        user.setPassword(userData.getPassword());
        user.setRole(userData.getRole());
        return em.merge(user);
    }
    
    public List<User> getFriends(User user) {
        // returns users whom this user is friends with
        List<User> friends = new ArrayList<>();
        for (FriendRequest fr : user.getSentRequests())
            if ("ACCEPTED".equals(fr.getStatus())) friends.add(fr.getReceiver());
        for (FriendRequest fr : user.getReceivedRequests())
            if ("ACCEPTED".equals(fr.getStatus())) friends.add(fr.getSender());
        return friends;
    }
}
