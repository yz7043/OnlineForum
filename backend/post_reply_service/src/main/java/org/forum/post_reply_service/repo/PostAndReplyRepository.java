package org.forum.post_reply_service.repo;

import org.forum.post_reply_service.entity.Post;
import org.forum.post_reply_service.entity.PostStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostAndReplyRepository extends MongoRepository<Post, String> {
    List<Post> findByStatusOrderByDateModifiedDesc(PostStatus status);

    List<Post> findByUserIdAndStatusOrderByDateModifiedDesc(Integer userId, PostStatus status);

    Optional<Post> findByIdAndStatus(String id, PostStatus status);
}
