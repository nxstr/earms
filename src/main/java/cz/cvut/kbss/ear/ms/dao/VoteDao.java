package cz.cvut.kbss.ear.ms.dao;

import cz.cvut.kbss.ear.ms.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoteDao extends BaseDao<Vote>{
    public VoteDao() {
        super(Vote.class);
    }
}
