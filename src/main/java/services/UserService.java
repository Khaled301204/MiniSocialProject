package services;



import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.WebApplicationException;

import entities.User;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;

    public User addUser(User user) {
    	if (isEmailTaken(user.getEmail())) {
            throw new WebApplicationException("Email already in use", 400);
        }
        em.persist(user);
        return em.find(User.class, user.getId());
    }
    
    public boolean isEmailTaken(String email) {
        return !em.createQuery("SELECT u FROM User u WHERE u.Email = :Email", User.class)
                  .setParameter("Email", email)
                  .getResultList().isEmpty();
    }
    
    public User login(String email, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.Email = :Email AND u.Password = :Password",User.class)
                    .setParameter("Email", email)
                    .setParameter("Password", password) 
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new WebApplicationException("Invalid email or password", 401);
        }
    }

    public User getUserById(int id) {
        return em.find(User.class, id);
    }

    public List<User> getAllUsers() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        cq.from(User.class);
        return em.createQuery(cq).getResultList();
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
}
