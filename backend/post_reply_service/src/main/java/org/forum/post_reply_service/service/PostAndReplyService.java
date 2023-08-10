package org.forum.post_reply_service.service;

import org.forum.post_reply_service.constant.AccountActive;
import org.forum.post_reply_service.constant.AuthorityConstant;
import org.forum.post_reply_service.dto.request.post.ReplyPostRequest;
import org.forum.post_reply_service.dto.request.post.SavePostRequest;
import org.forum.post_reply_service.dto.response.post.CreatePostResponseDTO;
import org.forum.post_reply_service.entity.Post;
import org.forum.post_reply_service.entity.PostReply;
import org.forum.post_reply_service.entity.PostStatus;
import org.forum.post_reply_service.entity.SubReply;
import org.forum.post_reply_service.exception.ModifyImmutableResourceException;
import org.forum.post_reply_service.exception.PostStateTransferException;
import org.forum.post_reply_service.exception.ResourceNotFoundException;
import org.forum.post_reply_service.exception.UnauthorizedException;
import org.forum.post_reply_service.repo.PostAndReplyRepository;
import org.forum.post_reply_service.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.sort;

@Service
public class PostAndReplyService {
    private PostAndReplyRepository postAndReplyRepository;
    @Autowired
    public void setPostAndReplyRepository(PostAndReplyRepository postAndReplyRepository){
        this.postAndReplyRepository = postAndReplyRepository;
    }

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public CreatePostResponseDTO createNewPost(AuthUserDetail authUserDetail){
        checkUserActive(authUserDetail);
        Post post = Post.builder()
                .userId(authUserDetail.getUserID())
                .isArchived(false)
                .status(PostStatus.UNPUBLISHED)
                .dateCreated(Date.from(Instant.now()))
                .dateModified(Date.from(Instant.now()))
                .images(new ArrayList<>())
                .attachments(new ArrayList<>())
                .postReplies(new ArrayList<>())
                .build();
        Post savedPost = postAndReplyRepository.save(post);
        return CreatePostResponseDTO
                .builder()
                .postId(savedPost.getId())
                .userId(savedPost.getUserId())
                .build();
    }

    @Transactional
    public void saveOrPublishPost(String postId, SavePostRequest request, AuthUserDetail userDetail){
        checkUserActive(userDetail);
        Optional<Post> optionalPost = postAndReplyRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResourceNotFoundException("Post not found!");
        }
        Post post = optionalPost.get();
        // This post doesn't belong to current user
        if(!post.getUserId().equals(userDetail.getUserID()))
            throw new ResourceNotFoundException("Post not found!");
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        post.setAttachments(request.getAttachments());
        post.setDateModified(Date.from(Instant.now()));
        if(request.isToPublish()){
            post.setStatus(PostStatus.PUBLISHED);
        }
        postAndReplyRepository.save(post);
    }

    @Transactional
    public void replayPost(ReplyPostRequest request, AuthUserDetail userDetail, String postId){
        checkUserActive(userDetail);
        Post post = getPostById(postId);
        if(post.getIsArchived())
            throw new ModifyImmutableResourceException("This post is archived!");
        if(!post.getStatus().equals(PostStatus.PUBLISHED))
            throw new ModifyImmutableResourceException("This post is private");
        PostReply reply = PostReply.builder()
                        .userId(userDetail.getUserID())
                        .comment(request.getComment())
                        .isActive(true)
                        .dateCreated(Date.from(Instant.now()))
                        .subReplies(new ArrayList<>())
                        .build();
        if(post.getPostReplies() == null){
            post.setPostReplies(new ArrayList<>());
        }
        post.getPostReplies().add(reply);
        postAndReplyRepository.save(post);
    }

    @Transactional
    public void replayReplay(ReplyPostRequest request, Integer replyIndex, AuthUserDetail userDetail, String postId){
        checkUserActive(userDetail);
        Post post = getPostById(postId);
        if(post.getIsArchived())
            throw new ModifyImmutableResourceException("This post is archived!");
        if(post.getStatus() != PostStatus.PUBLISHED)
            throw new ModifyImmutableResourceException("This post is private");
        List<PostReply> postReplies = post.getPostReplies();
        if(postReplies == null || postReplies.size() <= replyIndex)
            throw new ResourceNotFoundException("Reply not found!");
        if(!postReplies.get(replyIndex).getIsActive())
            throw new ModifyImmutableResourceException("This reply is inactive!");
        SubReply subReply = SubReply.builder()
                .userId(userDetail.getUserID())
                .comment(request.getComment())
                .isActive(true)
                .dateCreated(Date.from(Instant.now()))
                .build();

        List<SubReply> subReplies = postReplies.get(replyIndex).getSubReplies();
        if(subReplies == null) {
            subReplies = new ArrayList<>();
            subReplies.add(subReply);
            postReplies.get(replyIndex).setSubReplies(subReplies);
        } else {
            subReplies.add(subReply);
        }
        postAndReplyRepository.save(post);
    }

    @Transactional
    public void changePostStatus(String postId, PostStatus postStatus, AuthUserDetail userDetail){
        checkUserActive(userDetail);
        boolean isUser = userDetail.getAuthorities().stream().filter(a -> a.getAuthority().equals(AuthorityConstant.AUTHORITY_USER)).count() > 0;
        boolean isAdmin = userDetail.getAuthorities().stream().filter(a -> a.getAuthority().equals(AuthorityConstant.AUTHORITY_ADMIN)).count() > 0;
        Post post = getPostById(postId);
        switch (postStatus){
            case BANNED:
                if(!post.getStatus().equals(PostStatus.PUBLISHED))
                    throw new PostStateTransferException("Post state transfer illegal");
                if(!isAdmin)
                    throw new UnauthorizedException("Not authorized");
                break;
            case HIDDEN:
                if(!post.getStatus().equals(PostStatus.PUBLISHED))
                    throw new PostStateTransferException("Post state transfer illegal");
                if(!post.getUserId().equals(userDetail.getUserID())) // no need for authority check since id checked
                    throw new ResourceNotFoundException("You don't have this post!");
                break;
            case DELETED:
                if(!post.getStatus().equals(PostStatus.PUBLISHED))
                    throw new PostStateTransferException("Post state transfer illegal");
                if(!post.getUserId().equals(userDetail.getUserID()))
                    throw new ResourceNotFoundException("You don't have this post!");
                if(!isUser)
                    throw new UnauthorizedException("Not authorized");
                break;
            case PUBLISHED:
                if(post.getStatus().equals(PostStatus.PUBLISHED))
                    throw new PostStateTransferException("Post state transfer illegal");
                if((post.getStatus().equals(PostStatus.UNPUBLISHED) || post.getStatus().equals(PostStatus.HIDDEN))
                        && !post.getUserId().equals(userDetail.getUserID()))
                    throw new ResourceNotFoundException("You don't have this post!");
                if((post.getStatus().equals(PostStatus.DELETED) || post.getStatus().equals(PostStatus.BANNED)) && !isAdmin )
                    throw new UnauthorizedException("Not authorized");
                break;
            case UNPUBLISHED:
                throw new PostStateTransferException("Post state transfer illegal");
        }
        post.setStatus(postStatus);
        postAndReplyRepository.save(post);
    }

    @Transactional
    public void toggleArchive(String postId, AuthUserDetail userDetail){
        checkUserActive(userDetail);
        Post post = getPostById(postId);
        if(!post.getUserId().equals(userDetail.getUserID()))
            throw new ResourceNotFoundException("Post not found!");
        post.setIsArchived(!post.getIsArchived());
        postAndReplyRepository.save(post);
    }

    Post getPostById(String postId){
        Optional<Post> optionalPost = postAndReplyRepository.findById(postId);
        if(!optionalPost.isPresent())
            throw new ResourceNotFoundException("Post not found!");
        return optionalPost.get();
    }

    public List<Post> getAllPost(AuthUserDetail userDetail){
        return postAndReplyRepository.findByStatusOrderByDateModifiedDesc(PostStatus.PUBLISHED);
    }

    public List<Post> getPostByStatus(PostStatus status){
        return postAndReplyRepository.findByStatusOrderByDateModifiedDesc(status);
    }

    public List<Post> getPostByStatusUserID(AuthUserDetail userDetail, PostStatus status){
        return postAndReplyRepository.findByUserIdAndStatusOrderByDateModifiedDesc(userDetail.getUserID(), status);
    }

    public List<Post> getUserAllDrafts(AuthUserDetail userDetail){
        return postAndReplyRepository.findByUserIdAndStatusOrderByDateModifiedDesc(userDetail.getUserID(), PostStatus.UNPUBLISHED);
    }

    public Post getPublishedPostByID(String postId){
        Optional<Post> post = postAndReplyRepository.findByIdAndStatus(postId, PostStatus.PUBLISHED);
        return post.isPresent() ? post.get() : null;
    }

    public List<Post> getTop3RepliedPosts(){
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("status").is(PostStatus.PUBLISHED)
                                .and("isArchived").is(false)
                ),
                Aggregation.unwind("postReplies"),
                Aggregation.group("_id")
                        .first("title").as("title")
                        .first("content").as("content")
                        .first("dateCreated").as("dateCreated")
                        .first("dateModified").as("dateModified")
                        .push("postReplies").as("postReplies")
                        .count().as("numReplies"),
                Aggregation.sort(Sort.Direction.DESC, "numReplies"),
                Aggregation.limit(3)
        );

        AggregationResults<Post> results = mongoTemplate.aggregate(agg, Post.class, Post.class);

        return results.getMappedResults();
    }

    private void checkUserActive(AuthUserDetail userDetail){
        if(userDetail.getActive() != AccountActive.ACTIVE)
            throw new UnauthorizedException(userDetail.getActive() == AccountActive.INACTIVE
                    ? "Need to verify your email!"
                    : "You've been banned");
    }
}
