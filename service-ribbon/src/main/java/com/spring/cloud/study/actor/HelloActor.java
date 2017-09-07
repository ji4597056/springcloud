package com.spring.cloud.study.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.spring.cloud.study.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Jeffrey
 * @since 2017/08/30 15:59
 */
@Component("helloActor")
@Scope("prototype")
public class HelloActor extends AbstractActor {

    @Autowired
    private HelloService helloService;

    public static Props props() {
        return Props.create(HelloActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, s -> getSender().tell(helloService.hiRemoteService(s), getSelf()))
            .build();
    }
}
