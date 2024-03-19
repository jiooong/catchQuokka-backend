package com.example.demo.global.stomp.handler;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;

// 프로토콜 레벨에서 발생하는 레벨을 처리
@Slf4j
@RequiredArgsConstructor
@Component
public class MyStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler {

//    @param clientMessage 클라이언트 메시지
//    @param ex 발생한 예외
//    @return 오류 메시지를 포함한 Message 객체
    @Override
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        //클라이언트 메세지를 처리하는 동안 발생하는 오류를 처리
        //throw new MessageDeliveryException("UNAUTHORIZED")

        if ("UNAUTHORIZED".equals(ex.getMessage())) {
            return errorMessage("권한이 유효하지 않습니다");
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
    private Message<byte[]> errorMessage(String errorMessage) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        //바이트 배열로 전송해야함
        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }

}
