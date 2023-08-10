package org.example.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public abstract class AbstractHibernateDao<T> {
    @Autowired
    protected SessionFactory sessionFactory;

    protected Class<T> clazz;

    protected final void setClazz(final Class<T> clazzToSet) {
        clazz = clazzToSet;
    }

    public T findById(int id) {
        return getCurrentSession().get(clazz, id);
    }

    public void add(T item) {
        getCurrentSession().save(item);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
