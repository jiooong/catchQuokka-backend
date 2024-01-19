package com.example.demo.global.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOauth {
    String getOauthRedirectURL(){
        return "";
    }
}
