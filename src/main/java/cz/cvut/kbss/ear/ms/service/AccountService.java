package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.dao.AccountDao;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.model.Account;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public abstract class AccountService <T extends Account>{
    private final PasswordEncoder passwordEncoder;
    private final AccountDao<T> userDao;

    protected AccountService(PasswordEncoder passwordEncoder, AccountDao<T> userDao) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public T findByUsername(String username)
    {
        return userDao.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public T findById(Integer userId)
    {
        Objects.requireNonNull(userDao);
        return userDao.find(userId);
    }

    @Transactional
    public T persist(T user)
    {
        Objects.requireNonNull(user);
        Objects.requireNonNull(userDao);
        T returnAccount = userDao.findByUsername(user.getUsername());
        if(returnAccount !=null){
            throw new ExistsException("Person with this username is already exist!");
        }
        user.encodePassword(passwordEncoder);
        userDao.persist(user);
        return user;
    }

    @Transactional
    public void changeCurrentPersonUsername(Integer id, String username){
        Account returnAccount = findByUsername(username);
        if(returnAccount !=null){
            throw new ExistsException("Person with this username is already exists!");
        }
        T currentAccount = findById(id);
        currentAccount.setUsername(username);
        userDao.update(currentAccount);
    }
}
