package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.Event;
import cz.cvut.kbss.ear.ms.model.PollOption;
import cz.cvut.kbss.ear.ms.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class EventDao extends BaseDao<Event>{
    public EventDao() {
        super(Event.class);
    }

    public List<Event> findAllOpened() {
        try{
        LocalDate today = LocalDate.now();
        return em.createNamedQuery("Event.findByOpenDueTo", Event.class).setParameter("openDueTo", today).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Event> findAllOpenedForOneOwner(User owner){
        try{
        List<Event> allOpened = findAllOpened();
        List<Event> allOpenedForOwner = new ArrayList<>();
        for(Event e: allOpened){
            if(e.getOwner() == owner){
                allOpenedForOwner.add(e);
            }
        }
        return allOpenedForOwner;
        } catch (NoResultException e) {
            return null;
        }
    }

}
