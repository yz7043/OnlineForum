package org.example.dao;

import org.example.config.HibernateConfigUtil;
import org.example.domain.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    public User getUserUsingEmail(String email) {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;
        User user;

        try {
            transaction = session.beginTransaction();

            String hqlQuery = "SELECT u FROM User u WHERE u.email = :email";
            Query<User> query = session.createQuery(hqlQuery, User.class);
            query.setParameter("email", email);
            user = query.getSingleResult();

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }

            user = null;
        } finally {
            session.close();
        }

        return user;
    }


    public boolean registerAuser(User user) {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;

        boolean isSuccess = false;

        try {
            transaction = session.beginTransaction();

            //transient to presistent
            session.saveOrUpdate(user);

            isSuccess = true;
            transaction.commit();

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
                isSuccess = false;
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return isSuccess;
    }
}
