package com.hlh.contentsmart.openfeign.fallback;

import com.hlh.contentsmart.openfeign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * @author w2gd
 */
@Slf4j
@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
    /**
     * 可以捕捉到具体到异常
     * @param cause 原因
     * @return UserService
     */

    @Override
    public UserService create(Throwable cause) {
        return null;
    }
    // @Override
    // public UserService create(Throwable cause) {
    //     return id -> {
    //         log.info("fallback factory method test" + cause);
    //         User user = User.builder().nickname("降级方案返回用户").avatar("http://img.w2gd.top/up/lem2.png").build();
    //         return ResponseResult.success(user);
    //     };
    // }
}
