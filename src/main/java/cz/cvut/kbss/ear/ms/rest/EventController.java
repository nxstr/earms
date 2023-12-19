package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.dto.AccountDto;
import cz.cvut.kbss.ear.ms.dto.EventDto;
import cz.cvut.kbss.ear.ms.dto.PollOptionDto;
import cz.cvut.kbss.ear.ms.dto.VoteDto;
import cz.cvut.kbss.ear.ms.exceptions.DateValidationException;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.*;
import cz.cvut.kbss.ear.ms.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.ms.service.AdminService;
import cz.cvut.kbss.ear.ms.service.EventService;
import cz.cvut.kbss.ear.ms.service.PollOptionService;
import cz.cvut.kbss.ear.ms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/event")
public class EventController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final EventService eventService;
    private final UserService userService;
    private final AdminService adminService;
    private final PollOptionService pollOptionService;

    @Autowired
    public EventController(EventService eventService, UserService userService, AdminService adminService, PollOptionService pollOptionService) {
        this.eventService = eventService;
        this.userService = userService;
        this.adminService = adminService;
        this.pollOptionService = pollOptionService;
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(value = "/{id}")
    public EventDto getEvent(Principal principal, @PathVariable Integer id) {
        try {
            Account user = getAccount(principal);
            Event event = eventService.find(id);
            if (eventService.isClosed(event) && pollOptionService.findFinalForEvent(event) == null) {
                eventService.makeAutoFinalPollOption(event);
            }
            if (user.getRole() == Role.ADMIN) {
                return prepareDto(event);
            }
            boolean isBelong = Objects.equals(user.getId(), event.getOwner().getId());
            if (!isBelong) {
                for (User u : event.getGuests()) {
                    if (Objects.equals(u.getId(), user.getId())) {
                        isBelong = true;
                        break;
                    }
                }
                if (!isBelong) {
                    return new EventDto();
                }
            }
            return prepareDto(event);
        }catch (NotFoundException e){
            return new EventDto();
        }
    }


    private EventDto prepareDto(Event event){
        List<AccountDto> guests = new ArrayList<>();
        event.getGuests().stream().forEach(e-> guests.add(new AccountDto(e.getId(), e.getUsername(), null, e.getRole().toString())));
        List<PollOptionDto> pollOptionDtoList = new ArrayList<>();

        for(PollOption e: event.getOptions()){
            List<VoteDto> voteDtoList = new ArrayList<>();
            e.getVotes().stream().forEach(v -> voteDtoList.add(new VoteDto(v.getId(), v.getComment(), v.getVoteType().toString(), v.getGuest().getId(), e.getId())));
            PollOptionDto p = new PollOptionDto(e.getId(), e.getDateSlot(), e.getTimeSlot(), e.getIsFinal(), event.getId(), voteDtoList);
            pollOptionDtoList.add(p);
        }

        return new EventDto(event.getId(), event.getName(), event.getDetail(), event.getOpenDueTo(), event.getLocation(), event.getOwner().getId(),
                guests, pollOptionDtoList);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping(value = "/activeOwned")
    public List<EventDto> getAllActiveOwned(Principal principal) {
        User user = getUser(principal);
        List<Integer> activeOwned = eventService.findAllActiveForOwner(user).stream().map(Event::getId).collect(Collectors.toList());;
        List<EventDto> events = new ArrayList<>();
        activeOwned.forEach(e-> events.add(prepareDto(eventService.find(e))));
        return events;
    }


    @GetMapping(value = "/allOwned")
    public List<EventDto> getAllOwned(Principal principal) {
        System.out.println( principal.getName());
        User user = getUser(principal);
        List<EventDto> events = new ArrayList<>();
        user.getOwnedEvents().forEach(e-> events.add(prepareDto(e)));
        System.out.println("here");
        return events;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping(value = "/activeGuest")
    public List<EventDto> getAllActiveGuest(Principal principal) {
        User user = getUser(principal);
        List<Integer> activeGuest = eventService.findAllActiveGuestEvents(user).stream().map(Event::getId).collect(Collectors.toList());;
        List<EventDto> events = new ArrayList<>();
        activeGuest.forEach(e-> events.add(prepareDto(eventService.find(e))));
        return events;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping(value = "/notVoted")
    public List<Integer> getAllNotVoted(Principal principal) {
        User user = getUser(principal);
        List<Integer> notVoted = eventService.findAllNotVotedEvents(user).stream().map(Event::getId).collect(Collectors.toList());;
        return notVoted;
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/create")
    public ResponseEntity<Event> createEvent(Principal principal, @RequestBody EventDto event) {
            User user = getUser(principal);
            Event res = eventService.create(event.getName(), event.getDetail(), event.getLocation(), event.getOpenDueTo(), user);

            LOG.debug("Event {} successfully created.", event.getName());
            return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping(value = "/{event_id}/update")
    public ResponseEntity<String> updateEvent(Principal principal, @RequestBody EventDto eventDto ,@PathVariable Integer event_id) {
        try {
            Account user = getAccount(principal);
            Event event = eventService.find(event_id);
            event.setName(eventDto.getName());
            event.setDetail(eventDto.getDetail());
            event.setLocation(eventDto.getLocation());
            event.setOpenDueTo(eventDto.getOpenDueTo());
            if (user.getRole() == Role.ADMIN) {
                Event event1 = eventService.update(event, event_id);
                LOG.debug("Event {} successfully updated by admin.", event1.getId());
                return new ResponseEntity<>(event1.toString(), HttpStatus.OK);
            } else {
                if (!Objects.equals(user.getId(), eventService.find(event_id).getOwner().getId())) {
                    LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                    return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
                }
                Event event1 = eventService.update(event, event_id);
                LOG.debug("Event {} successfully updated.", event1.getId());
                return new ResponseEntity<>(event1.toString(), HttpStatus.OK);
            }
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {}.", getAccount(principal).getId(), event_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping(value = "/{id}/delete")
    public ResponseEntity<String> deleteEvent(Principal principal, @PathVariable Integer id) {
        try {
            Account user = getAccount(principal);
            Event event = eventService.find(id);
            if (user.getRole() == Role.ADMIN) {
                eventService.delete(event);
                LOG.debug("Event {} successfully deleted by admin.", event.getId());
            } else {
                if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                    LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                    return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
                }
                eventService.delete(event);
                LOG.debug("Event {} successfully deleted.", event.getId());
            }
            return new ResponseEntity<>("Event with id " + id + " successfully deleted", HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}/close")
    public ResponseEntity<String> closeEvent(Principal principal, @PathVariable Integer id) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(id);
            eventService.closeEvent(event);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }

            LOG.debug("Event {} successfully closed.", event.getId());
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/closeAll")
    public ResponseEntity<String> closeAllEvents(Principal principal) {
            User user = getUser(principal);
            eventService.closeAll(user);
            LOG.debug("Events for user {} successfully closed.", user.getId());
            return new ResponseEntity<>("Events successfully closed", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}/options")
    public ResponseEntity<String> addPollOption(Principal principal, @PathVariable Integer id, @RequestBody List<PollOptionDto> pollOptionsDto) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(id);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }
            for (PollOptionDto p : pollOptionsDto) {
                PollOption pl = new PollOption(p.getDateSlot(), p.getTimeSlot(), event);
                eventService.addPollOption(event, pl);
            }
            LOG.debug("PollOption(s) successfully added to event{}.", event.getId());
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (DateValidationException d){
            LOG.debug("User {} is trying to add not valid pollOption event {} or event is closed.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(d.getMessage(), HttpStatus.NOT_FOUND);
        }catch (ExistsException e){
            LOG.debug("User {} is trying to add same pollOption to event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{event_id}/options/{id}")
    public ResponseEntity<String> removePollOption(Principal principal, @PathVariable Integer event_id, @PathVariable Integer id) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(event_id);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }
            PollOption pollOption = pollOptionService.find(id);
            if (!Objects.equals(event_id, pollOption.getEvent().getId())) {
                LOG.debug("Event {} does not have an pollOption {}.", event_id, id);
                return new ResponseEntity<>("This pollOption does not belong to current event", HttpStatus.FORBIDDEN);
            }
            eventService.removePollOption(event, pollOption);
            LOG.debug("PollOption {} successfully deleted from event{}.", id, event_id);
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (DateValidationException d){
            LOG.debug("User {} is trying to remove pollOption from closed event {}", getAccount(principal).getId(), event_id);
            return new ResponseEntity<>(d.getMessage(), HttpStatus.NOT_FOUND);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {} or pollOption {}.", getAccount(principal).getId(), event_id, id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}/guests/add/registered")
    public ResponseEntity<String> addGuestsByUsernames(Principal principal, @PathVariable Integer id, @RequestBody List<String> usernames) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(id);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }
            for (String u : usernames) {
                eventService.addRegisteredUserAsAGuestByUsername(u, event);
            }
            LOG.debug("User(s) successfully added to event{}.", event.getId());
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {} or username.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (DateValidationException d){
            LOG.debug("User {} is trying to add guest to closed event {}", getAccount(principal).getId(), id);
            return new ResponseEntity<>(d.getMessage(), HttpStatus.NOT_FOUND);
        }catch (ExistsException e){
            LOG.debug("User {} is trying to add same guest to event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}/guests/add/notregistered")
    public ResponseEntity<String> addGuestsByEmails(Principal principal, @PathVariable Integer id, @RequestBody List<String> emails) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(id);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }
            for (String u : emails) {
                eventService.addUserAsAGuestByEmail(u, event);
            }
            LOG.debug("User(s) successfully added to event{} or email for registration sended.", event.getId());
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to get unexisting event {}.", getAccount(principal).getId(), id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (DateValidationException d){
            LOG.debug("User {} is trying to add guest to closed event {}", getAccount(principal).getId(), id);
            return new ResponseEntity<>(d.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{event_id}/guests/delete/{id}")
    public ResponseEntity<String> removeGuest(Principal principal, @PathVariable Integer event_id, @PathVariable Integer id) {
        try {
            User user = getUser(principal);
            Event event = eventService.find(event_id);
            if (!Objects.equals(user.getId(), event.getOwner().getId())) {
                LOG.debug("Event {} does not belong to user {}.", event.getId(), user.getId());
                return new ResponseEntity<>("This event does not belong to current user", HttpStatus.FORBIDDEN);
            }
            User guest = userService.find(id);
            eventService.removeGuest(event, guest);
            LOG.debug("User {} successfully removed from event {}.", user.getId(), event.getId());
            return new ResponseEntity<>(event.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is trying to remove guest with id {} from event {}", getAccount(principal).getId(), event_id, id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (DateValidationException d){
            LOG.debug("User {} is trying to add guest to closed event {}", getAccount(principal).getId(), id);
            return new ResponseEntity<>(d.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private User getUser(Principal principal){
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        return userService.find(id);
    }

    private Account getAccount(Principal principal){
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            Account acc = userService.findById(id);
            if(acc==null){
                acc = adminService.findById(id);
            }
            return acc;
    }
}
