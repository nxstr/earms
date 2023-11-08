package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.service.AdminService;
import cz.cvut.kbss.ear.ms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/users/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AdminService adminService;

    @Autowired
    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }


    @PostMapping(value = "/createAdmin")
    public ResponseEntity<String> createAdmin(@RequestBody Admin admin) {
        try {
            Admin admin1 = adminService.persist(admin);
            LOG.debug("User {} successfully registered.", admin1.getUsername());
            return new ResponseEntity<>(admin1.toString(), HttpStatus.CREATED);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/updateAdmin/{id}")
    public ResponseEntity<String> updateAdmin(@RequestBody Admin admin, @PathVariable Integer id) {
        try {
            Admin admin1 = adminService.update(admin, id);
            LOG.debug("User {} successfully updated.", admin1.getUsername());
            return new ResponseEntity<>(admin1.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("Admin {} is not found", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/deleteAdmin/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Integer id) {
        try {
            Admin admin1 = adminService.find(id);
            adminService.delete(admin1);
            LOG.debug("User {} successfully deleted.", admin1.getUsername());
            return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("Admin {} is not found", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/updateUser/{id}")
    public ResponseEntity<String> updateUser(@RequestBody User user, @PathVariable Integer id) {
        try {

            User user1 = userService.update(user, id);
            LOG.debug("User {} successfully updated.", user1.getUsername());
            return new ResponseEntity<>(user1.toString(), HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is not found", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (ExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            User user1 = userService.find(id);
            userService.delete(user1);
            LOG.debug("User {} successfully deleted.", user1.getUsername());
//        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
            return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
        }catch (NotFoundException e){
            LOG.debug("User {} is not found", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
