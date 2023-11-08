package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.Event;
import cz.cvut.kbss.ear.ms.model.PollOption;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
public class PollOptionDao extends BaseDao<PollOption>{
    public PollOptionDao() {
        super(PollOption.class);
    }
    public List<PollOption> findByDateSlot(LocalDate date) {
        try{
        Objects.requireNonNull(date);
        return em.createNamedQuery("Option.findByDateSlot", PollOption.class).setParameter("dateslot", date)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PollOption findFinalForEvent(Event event) {
        try{
        Objects.requireNonNull(event);
        return em.createNamedQuery("Option.findFinalForEvent", PollOption.class).setParameter("event", event)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
