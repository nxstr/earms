package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class)
@ActiveProfiles("test")
public class UserDaoTest {
    @Autowired
    private TestEntityManager em;

    @Qualifier("userDao")
    @Autowired
    private UserDao sut;

    @Test
    public void findByEmailFindsUser(){
        final User cat = Generator.generateUser();
        em.persist(cat);
        assertNotNull(cat.getId());

        final User result = sut.findByEmail(cat.getEmail());
        assertNotNull(result);
        assertEquals(cat.getId(), result.getId());
    }

    @Test
    public void findUserByVoteOnEvent(){
        User user  = Generator.generateUser();
        em.persist(user);
        Event event = Generator.generateEvent();
        event.setOwner(user);
        User guest = Generator.generateUser();
        em.persist(guest);
        Set<User> gg = new HashSet<User>();
        gg.add(guest);
        event.setGuests(gg);
        em.persist(event);
        Set<Event> ev = new HashSet<Event>();
        ev.add(event);
        guest.setGuestEvents(ev);
        em.merge(guest);
        PollOption pollOption = null;
        for(PollOption d: event.getOptions()){
            em.persist(d);
            pollOption = d;
        }
        Vote vote = new Vote(guest, pollOption);
        em.persist(vote);

        User res = sut.findUserByVoteOnEvent(event, vote);
        assertNotNull(res.getId());
        assertEquals(guest.getId(), res.getId());
    }
}
