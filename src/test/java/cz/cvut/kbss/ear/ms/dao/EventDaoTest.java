package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.ms.dao.EventDao;
import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.model.Event;
import cz.cvut.kbss.ear.ms.model.PollOption;
import cz.cvut.kbss.ear.ms.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class)
@ActiveProfiles("test")
public class EventDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private EventDao sut;

    @Test
    public void findAllOpened(){
        List<Event> events = new ArrayList<Event>();
        List<Event> openedEvents = new ArrayList<Event>();
        for(int i=0; i<6; i++){
            Event e = Generator.generateEvent();
            events.add(e);
            em.persist(e);
            em.persist(e.getOwner());
            for(PollOption p: e.getOptions()){
                em.persist(p);
            }
            assertNotNull(e);
        }
        for(Event e: events){
            if(e.getOpenDueTo().isAfter(LocalDate.now())){
                openedEvents.add(e);
            }
        }

        List<Event> result = sut.findAllOpened();
        assertEquals(openedEvents.size(), result.size());
        assertEquals(openedEvents, result);

    }

    @Test
    public void findAllOpenedForOneOwner(){
        User owner = Generator.generateUser();
        em.persist(owner);
        List<Event> eventsFroOwner = new ArrayList<Event>();
        List<Event> openedEvents = new ArrayList<Event>();
        for(int i=0; i<10; i++){
            Event e = Generator.generateEvent();
            if(i%2==0){
                e.setOwner(owner);
                eventsFroOwner.add(e);
            }else{
                em.persist(e.getOwner());
            }
            em.persist(e);
            for(PollOption p: e.getOptions()){
                em.persist(p);
            }
            assertNotNull(e);

        }
        for(Event e: eventsFroOwner){
            if(e.getOpenDueTo().isAfter(LocalDate.now())){
                openedEvents.add(e);
            }
        }
        List<Event> result = sut.findAllOpenedForOneOwner(owner);
        assertEquals(openedEvents.size(), result.size());
        assertEquals(openedEvents, result);
    }

}
