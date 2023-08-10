package org.forum.post_reply_service.exception;

public class ModifyImmutableResourceException extends RuntimeException{
    public ModifyImmutableResourceException(String msg){
        super(msg);
    }
}
