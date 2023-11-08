package cz.cvut.kbss.ear.ms.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class DefaultPageController {
    @GetMapping
    public String defaultString() {
        return "Welcome to Meeting Scheduler!!!";
    }
}

