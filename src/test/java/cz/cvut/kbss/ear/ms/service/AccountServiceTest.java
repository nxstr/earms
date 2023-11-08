package cz.cvut.kbss.ear.ms.service;

import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.exceptions.ExistsException;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.Account;
import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class AccountServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserService sut;
    @Autowired
    private AdminService sut1;

    @Test
    public void registerPersonReturnsException(){
        Account account = Generator.generateUser();
        em.persist(account);
        assertThrows(ExistsException.class, () -> sut.persist((User) account));
    }

    @Test
    public void changeCurrentPersonUsernameChangesUsername(){
        Account account = Generator.generateAdmin();
        em.persist(account);

        sut1.changeCurrentPersonUsername(account.getId(), "testUserName");
        assertEquals("testUserName", account.getUsername());
    }

}
