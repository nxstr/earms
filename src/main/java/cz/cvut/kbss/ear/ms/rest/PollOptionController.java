package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.dto.VoteDto;
import cz.cvut.kbss.ear.ms.exceptions.DateValidationException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.PollOption;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.model.Vote;
import cz.cvut.kbss.ear.ms.model.VoteType;
import cz.cvut.kbss.ear.ms.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.ms.service.PollOptionService;
import cz.cvut.kbss.ear.ms.service.UserService;
import cz.cvut.kbss.ear.ms.service.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rest/event")
public class PollOptionController {


    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final PollOptionService pollOptionService;
    private final UserService userService;
    private final VoteService voteService;


    public PollOptionController(PollOptionService pollOptionService, UserService userService, VoteService voteService) {
        this.pollOptionService = pollOptionService;
        this.userService = userService;
        this.voteService = voteService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/options/final/{option_id}")
    public ResponseEntity<String> setFinal(Principal principal, @PathVariable Integer option_id) {
        try {
            User user = getUser(principal);
            PollOption pollOption = pollOptionService.find(option_id);

            if(!Objects.equals(user.getId(), pollOption.getEvent().getOwner().getId())){
                LOG.debug("Event {} does not belong to user {}.", pollOption.getEvent().getId(), user.getId());
                return new ResponseEntity<>("This user does not belong to current event", HttpStatus.FORBIDDEN);
            }
            PollOption pollOption1 = pollOptionService.setFinal(pollOption);
            LOG.debug("PollOption {} set to final.", pollOption1.getId());
            return new ResponseEntity<>(pollOption.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying get unexisting pollOption {}", getUser(principal).getId(), option_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (DateValidationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/options/{id}/votes/add")
    public ResponseEntity<String> addVote(Principal principal, @PathVariable Integer id, @RequestBody List<VoteDto> voteDto) {
        try {
            User user = getUser(principal);
            PollOption pollOption = pollOptionService.find(id);
            if (Objects.equals(user.getId(), pollOption.getEvent().getOwner().getId())) {
                LOG.debug("Owner is trying to add vote to event {}.", pollOption.getEvent().getId());
                return new ResponseEntity<>("Owner can not add or remove votes", HttpStatus.FORBIDDEN);
            }
            for(VoteDto v: voteDto) {
                Vote vote = new Vote(user, pollOptionService.find(id));
                vote.setComment(v.getComment());
                vote.setVoteType(VoteType.valueOf(v.getVoteType()));
                pollOptionService.addVote(pollOption, vote, user);
            }
            LOG.debug("Votes(s) successfully added to pollOption{}.", pollOption.getId());
            return new ResponseEntity<>(pollOption.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to add unexisting pollOption {}", getUser(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/options/{id}/votes/delete/{vote_id}")
    public ResponseEntity<String> removeVote(Principal principal, @PathVariable Integer id, @PathVariable Integer vote_id) {
        try {
            User user = getUser(principal);
            PollOption pollOption = pollOptionService.find(id);
            Vote vote = voteService.find(vote_id);
            if (Objects.equals(user.getId(), pollOption.getEvent().getOwner().getId())) {
                LOG.debug("Owner is trying to remove vote to event {}.", pollOption.getEvent().getId());
                return new ResponseEntity<>("Owner can not add or remove votes", HttpStatus.FORBIDDEN);
            }
            pollOptionService.removeVote(pollOption, vote);
            LOG.debug("Votes successfully deleted from pollOption{}.", pollOption.getId());
            return new ResponseEntity<>("Successfully deleted vote", HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to remove unexisting pollOption {} or vote {}", getUser(principal).getId(), id, vote_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private User getUser(Principal principal){
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        return userService.find(id);
    }
}
