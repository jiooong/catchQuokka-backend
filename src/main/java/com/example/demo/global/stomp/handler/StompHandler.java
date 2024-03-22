package com.example.demo.global.stomp.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

  // Socket 통신 상태
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if(StompCommand.CONNECT.equals(accessor.getCommand())){
      log.info("CONNECT");
    }else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
      log.info("SUBSCRIBE");
    }else if(StompCommand.DISCONNECT.equals(accessor.getCommand())){ // 연결 종료
      log.info("DISCONNECT");
      // 토큰으로 사용자가 누군지 확인하기
      // 방 나갔다 !

    }
    return message;
  }
}