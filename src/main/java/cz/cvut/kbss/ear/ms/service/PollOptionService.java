package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.dao.PollOptionDao;
import cz.cvut.kbss.ear.ms.dao.UserDao;
import cz.cvut.kbss.ear.ms.exceptions.DateValidationException;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class PollOptionService {
    private final PollOptionDao dao;
    private final UserDao userDao;
    private final VoteService voteService;

    @Autowired
    public PollOptionService(PollOptionDao dao, UserDao userDao, VoteService voteService) {
        this.dao = dao;
        this.userDao = userDao;
        this.voteService = voteService;
    }

    @Transactional
    public PollOption persist(PollOption pollOption, Event event){
        Objects.requireNonNull(event);
        Objects.requireNonNull(pollOption);
        for(PollOption p: event.getOptions()){
            if(Objects.equals(p.getDateSlot(), pollOption.getDateSlot()) && Objects.equals(p.getTimeSlot(), pollOption.getTimeSlot())){
                throw new ExistsException("Same date is already exist in this event");
            }
        }
        pollOption.setEvent(event);
        dao.persist(pollOption);
        return pollOption;
    }

    @Transactional
    public PollOption setFinal(PollOption pollOption){
        Objects.requireNonNull(pollOption);
        Event event = pollOption.getEvent();
        if(event.getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed!");
        }
        for(PollOption p: event.getOptions()){
            if(p.getIsFinal()){
                p.setIsFinal(false);
            }
        }
        pollOption.setIsFinal(true);
        dao.update(pollOption);
        return pollOption;
    }

    @Transactional(readOnly = true)
    public PollOption findFinalForEvent(Event event){
        Objects.requireNonNull(event);
        return dao.findFinalForEvent(event);
    }

    @Transactional(readOnly = true)
    public PollOption find(Integer id){
        PollOption pollOption = dao.find(id);
        if(pollOption==null) {
            throw new NotFoundException("Polloption with this id s not found");
        }
        return pollOption;
    }

    @Transactional
    public void addVote(PollOption pollOption, Vote vote, User user){
        Objects.requireNonNull(vote);
        Objects.requireNonNull(pollOption);
        if(pollOption.getEvent().getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        for(User u:pollOption.getEvent().getGuests()){
            if(Objects.equals(u.getId(), user.getId())){
                vote.setGuest(user);
                vote.setPollOption(pollOption);
                voteService.persist(pollOption, vote);
                return;
            }
        }
        throw new NotFoundException("This user is not a guest for event");
    }

    @Transactional
    public void removeVote(PollOption pollOption, Vote vote){
        Objects.requireNonNull(vote);
        Objects.requireNonNull(pollOption);
        if(pollOption.getEvent().getOpenDueTo().isBefore(LocalDate.now())){
            throw new DateValidationException("Event is already closed");
        }
        for(User u:pollOption.getEvent().getGuests()){
            if(Objects.equals(u.getId(), vote.getGuest().getId())){
                u.getVotes().remove(vote);
                userDao.update(u);
                pollOption.getVotes().remove(vote);
                dao.update(pollOption);
                voteService.delete(vote);
                return;
            }
        }
    }

    @Transactional
    public Integer countPositiveVotes(PollOption pollOption){
        int count = 0;
        for(Vote v: pollOption.getVotes()){
            if(v.getVoteType() == VoteType.POSITIVE){
                count++;
            }
        }
        return count;
    }

    @Transactional
    public void delete(PollOption pollOption){
        Objects.requireNonNull(pollOption);
//        while(pollOption.getVotes().size()!=0){
//            Vote vote = pollOption.getVotes().get(pollOption.getVotes().size() - 1);
//            voteService.delete(vote);
//            pollOption.getVotes().remove(vote);
//        }
        dao.remove(pollOption);
    }
}
