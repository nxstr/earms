package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public abstract class AccountDao<T extends Account> extends BaseDao<T>{
    public AccountDao(Class<T> type) { super(type); }

    public T findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM "
                            + type.getSimpleName()
                            + " u WHERE u.username = "
                            +"'"+username +"'", type)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
