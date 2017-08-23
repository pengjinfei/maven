package com.pengjinfei.maven.configuration.integration;

import lombok.Data;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.util.StringUtils;

/**
 * Created on 8/23/17
 *
 * @author Pengjinfei
 */
@Data
public class MybatisUpdateRecoveryCallback implements RecoveryCallback {

    private String sql;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Object recover(RetryContext retryContext) throws Exception {
        if (StringUtils.hasText(sql)) {
            GenericMessage message = (GenericMessage) retryContext.getAttribute("message");
            sqlSessionTemplate.update(sql, message.getPayload());
        }
        return null;
    }
}
