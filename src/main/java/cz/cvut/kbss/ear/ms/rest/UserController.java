package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.dto.UserDto;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.security.model.AuthenticationToken;
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
import java.util.Objects;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param userDto UserDto data
     */
    @PreAuthorize("anonymous || hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        try {
            User user = new User(userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.getFirstName(), userDto.getLastName());
            if(userService.existsUsername(user.getUsername())){
                return new ResponseEntity<>("Username is already exists", HttpStatus.BAD_REQUEST);
            }
            if(userService.existsEmail(user.getEmail())){
                return new ResponseEntity<>("Email is already exists", HttpStatus.CONFLICT);
            }
            User user1 = userService.persist(user);
            LOG.debug("User {} successfully registered.", user1.getUsername());
            return new ResponseEntity<>(user1.toString(), HttpStatus.CREATED);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/changeEmail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> changeCurrentUserEmail(Principal principal, @RequestBody String email){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            User user = userService.find(id);
            userService.changeCurrentUserEmail(user, email);
            LOG.debug("User {} changed email {}.", user.getUsername(), email);
            return new ResponseEntity<>(user.toString(), HttpStatus.NO_CONTENT);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    @PutMapping(value = "/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> update(Principal principal, @RequestBody UserDto userDto){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            User user = userService.find(id);
            user.setUsername(userDto.getUsername());
            if(!Objects.equals(userDto.getPassword(), "") && !Objects.equals(userDto.getPassword(), null)) {
                user.setPassword(userDto.getPassword());
            }
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            User user1 = userService.update(user, id);
            LOG.debug("User {} changed data.", user1.getId());
            return new ResponseEntity<>(user1.toString(), HttpStatus.OK);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/{id}")
    public UserDto getUser(@PathVariable Integer id){
        try {
            User user = userService.findById(id);
            return new UserDto(id, user.getUsername(), "", user.getFirstName(), user.getLastName(), user.getEmail(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }catch (NotFoundException e){
            return null;
        }
    }

    @GetMapping(value = "/getByName/{username}")
    public UserDto getUserByUsername(@PathVariable String username){
        try {
            User user = userService.findByUsername(username);
            return new UserDto(user.getId(), user.getUsername(), "", user.getFirstName(), user.getLastName(), user.getEmail(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }catch (NotFoundException e){
            return null;
        }
    }
}


