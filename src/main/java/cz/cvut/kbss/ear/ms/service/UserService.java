package cz.cvut.kbss.ear.ms.service;


import cz.cvut.kbss.ear.ms.dao.EventDao;
import cz.cvut.kbss.ear.ms.dao.UserDao;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class UserService extends AccountService<User>{
    private final UserDao dao;
    private final PasswordEncoder passwordEncoder;
    private final EventDao eventDao;

    @Autowired
    public UserService(UserDao dao, PasswordEncoder passwordEncoder, PasswordEncoder passwordEncoder1, EventDao eventDao) {
        super(passwordEncoder, dao);
        this.dao = dao;
        this.passwordEncoder = passwordEncoder1;
        this.eventDao = eventDao;
    }

    @Transactional(readOnly = true)
    public User find(Integer id){
        User user = dao.find(id);
        if(user == null){
            throw new NotFoundException("User with this id does not exist!");
        }
        return user;
    }


    @Transactional
    public void changeCurrentUserEmail(User currentUser, String email){
        User returnUser = dao.findByEmail(email);
        if(returnUser !=null){
            throw new ExistsException("User with this email is already exist!");
        }
        currentUser.setEmail(email);
        dao.update(currentUser);
    }

    @Transactional
    public User update(User user, Integer id){
        Objects.requireNonNull(user);
        User returnUser = dao.find(id);
        if(returnUser ==null){
            throw new ExistsException("This user is not exist!");
        }
        returnUser.setUsername(user.getUsername());
        if(!Objects.equals(user.getUsername(), returnUser.getUsername())){
            if(existsUsername(user.getUsername())){
                throw new ExistsException("This username is already exist!");
            }
        }
        user.encodePassword(passwordEncoder);
        returnUser.setPassword(user.getPassword());
        returnUser.setEmail(user.getEmail());
        if(!Objects.equals(user.getEmail(), returnUser.getEmail())){
            if(existsEmail(user.getEmail())){
                throw new ExistsException("This email is already exist!");
            }
        }
        returnUser.setFirstName(user.getFirstName());
        returnUser.setLastName(user.getLastName());
        dao.update(returnUser);
        return returnUser;
    }

    @Transactional
    public void delete(User user){
        Objects.requireNonNull(user);
        User returnUser = dao.find(user.getId());
        for(Event e: user.getGuestEvents()){
            e.getGuests().remove(user);
            eventDao.update(e);
        }
        Objects.requireNonNull(returnUser);
        dao.remove(user);
    }

    private boolean existsUsername(String username){
        return dao.findByUsername(username) != null;
    }

    private boolean existsEmail(String email){
        return dao.findByEmail(email) != null;
    }

}
