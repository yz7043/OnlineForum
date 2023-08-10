package org.example.service;

import net.minidev.json.JSONObject;
import org.apache.catalina.User;
import org.example.common.CommonResponse;
import org.example.common.StatusResponse;
import org.example.constant.DefaultProfileImage;
import org.example.dao.CompPostUserDao;
import org.example.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.LinkedHashMap;
import java.util.List;

@Service
public class CompPostUserService {

    private CompPostUserDao compPostUserDao;

    @Autowired
    public void setCompPostUserDao(CompPostUserDao compPostUserDao) {
        this.compPostUserDao = compPostUserDao;
    }

    public List<LinkedHashMap<String, Object>> getAllPosts(AuthUserDetail authUserDetail) {
        CommonResponse posts = compPostUserDao.getAllPosts(authUserDetail);
        return addUserPropertiesToPosts(posts,authUserDetail);
    }

    public LinkedHashMap<String, Object> getPostByPostID(String postID, AuthUserDetail authUserDetail) {
        CommonResponse post = compPostUserDao.getPostByPostID(postID, authUserDetail);
        LinkedHashMap<String, Object> post_data = (LinkedHashMap<String, Object>) post.getData();

        if(post_data.size() == 0) {
            return post_data;
        }

        addUserPropertiesToAPost(post_data, authUserDetail);
        return post_data;
    }

    public List<LinkedHashMap<String, Object>> getTopReplyPost(AuthUserDetail authUserDetail) {
        CommonResponse posts = compPostUserDao.getTopReplyPost(authUserDetail);
        return addUserPropertiesToPosts(posts,authUserDetail);
    }

    public List<LinkedHashMap<String, Object>> getPostByStatus(AuthUserDetail authUserDetail, String status) {
        CommonResponse posts = compPostUserDao.getPostByStatus(authUserDetail, status);
        return addUserPropertiesToPosts(posts,authUserDetail);
    }


    //HELPER
    private void addUserPropertiesToAPost(LinkedHashMap<String, Object> post, AuthUserDetail authUserDetail) {

//        addUserPropertiesToMap(post, authUserDetail);
        //reply
        List<LinkedHashMap<String, Object>> postReplies
                = (List<LinkedHashMap<String, Object>>) post.get("postReplies");

        if (postReplies == null || postReplies.size() == 0) {
            System.out.println("No replies");
            return;
        }

        for (LinkedHashMap<String, Object> postReply : postReplies) {

            addUserPropertiesToMap(postReply, authUserDetail);

            //sub-secondary
            List<LinkedHashMap<String, Object>> subReplies
                    = (List<LinkedHashMap<String, Object>>) postReply.get("subReplies");

            if (subReplies == null || subReplies.size() == 0) {
                System.out.println("No sub replies");
                continue;
            }

            for (LinkedHashMap<String, Object> subReply : subReplies) {
                addUserPropertiesToMap(subReply, authUserDetail);
            }
        }
    }

    private List<LinkedHashMap<String, Object>> addUserPropertiesToPosts(CommonResponse posts,
                                                                         AuthUserDetail authUserDetail) {
        List<LinkedHashMap<String, Object>> posts_arr = (List<LinkedHashMap<String, Object>>) posts.getData();

        //What should be the empty list considered as?
        if(posts_arr == null || posts_arr.size() == 0) {
            return posts_arr;
        }

        for(LinkedHashMap<String, Object> post : posts_arr) {
            // master post
            addUserPropertiesToMap(post, authUserDetail);
        }

        return posts_arr;
    }


    //helper functions
    private void addUserPropertiesToMap(LinkedHashMap<String, Object> map, AuthUserDetail authUserDetail) {
        Integer userID = (Integer) map.get("userId");

        if(userID == null) {
            map.put("lastname", "unknown");
            map.put("firstname", "unknown");
            map.put("profile_image_url", DefaultProfileImage.DEFAULT_IMAGE_URL);
            return;
        }

        CommonResponse userResponse = compPostUserDao.getUserByUserID(authUserDetail, userID);
        LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) userResponse.getData();

        map.put("lastname", user.get("lastname"));
        map.put("firstname", user.get("firstname"));
        map.put("profile_image_url", user.get("profile_image_url"));
    }



}
