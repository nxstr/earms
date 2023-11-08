package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.model.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EventService sut;
    @Autowired
    private PollOptionService sut1;

    @Test
    public void addRegisteredUserAsAGuestAddsUser(){
        Event event = Generator.generateEvent();
        User user = Generator.generateUser();
        em.persist(user);
        em.persist(event.getOwner());
        em.persist(event);
        for(PollOption p: event.getOptions()){
            em.persist(p);
        }
        Set<User> eventGuests = new HashSet<>();
        eventGuests.add(user);
        sut.addRegisteredUserAsAGuestByUsername(user.getUsername(), event);
        assertEquals(eventGuests, event.getGuests());
    }

    @Test
    public void updateTest(){

//        User user = Generator.generateUser();
//        Event event = Generator.generateEvent();
//        em.persist(user);
//        event.setOwner(user);
//        em.persist(event);
//        user.setFirstName("testearno");
//        em.merge(user);
//
////        Set<Event> evs = sut.findAllActiveForOwner(user);
//        assertEquals("testearno", event.getOwner().getFirstName());

        User user = Generator.generateUser();
        Event event = Generator.generateEvent();
        em.persist(user);
        event.setOwner(user);
        em.persist(event);

        event.setName("testearno");
        em.merge(event);
        for(Event e:user.getOwnedEvents()){
            if(e.getName()=="testearno"){
                System.out.println("yes-----------------");
            }
        }
        Integer id = event.getId();
        em.remove(event);
        for(Event e:user.getOwnedEvents()){
            if(e.getId()==id){
                System.out.println("no-----------------");
            }
        }
//        assertEquals("testarno", );
    }

    @Test
    public void closeAllEventsForOneUser(){
        User user = Generator.generateUser();
        em.persist(user);
        Set<Event> evs = new HashSet<>();
        for(int i=0; i<8; i++){
            Event event = Generator.generateEvent();
            event.setOwner(user);
            if(event.getOpenDueTo().isAfter(LocalDate.now())){
                evs.add(event);
            }
            em.persist(event);
            for(PollOption p: event.getOptions()){
                em.persist(p);
            }
        }
        if(!evs.isEmpty()) {
            sut.closeAll(user);
            for (Event e : user.getOwnedEvents()) {
                assertTrue(e.getOpenDueTo().isBefore(LocalDate.now()));
            }
        }

    }

    @Test
    public void makeAutoFinalPollOptionMakesOptionFinal(){
        Random r = new Random();
        Event event = Generator.generateEvent();
        em.persist(event);
        em.persist(event.getOwner());
        event.setOptions(new ArrayList<>());
        User guest1 = Generator.generateUser();
        User guest2 = Generator.generateUser();
        em.persist(guest1);
        em.persist(guest2);
        event.setGuests(new HashSet<>());
        event.getGuests().add(guest1);
        event.getGuests().add(guest2);
        VoteType[] types = VoteType.values();
        List<PollOption> opts = new ArrayList<>();
        for(int i=0; i<5; i++){
            PollOption p = Generator.generatePollOption(event);
            event.getOptions().add(p);
            p.setVotes(new ArrayList<>());
            em.persist(p);
            opts.add(p);
        }
        ArrayList<Integer> counts = new ArrayList<>();
        for(PollOption p: event.getOptions()){
            int count = 0;
            for(User u: event.getGuests()) {
                Vote v = new Vote(u, p);
                int rand = r.nextInt(types.length);
                VoteType t = types[rand];
                v.setVoteType(t);
                em.persist(v);
                p.getVotes().add(v);
                if(v.getVoteType().equals(VoteType.POSITIVE)){
                    count++;
                }

            }
            counts.add(count);
        }
        Integer max = counts.stream().max(Comparator.naturalOrder()).get();
        int idx = counts.indexOf(max);
        opts.get(idx).setIsFinal(true);
        sut.makeAutoFinalPollOption(event);
        PollOption res = sut1.findFinalForEvent(event);
        assertEquals(opts.get(idx).getId(), res.getId());
        assertTrue(res.getIsFinal());
    }

    @Test
    public void findAllActiveGuestEvents_findsAllActive(){
        User user = generateTestData();
        Set<Event> events = user.getGuestEvents();
        Set<Event> events1 = new HashSet<>();
        for(Event e: events){
            if(e.getOpenDueTo().isBefore(LocalDate.now())){
                continue;
            }
            events1.add(e);
        }

        Set<Event> result = sut.findAllActiveGuestEvents(user);
        assertEquals(events1, result);
    }

    @Test
    public void findAllNotVotedEvents_findsAllNotVoted(){
        User user = generateTestData();
        Set<Event> events = sut.findAllActiveGuestEvents(user);
        Set<Event> events1 = new HashSet<>();
        for(Event e: events){
            for(PollOption p: e.getOptions()){
                if(p.getVotes()==null){
                    events1.add(e);
                    break;
                }
            }
        }

        Set<Event> result = sut.findAllNotVotedEvents(user);
        assertEquals(events1, result);

    }

    private User generateTestData(){
        User user = Generator.generateUser();
        em.persist(user);
        Event event1 = Generator.generateEvent();
        Event event2 = Generator.generateEvent();
        Event event3 = Generator.generateEvent();

        em.persist(event1);
        em.persist(event1.getOwner());
        for(PollOption p: event1.getOptions()){
            em.persist(p);
        }
        em.persist(event2);
        em.persist(event2.getOwner());
        for(PollOption p: event2.getOptions()){
            em.persist(p);
            Vote vote = new Vote(user, p);
            em.persist(vote);
            user.setVotes(new ArrayList<>());
            user.getVotes().add(vote);
            p.setVotes(new ArrayList<>());
            p.getVotes().add(vote);
        }
        em.persist(event3);
        em.persist(event3.getOwner());
        for(PollOption p: event3.getOptions()){
            em.persist(p);
        }
        sut.addGuest(event1, user);
        sut.addGuest(event2, user);
        sut.addGuest(event3, user);
        return user;
    }
}
