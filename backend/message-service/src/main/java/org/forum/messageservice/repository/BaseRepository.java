package org.forum.messageservice.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BaseRepository {
    protected SessionFactory sessionFactory;

    @Autowired
    public BaseRepository(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
}
