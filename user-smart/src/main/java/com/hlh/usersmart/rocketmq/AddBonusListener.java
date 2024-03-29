package com.hlh.usersmart.rocketmq;

import com.hlh.usersmart.domain.entity.BonusEventLog;
import com.hlh.usersmart.domain.entity.User;
import com.hlh.usersmart.domain.entity.dto.UserAddBonusDto;
import com.hlh.usersmart.repository.BonusEventLogRepository;
import com.hlh.usersmart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author w2gd
 */
@Service
@RocketMQMessageListener(consumerGroup = "consumer", topic = "add-bonus")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddBonusListener implements RocketMQListener<UserAddBonusDto> {

    private final UserRepository userRepository;

    private final BonusEventLogRepository bonusEventLogRepository;

    @Override
    public void onMessage(UserAddBonusDto userAddBonusDto) {
        Integer userId = userAddBonusDto.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setBonus(user.getBonus() + userAddBonusDto.getBonus());
            userRepository.saveAndFlush(user);
        }
        bonusEventLogRepository.saveAndFlush(BonusEventLog.builder()
                .userId(userId).event("CONTRIBUTE").value(userAddBonusDto.getBonus().toString())
                .createTime(new Date())
                .description("投稿积分")
                .build());
    }
}
