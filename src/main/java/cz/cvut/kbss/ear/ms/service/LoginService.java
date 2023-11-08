package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.security.DefaultAuthenticationProvider;
import cz.cvut.kbss.ear.ms.security.SecurityUtils;
import cz.cvut.kbss.ear.ms.security.model.AccountDetails;
import cz.cvut.kbss.ear.ms.security.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LoginService {

    private final DefaultAuthenticationProvider provider;
    private final AccountDetailsService userDetailsService;

    @Autowired
    public LoginService(DefaultAuthenticationProvider provider, AccountDetailsService userDetailsService) {
        this.provider = provider;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public void loginUser (String username, String password) throws ExistsException {
        if (SecurityUtils.getCurrentUserDetails() != null) {
            throw new ExistsException("You are already login." + SecurityUtils.getCurrentUserDetails().getUsername());
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        provider.authenticate(authentication);

        final AccountDetails userDetails = (AccountDetails) userDetailsService.loadUserByUsername(username);
        SecurityUtils.setCurrentUser(userDetails);
        System.out.println(SecurityUtils.getCurrentUser().getRole());
    }
}
