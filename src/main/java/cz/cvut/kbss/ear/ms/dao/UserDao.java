package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.Event;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.model.Vote;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class UserDao extends AccountDao<User>{
    public UserDao() {
        super(User.class);
    }
    public User findByEmail(String email) {
        try {
            User u = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
                    .getSingleResult();
            return u;
        } catch (NoResultException e) {
            return null;
        }
    }
    public User findUserByVoteOnEvent(Event event, Vote vote) {
        try {
            User user = null;
            List<User> votes = em.createNamedQuery("User.findUserByVote", User.class).setParameter("vote", vote)
                    .getResultList();
            List<User> guestevents = em.createNamedQuery("User.findGuestEvent", User.class).setParameter("event", event)
                    .getResultList();
            for(User u: votes){
                for(User g :guestevents){
                    if(u.getId() == g.getId()){
                        user = u;
                    }
                }
            }
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }
}
