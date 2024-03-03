package com.example.simpletravel.event;

import com.example.simpletravel.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SignupEvent extends ApplicationEvent {
    private User user;
    private String requestUrl;

    public SignupEvent(Object source, User user, String requestUrl){
        super(source);

        this.user=user;
        this.requestUrl=requestUrl;
    }
}
