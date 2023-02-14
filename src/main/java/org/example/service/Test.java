package org.example.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class Test {
    public Object test() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("LDAP_ID");
    }
}
