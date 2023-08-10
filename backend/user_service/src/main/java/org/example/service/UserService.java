package org.example.service;

import org.example.dao.UserDao;
import org.example.domain.User;
import org.example.dto.request.EditProfileRequest;
import org.example.exception.CantBeModifyException;
import org.example.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUserUsingUsername(String email) {
        return userDao.getUserUsingEmail(email);
    }

    public boolean registerAuser(User user) {
        return userDao.registerAuser(user);
    }

    public User getUserByUserID(int id) {
        return userDao.getUserUsingUserID(id);
    }

    public List<User> getAllUsers(){
        return userDao.getAllUsers();
    }

    public boolean setActiveStatus(AuthUserDetail authUserDetail, int id, int active) throws CantBeModifyException {
        return userDao.setActiveStatus(authUserDetail, id, active); //either admin or super_admin
    }

    public boolean activateAUserByEmail(AuthUserDetail authUserDetail, int userID) throws CantBeModifyException {
        return userDao.setActiveStatus(authUserDetail, userID,1);
    }

    public boolean adminAssign_Or_revoke(AuthUserDetail authUserDetail, int id, String type)
            throws CantBeModifyException {
        return userDao.adminAssign_Or_revoke(authUserDetail, id,type);
    }

    public boolean activateUserByUserEmail(String emailAddr) throws CantBeModifyException {

        return userDao.activateUserByUserEmail(emailAddr);
    }

    public int editUserProfileInfo(int userID, EditProfileRequest request) throws CantBeModifyException {
        return userDao.editUserProfileInfo(userID, request);
    }
}
