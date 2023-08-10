package org.example.dao;

import org.example.config.HibernateConfigUtil;
import org.example.constant.AccountActive;
import org.example.constant.AuthorityConstant;
import org.example.constant.VerifyNeed;
import org.example.domain.User;
import org.example.dto.request.EditProfileRequest;
import org.example.exception.CantBeModifyException;
import org.example.security.AuthUserDetail;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

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

            if(user != null) {
                user.setPassword(null);
            }

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

    public User getUserUsingUserID(int userID) {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;
        User user;

        try {
            transaction = session.beginTransaction();

            String hqlQuery = "SELECT u FROM User u WHERE u.user_id = :var";
            Query<User> query = session.createQuery(hqlQuery, User.class);
            query.setParameter("var", userID);
            user = query.getSingleResult();

            if(user != null) {
                user.setPassword(null);
            }

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

    public List<User> getAllUsers() {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;

        List<User> result;

        try {
            transaction = session.beginTransaction();

            String hqlQuery = "SELECT u FROM User u";
            Query<User> query = session.createQuery(hqlQuery, User.class);
            result = query.getResultList();

            for(User user: result) {
                user.setPassword(null);
            }

//            transaction.commit();
        } catch (Exception e) {
            if(transaction != null) {
                e.printStackTrace();
                transaction.rollback();
            }
            return null;

        } finally {
            session.close();
        }

        return result;
    }


    public boolean setActiveStatus(AuthUserDetail authUserDetail, int id, int active) throws CantBeModifyException {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;

        boolean isSuccess = false;

        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);

            if(user == null) {
                throw new CantBeModifyException("User does not exist.");
            }

            if(authUserDetail.getUser_roles().equals(AuthorityConstant.AUTHORITY_USER)
                    && id != authUserDetail.getUserID()) {
                throw new CantBeModifyException("Can't modify other user because you are a user only");

            } else if(authUserDetail.getUser_roles().equals(AuthorityConstant.AUTHORITY_ADMIN)
                    && user.getType().equals(AuthorityConstant.AUTHORITY_ADMIN)) {
                throw new CantBeModifyException("Admin can't modify other admins active status");
            }

            user.setActive(active);

            isSuccess = true;
            transaction.commit();

        } catch (CantBeModifyException e) {
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e;

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


    public boolean adminAssign_Or_revoke(AuthUserDetail authUserDetail, int id, String type)
            throws CantBeModifyException {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;

        boolean isSuccess = false;

        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);

            if(user == null) {
                throw new CantBeModifyException("User does not exist.");
            }

            if(type.equals(AuthorityConstant.AUTHORITY_SUPER_ADMIN)) {
                throw new CantBeModifyException("Super admin can't be modified. ");

            } else if(user.getType().equals(AuthorityConstant.AUTHORITY_USER)
                    && (user.getActive() == AccountActive.BANNED || user.getActive() == AccountActive.INACTIVE)) {
                throw new CantBeModifyException("Can't operate banned or inactive user for admin role.");

            } else if(user.getType().equals(AuthorityConstant.AUTHORITY_ADMIN)
                    && type.equals(AuthorityConstant.AUTHORITY_ADMIN)) {

                throw new CantBeModifyException("This user is already admin");

            }

            user.setType(type);

            isSuccess = true;
            transaction.commit();

        } catch (CantBeModifyException e) {
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e;

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

    public boolean activateUserByUserEmail(String email) throws CantBeModifyException {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;

        boolean isSuccess = false;

        try {
            transaction = session.beginTransaction();

            String hqlQuery = "SELECT u FROM User u WHERE u.email = :email";
            Query<User> query = session.createQuery(hqlQuery, User.class);
            query.setParameter("email", email);
            User user = query.getSingleResult();

            user.setActive(AccountActive.ACTIVE);

            isSuccess=true;
            transaction.commit();

        } catch (NoResultException e) {
            if(transaction != null) {
                transaction.rollback();
            }
            isSuccess=false;
            e.printStackTrace();
            throw new CantBeModifyException("User does not exist.");

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return isSuccess;
    }

    public int editUserProfileInfo(int userID, EditProfileRequest request) throws CantBeModifyException {
        Session session = HibernateConfigUtil.openSession();
        Transaction transaction = null;
        int action = VerifyNeed.NO_NEED_DELETE_TOKEN;

        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, userID);
            System.out.println(user);

            if(user == null) {
                throw new CantBeModifyException("User is not found!");
            }

            if(request.getEmail() != null && !request.getEmail().equals("")) {
                user.setEmail(request.getEmail());
                user.setActive(AccountActive.INACTIVE);
                action = VerifyNeed.NEED_DELETE_TOKEN;
            }

            if(request.getLastname() != null && !request.getLastname().equals("")) {
                user.setLastname(request.getLastname());
            }

            if(request.getFirstname() != null && !request.getFirstname().equals("")) {
                user.setFirstname(request.getFirstname());
            }

            if(request.getPassword() != null && !request.getPassword().equals("")) {
                user.setPassword(request.getPassword());
            }

            if(request.getAvatar() != null && !request.getAvatar().equals("")) {
                user.setProfile_image_url(request.getAvatar());
            }

            transaction.commit();

        } catch (CantBeModifyException e) {
            if(transaction != null) {
                transaction.rollback();
            }

            throw e;

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }

            action = VerifyNeed.SOMETHING_WRONG;

        } finally {
            session.close();
        }

        return action;
    }
}
