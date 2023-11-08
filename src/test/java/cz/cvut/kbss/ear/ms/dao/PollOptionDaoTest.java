package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.model.PollOption;
import cz.cvut.kbss.ear.ms.model.Event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class)
@ActiveProfiles("test")
public class PollOptionDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private PollOptionDao sut;

    @Test
    public void findAllPollOptionsByDateSlot(){
        final Event event = Generator.generateEvent();
        em.persist(event);
        em.persist(event.getOwner());
        PollOption firstD = null;
        final Set<PollOption> dates1 = new HashSet<>();
        for(PollOption d: event.getOptions()){
            em.persist(d);
            dates1.add(d);
            firstD = d;
        }
        final Set<PollOption> dates = new HashSet<>();

        for(int i=0 ;i<5;i++){
            PollOption d = Generator.generatePollOption(event);
            dates.add(d);
            em.persist(d);
        }
        for(PollOption d: dates){
            if(Objects.equals(d.getDateSlot(), firstD.getDateSlot())){
                dates1.add(d);
            }
        }

        final List<PollOption> res = sut.findByDateSlot(firstD.getDateSlot());
        Set<PollOption> result = new HashSet<>(res);
        assertEquals(dates1.size(), result.size());
        assertThat(dates1.equals(result), is(true));
    }


    @Test
    public void findAllPollOptionsForEvent(){
        final Event event = Generator.generateEvent();
        em.persist(event);
        PollOption finalOp = null;
        final List<PollOption> dates = new ArrayList<>();
        for(PollOption d: event.getOptions()){
            em.persist(d);
        }
        for(int i=0 ;i<5;i++){
            PollOption d = Generator.generatePollOption(event);
            if(i==3){
                d.setIsFinal(true);
                finalOp = d;
            }
            dates.add(d);
            em.persist(d);
        }
        event.setOptions(dates);
        em.merge(event);
        final PollOption res = sut.findFinalForEvent(event);
        assertEquals(finalOp.getId(), res.getId());
    }
}
