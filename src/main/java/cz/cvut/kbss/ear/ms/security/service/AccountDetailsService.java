package cz.cvut.kbss.ear.ms.security.service;

import cz.cvut.kbss.ear.ms.dao.AccountDao;
import cz.cvut.kbss.ear.ms.dao.AdminDao;
import cz.cvut.kbss.ear.ms.dao.UserDao;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.security.model.AccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserDao userDao;
    private final AdminDao adminDao;

    @Autowired
    public AccountDetailsService(UserDao userDao, AdminDao adminDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account user = findUserWithAllDao(username);
        if (user == null) {
            throw new NotFoundException("User with username " + username + " not found.");
        }
        return new AccountDetails(user);
    }

    private Account findUserWithAllDao(String username)
    {
        Account user = adminDao.findByUsername(username);
        if (user == null){
            user = userDao.findByUsername(username);
        }
        return user;
    }

}

