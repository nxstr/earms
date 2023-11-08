package cz.cvut.kbss.ear.ms.rest;

import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody HashMap<String, String> request){
        try {
            loginService.loginUser(request.get("username"), request.get("password"));
            LOG.trace("User {} successfully logged in", request.get("username"));
            return new ResponseEntity<>("Welcome to MS, " + request.get("username"), HttpStatus.OK);
        }catch (ExistsException e){
            LOG.trace("User {} is already logged in", request.get("username"));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (NotFoundException e){
            LOG.trace("User {} is not exist", request.get("username"));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (BadCredentialsException e){
            LOG.trace(" Wrong password for username {} is not exist", request.get("username"));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}


