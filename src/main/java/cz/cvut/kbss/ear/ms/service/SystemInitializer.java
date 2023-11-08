package cz.cvut.kbss.ear.ms.service;


import cz.cvut.kbss.ear.ms.model.Admin;
import cz.cvut.kbss.ear.ms.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import javax.annotation.PostConstruct;

@Component
public class SystemInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    /**
     * Default admin username
     */
    private static final String ADMIN_USERNAME = "admin6";


    private final AdminService adminService;
    private final PlatformTransactionManager txManager;

    @Autowired
    public SystemInitializer(AdminService adminService, PlatformTransactionManager txManager) {
        this.adminService = adminService;
        this.txManager = txManager;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generateAdmin();
            return null;
        });
    }

    /**
     * Generates an admin account if it does not already exist.
     */
    private void generateAdmin() {
        if (adminService.exists(ADMIN_USERNAME)) {
            return;
        }
        final Admin admin = new Admin();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword("admin6");
        admin.setRole(Role.ADMIN);
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        adminService.persist(admin);
    }
}

