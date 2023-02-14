package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BaseResponse;
import org.example.exception.CustomAppException;
import org.example.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
////public class UserValidationFilter extends GenericFilterBean {
////public class UserValidationFilter extends OncePerRequestFilter {
//public class UserValidationFilter implements Filter {
//    @Autowired
//    private ObjectMapper objectMapper;
//
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("PRE >" + SecurityContextHolder.getContext() + "< -" + SecurityContextHolder.getContext().getAuthentication() + "-");
//        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
//            System.out.println("AFTER " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//
//            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
////            request.setAttribute(ERROR_STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
////            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User with id: " +
////                    ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("preferred_username") + " not synchronized yet.");
////            response.flushBuffer();
//            BaseResponse<Object> resp  = BaseResponse.builder()
//                    .timestamp(Instant.now())
//                    .errors(Set.of("User with id: " +
//                            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("preferred_username") + " not synchronized yet."))
//                    .message(Utils.getMessageForStatus(HttpStatus.FORBIDDEN))
//                    .errorType(log.isErrorEnabled() ? getClass().getSimpleName() : null)
//                    .build();
////            request.setAttribute(ERROR_MESSAGE, objectMapper.writeValueAsString(resp));
//            response.getOutputStream().write(objectMapper.writeValueAsString(resp).getBytes(StandardCharsets.UTF_8));
////            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, objectMapper.writeValueAsString(resp));
//            SecurityContextHolder.getContext().setAuthentication(null);
//            return;
//        }
//        chain.doFilter(request, response);
//    }
//}

//public class UserValidationFilter extends GenericFilterBean {
public class UserValidationFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("PRE >" + SecurityContextHolder.getContext() + "< -" + authentication + "-");
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            chain.doFilter(request, response);
            return;
        } else if (authentication.getPrincipal() != null) {
            System.out.println("AFTER " + authentication.getPrincipal());

//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            request.setAttribute(ERROR_STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User with id: " +
//                    ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("preferred_username") + " not synchronized yet.");
//            response.flushBuffer();

//            final BaseResponse<Object> resp  = BaseResponse.builder()
//                    .timestamp(Instant.now())
//                    .errors(Set.of("User with id: " +
//                            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims().get("preferred_username") + " not synchronized yet."))
//                    .message(Utils.getMessageForStatus(HttpStatus.FORBIDDEN))
//                    .errorType(log.isErrorEnabled() ? getClass().getSimpleName() : null)
//                    .build();
//            request.setAttribute(ERROR_MESSAGE, objectMapper.writeValueAsString(resp));
//            response.getOutputStream().write(objectMapper.writeValueAsString(resp).getBytes(StandardCharsets.UTF_8));
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, objectMapper.writeValueAsString(resp));

            response.sendError(HttpServletResponse.SC_FORBIDDEN, ((Jwt) authentication.getPrincipal()).getClaims().get("preferred_username").toString());
//            response.getOutputStream().write(objectMapper.writeValueAsString(resp).getBytes(StandardCharsets.UTF_8));
//            throw new CustomAppException(HttpStatus.FORBIDDEN, "TEST");

//            SecurityContextHolder.getContext().setAuthentication(null);
            return;
        }
        chain.doFilter(request, response);
    }
}
