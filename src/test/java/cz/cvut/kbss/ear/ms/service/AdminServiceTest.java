package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.model.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class AdminServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AdminService sut;

    @Test
    public void createAndFindAdmin(){
        Account account = Generator.generateAdmin();
        Admin admin = sut.persist((Admin) account);

        Admin result = sut.find(account.getId());
        assertEquals(admin, result);
    }

}
