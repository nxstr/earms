package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.dao.PollOptionDao;
import cz.cvut.kbss.ear.ms.dao.UserDao;
import cz.cvut.kbss.ear.ms.dao.VoteDao;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.PollOption;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class VoteService {
    private final VoteDao dao;
    private final UserDao userDao;
    private final PollOptionDao pollOptionDao;

    @Autowired
    public VoteService(VoteDao dao, UserDao userDao, PollOptionDao pollOptionDao) {
        this.dao = dao;
        this.userDao = userDao;
        this.pollOptionDao = pollOptionDao;
    }

    @Transactional
    public Vote persist(PollOption pollOption, Vote vote){
        for(Vote v: pollOption.getVotes()){
            if(v.getGuest()==vote.getGuest()){
                v.setComment(vote.getComment());
                v.setVoteType(vote.getVoteType());
                update(v);
                return v;
            }
        }
        dao.persist(vote);
        if(pollOption.getVotes().isEmpty()){
            pollOption.setVotes(new ArrayList<>());
        }
        pollOption.getVotes().add(vote);
        if(vote.getGuest().getVotes().isEmpty()){
            vote.getGuest().setVotes(new ArrayList<>());
        }
        vote.getGuest().getVotes().add(vote);
        return vote;
    }

    @Transactional(readOnly = true)
    public Vote find(Integer id){
        Vote vote = dao.find(id);
        if(vote==null) {
            throw new NotFoundException("Event with this id s not found");
        }
        return vote;
    }

    @Transactional
    public void update(Vote vote){
        Objects.requireNonNull(vote);
        dao.update(vote);
    }

    @Transactional
    public void delete(Vote vote){
        Objects.requireNonNull(vote);
        User user = vote.getGuest();
        user.getVotes().remove(vote);
        userDao.update(user);
        PollOption pollOption = vote.getPollOption();
        pollOption.getVotes().remove(vote);
        pollOptionDao.update(pollOption);
        dao.remove(vote);
    }
}
