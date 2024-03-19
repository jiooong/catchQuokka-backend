package com.example.demo.global.config;

import com.example.demo.domain.room.handler.MyTextHandler;
import com.example.demo.global.stomp.handler.MyStompSubProtocolErrorHandler;
import com.example.demo.global.stomp.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    private final MyStompSubProtocolErrorHandler stompSubProtocolErrorHandler;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher(".")); // URL을 / -> .으로
        registry.setApplicationDestinationPrefixes("/pub");     //클라이언트에서 보낸 메세지를 받을 prefix
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");//simplebroker 기능과 외부 메시지 브로커에 메시지를 전달하는 기능
        //registry.enableSimpleBroker("/sub");    //해당 주소를 구독하고 있는 클라이언트들에게 메세지 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("WebSocket 연결");
        registry.addEndpoint("/api/v1/room")   //SockJS 연결 주소
            .setAllowedOriginPatterns("**")           //모든 요청 수락
//            .withSockJS() //버전 낮은 브라우저에서도 적용 가능()
                            //구독자가 존재하지 않을 경우(Invalid SockJS path - required to have 3 path segments)
        ;
        registry.setErrorHandler(stompSubProtocolErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
