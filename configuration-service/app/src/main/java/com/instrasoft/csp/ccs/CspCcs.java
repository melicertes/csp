package com.instrasoft.csp.ccs;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableTransactionManagement
public class CspCcs {

//    @Bean
//    public Converter<String, Message> messageConverter() {
//        return new Converter<String, Message>() {
//            @Override
//            public Message convert(String id) {
//                return messageRepository().findMessage(Long.valueOf(id));
//            }
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(CspCcs.class, args);
    }

}
