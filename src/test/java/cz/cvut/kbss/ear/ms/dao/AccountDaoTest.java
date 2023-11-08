package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.ms.environment.Generator;
import cz.cvut.kbss.ear.ms.exceptions.NotFoundException;
import cz.cvut.kbss.ear.ms.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class)
@ActiveProfiles("test")
public class AccountDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserDao sut;
    @Autowired
    private AdminDao sut1;

    @Test
    public void findByUsername(){
        Account cat = Generator.generateUser();
        em.persist(cat);
        assertNotNull(cat.getId());


        Account res1 = sut1.findByUsername(cat.getUsername());
        assertNull(res1);


        Account res = sut.findByUsername(cat.getUsername());
        assertNotNull(res);
        assertEquals(cat.getId(), res.getId());

    }
}
