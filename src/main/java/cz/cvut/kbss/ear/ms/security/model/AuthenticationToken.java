package cz.cvut.kbss.ear.ms.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class AuthenticationToken extends AbstractAuthenticationToken implements Principal {

    private final AccountDetails userDetails;

    public AuthenticationToken(Collection<? extends GrantedAuthority> authorities, AccountDetails userDetails) {
        super(authorities);
        this.userDetails = userDetails;
        super.setAuthenticated(true);
        super.setDetails(userDetails);
    }

    @Override
    public String getCredentials() {
        return userDetails.getPassword();
    }

    @Override
    public AccountDetails getPrincipal() {
        return userDetails;
    }
}
