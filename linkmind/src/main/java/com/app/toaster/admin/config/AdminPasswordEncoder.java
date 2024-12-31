package com.app.toaster.admin.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminPasswordEncoder {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public GoogleAuthenticator googleAuthenticator(){
        return new GoogleAuthenticator();
    }


}
