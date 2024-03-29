package com.hlh.user.auth;


import com.hlh.user.utils.JwtOperator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author hlh
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAspect {
    private final JwtOperator jwtOperator;

    @Around("@annotation(com.hlh.user.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable{
        checkToken();
        return point.proceed();
    }
    @Around("@annotation(com.hlh.user.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point) throws Throwable{
        try{
            //验证token是否合法
            this.checkToken();
            //验证用户角色是否匹配
            HttpServletRequest request = getHttpServletRequest();
            String role = (String) request.getAttribute("role");

            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);

            String value = annotation.value();
            if (!Objects.equals(role, value)){
                throw new SecurityException("用户无权访问!");
            }
        }catch (Throwable throwable){
            throw new SecurityException("用户无权访问!", throwable);
        }
        return point.proceed();
    }
    private void checkToken(){
        try {
            //从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("x-token");
            //校验token是否合法&是否过期：如果不合法或已经过期就直接抛出异常
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid){
                throw new SecurityException("Token不合法!");
            }
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("nickname", claims.get("nickname"));
            request.setAttribute("role", claims.get("role"));
        }catch (Throwable throwable){
            throw new SecurityException("Token不合法");
        }
    }
    private HttpServletRequest getHttpServletRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        assert attributes != null;
        return attributes.getRequest();

    }
}
