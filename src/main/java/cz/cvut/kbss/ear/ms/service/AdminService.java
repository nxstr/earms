package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.dao.AdminDao;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminService extends AccountService<Admin>{
    private final AdminDao dao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(AdminDao dao, PasswordEncoder passwordEncoder) {
        super(passwordEncoder, dao);
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return dao.findByUsername(username) != null;
    }

    @Transactional
    public Admin find(Integer id){
        Admin admin = dao.find(id);
        if(admin == null){
            throw new NotFoundException("Admin with this id does not exist!");
        }
        return admin;
    }

    @Transactional
    public Admin update(Admin admin, Integer id){
        Objects.requireNonNull(admin);
        Admin admin1 = dao.find(id);
        admin1.setUsername(admin.getUsername());
        admin.encodePassword(passwordEncoder);
        admin1.setPassword(admin.getPassword());
        dao.update(admin1);
        return admin1;
    }

    @Transactional
    public void delete(Admin admin){
        Objects.requireNonNull(admin);
        Admin returnUser = dao.find(admin.getId());
        Objects.requireNonNull(returnUser);
        dao.remove(admin);
    }

}
