package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.dao.EventDao;
import cz.cvut.kbss.ear.ms.dao.UserDao;
import cz.cvut.kbss.ear.ms.exceptions.DateValidationException;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class EventService {
    private final EventDao dao;
    private final UserDao userDao;
    private final PollOptionService pollOptionService;
    private final DefaultEmailService emailService;
    @Autowired
    public EventService(EventDao dao, UserDao userDao, PollOptionService pollOptionService, DefaultEmailService emailService) {
        this.dao = dao;
        this.userDao = userDao;
        this.pollOptionService = pollOptionService;
        this.emailService = emailService;
    }
    @Transactional
    public Event create(String name, String detail, String location, LocalDate openDueTo, User owner){
        Event event = new Event();
        Objects.requireNonNull(name);
        event.setName(name);
        event.setDetail(detail);
        event.setLocation(location);
        if(openDueTo.isBefore(LocalDate.now())){
            throw new DateValidationException("Date is no longer valid");
        }
        event.setOpenDueTo(openDueTo);
        event.setOptions(new ArrayList<>());

        Objects.requireNonNull(owner);
        event.setOwner(owner);
        if(owner.getOwnedEvents().isEmpty()){
            owner.setOwnedEvents(new HashSet<>());
        }
        owner.getOwnedEvents().add(event);
        dao.persist(event);
        return event;
    }



    @Transactional
    public void addRegisteredUserAsAGuestByUsername(String username, Event event){
        Objects.requireNonNull(event);
        Objects.requireNonNull(username);
        if(event.getGuests() == null){
            event.setGuests(new HashSet<>());
        }
        User user = userDao.findByUsername(username);

        if(user==null){
            throw new NotFoundException("User with this username does not exist");
        }
        addGuest(event, user);
    }
    @Transactional
    public void addUserAsAGuestByEmail(String email, Event event){
        Objects.requireNonNull(event);
        Objects.requireNonNull(email);
        if(event.getGuests() == null){
            event.setGuests(new HashSet<>());
        }
        User user = userDao.findByEmail(email);
        if(user==null){
            emailService.sendSimpleEmail(email, "Registration", "You have been invited to register on Meeting Scheduler\nhttp://localhost:8080/ms/rest/users/register");
            return;
        }
        addGuest(event, user);
    }
    @Transactional
    public void addGuest(Event event, User user) {
        if(event.getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        if(Objects.equals(user.getId(), event.getOwner().getId())){
            throw new ExistsException("Owner of event can not be added as a guest");
        }
        if(event.getGuests()==null){
            event.setGuests(new HashSet<>());
        }
        for(User u: event.getGuests()){
            if(Objects.equals(user.getUsername(), u.getUsername())){
                throw new ExistsException("Same user is already added to this event");
            }
        }
        event.getGuests().add(user);
        if(user.getGuestEvents()==null){
            user.setGuestEvents(new HashSet<>());
        }
        user.getGuestEvents().add(event);

        dao.update(event);
    }

    @Transactional
    public void removeGuest(Event event, User user){
        Objects.requireNonNull(event);
        Objects.requireNonNull(user);
        if(event.getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        if(Objects.equals(user.getId(), event.getOwner().getId())){
            throw new ExistsException("Owner of event can not be removed from event");
        }
        for(User u:event.getGuests()){
            if(Objects.equals(u.getId(), user.getId())){
                user.getGuestEvents().remove(event);
                userDao.update(user);
                event.getGuests().remove(user);
                dao.update(event);
                return;
            }
        }
        throw new NotFoundException("User with this id is not belong to this event");
    }


    @Transactional
    public void addPollOption(Event event, PollOption pollOption){
        Objects.requireNonNull(event);
        Objects.requireNonNull(pollOption);
        if(event.getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        if(pollOption.getDateSlot().isBefore(LocalDate.now())){
            throw new DateValidationException("Dateoption is not valid");
        }
        pollOption.setIsFinal(false);
        pollOptionService.persist(pollOption, event);
        event.getOptions().add(pollOption);
        dao.update(event);
    }

    @Transactional
    public void removePollOption(Event event, PollOption pollOption){
        Objects.requireNonNull(event);
        Objects.requireNonNull(pollOption);
        if(event.getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        boolean exist = false;
        for(PollOption p: event.getOptions()){
            if(p.getDateSlot()==pollOption.getDateSlot() && p.getTimeSlot()==p.getTimeSlot()){
                event.getOptions().remove(p);
                exist = true;
                break;
            }
        }
        if(!exist){
            throw new NotFoundException("PollOption is not exist in this event!");
        }
        pollOptionService.delete(pollOption);
        dao.update(event);
    }


    @Transactional
    public void closeEvent(Event event){
        if(event.getOpenDueTo().isAfter(LocalDate.now()) || event.getOpenDueTo().isEqual(LocalDate.now())){
            if(pollOptionService.findFinalForEvent(event)==null) {
                makeAutoFinalPollOption(event);
            }
            event.setOpenDueTo(LocalDate.now().minusDays(1));
        }
        dao.update(event);
    }

    @Transactional
    public void closeAll(User user){
        Set<Event> events = findAllActiveForOwner(user);
        for (Event e : events) {
            if (pollOptionService.findFinalForEvent(e) == null) {
                makeAutoFinalPollOption(e);
            }
            closeEvent(e);
        }
    }

    @Transactional
    public boolean isClosed(Event event){
        return !event.getOpenDueTo().isAfter(LocalDate.now());
    }


    @Transactional
    public void makeAutoFinalPollOption(Event event){
        PollOption pollOption = pollOptionService.findFinalForEvent(event);
        if(pollOption==null){
            int max = 0;
            PollOption opt = null;
            for(PollOption p : event.getOptions()){
                opt = p;
                break;
            }
            if (event.getOpenDueTo().isAfter(LocalDate.now()) || event.getOpenDueTo().isEqual(LocalDate.now())) {
                for (PollOption p : event.getOptions()) {
                    if (pollOptionService.countPositiveVotes(p) > max) {
                        max = pollOptionService.countPositiveVotes(p);
                        opt = p;
                    }
                }
            }
            if (opt != null) {
                opt.setIsFinal(true);
                pollOptionService.setFinal(opt);
            }
        }
    }

    @Transactional(readOnly = true)
    public Set<Event> findAllActiveForOwner(User owner){
        return new HashSet<>(dao.findAllOpenedForOneOwner(owner));
    }

    @Transactional(readOnly = true)
    public Event find(Integer id){
        Event event = dao.find(id);
        if(event==null) {
            throw new NotFoundException("Event with this id s not found");
        }
        return event;
    }

    @Transactional(readOnly = true)
    public Set<Event> findAllActiveGuestEvents(User guest){
        Objects.requireNonNull(guest.getGuestEvents());
        Set<Event> events = new HashSet<>();
        for(Event e: guest.getGuestEvents()){
            if(e.getOpenDueTo().isBefore(LocalDate.now())){
                continue;
            }
            events.add(e);
        }
        return events;
    }

    @Transactional(readOnly = true)
    public Set<Event> findAllNotVotedEvents(User guest){
        Objects.requireNonNull(guest.getGuestEvents());
        Set<Event> events = findAllActiveGuestEvents(guest);
        List<Vote> votes = guest.getVotes();
        Set<Event> notVoted = new HashSet<>();
        boolean isVoted;
        for(Event e:events){
            isVoted = false;
            for(Vote v:votes){
                if(v.getPollOption().getEvent()==e){
                    isVoted = true;
                    break;
                }
            }
            if(!isVoted){
                notVoted.add(e);
            }
        }
        return notVoted;
    }

    @Transactional
    public Event update(Event event, Integer id){
        Objects.requireNonNull(event);
        Event event1 = dao.find(id);
        if(event1==null){
            throw new NotFoundException("Event with id " + id + " is not found");
        }
        event1.setName(event.getName());
        event1.setLocation(event.getLocation());
        event1.setOpenDueTo(event.getOpenDueTo());
        event1.setDetail(event.getDetail());
        dao.update(event1);
        return event1;
    }

    @Transactional
    public void delete(Event event){
        Objects.requireNonNull(event);
            for(User u: event.getGuests()){
                u.getGuestEvents().remove(event);
                userDao.update(u);
            }
            event.setGuests(new HashSet<>());
        User owner = event.getOwner();
        owner.getOwnedEvents().remove(event);
        userDao.update(owner);
        dao.remove(event);
    }

}
