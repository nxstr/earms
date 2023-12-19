package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.dto.AccountDto;
import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.ms.service.AdminService;
import cz.cvut.kbss.ear.ms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/rest/users")
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final AdminService adminService;
    private final UserService userService;

    @Autowired
    public AccountController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(value = "/current")
    public AccountDto getCurrent(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Account acc = userService.findById(id);
        if(acc==null){
            acc = adminService.findById(id);
        }
        return new AccountDto(acc.getId(), acc.getUsername(), null, acc.getRole().toString());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/get/{id}")
    public AccountDto getAccount(@PathVariable Integer id) {
        Account acc = userService.findById(id);
        if(acc==null){
            acc = adminService.findById(id);
            if(acc==null){
                return new AccountDto(null, null, null, null);
            }
        }
        return new AccountDto(acc.getId(), acc.getUsername(), null, acc.getRole().toString());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    @PutMapping(value = "/changeUname")
    public ResponseEntity<String> changeCurrentUserUsername(Principal principal, @RequestBody String username){
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            User user = userService.findById(id);
            if (user == null) {
                Admin user1 = adminService.find(id);
                adminService.changeCurrentPersonUsername(id, username);
                LOG.debug("User {} changed username {}.", user1.getId(), username);
                return new ResponseEntity<>(user1.toString(), HttpStatus.OK);
            }
            userService.changeCurrentPersonUsername(id, username);
            LOG.debug("User {} changed username {}.", user.getEmail(), username);
            return new ResponseEntity<>(user.toString(), HttpStatus.OK);
    }
}


