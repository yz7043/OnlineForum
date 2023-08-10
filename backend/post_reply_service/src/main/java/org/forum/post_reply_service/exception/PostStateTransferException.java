package org.forum.post_reply_service.exception;

public class PostStateTransferException extends RuntimeException{
    public PostStateTransferException(String msg){
        super(msg);
    }
}
