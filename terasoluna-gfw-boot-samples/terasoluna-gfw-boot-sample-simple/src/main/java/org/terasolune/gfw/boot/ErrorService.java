package org.terasolune.gfw.boot;

import org.springframework.stereotype.Service;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessage;
import org.terasoluna.gfw.common.message.ResultMessages;

@Service
public class ErrorService {
    public void throwBusinessException() {
        ResultMessages messages = ResultMessages.error()
                .add(ResultMessage.fromText("Error!"));
        throw new BusinessException(messages);
    }
}
