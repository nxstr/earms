package cz.cvut.kbss.ear.ms.security.model;

import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class AccountDetails implements org.springframework.security.core.userdetails.UserDetails{

    private final Account user;

    private final Set<GrantedAuthority> authorities;

    public AccountDetails(Account user) {
        Objects.requireNonNull(user);
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
    }

    public AccountDetails(Account user, Collection<GrantedAuthority> authorities) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(authorities);
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
        this.authorities.addAll(authorities);
    }

    private void addUserRole() {
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Account getAccount() {
        return user;
    }

    public void eraseCredentials() {
        user.erasePassword();
    }
}


