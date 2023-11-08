package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDao extends AccountDao<Admin>{
    public AdminDao() {
        super(Admin.class);
    }
}
