package com.spring.cloud.study.utils;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import com.spring.cloud.study.utils.SpringExtension.SpringExt;
import org.springframework.context.ApplicationContext;

/**
 * @author Jeffrey
 * @since 2017/08/30 16:34
 */
public class SpringExtension extends AbstractExtensionId<SpringExt> {

    public static final SpringExtension SpringExtProvider = new SpringExtension();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public static class SpringExt implements Extension {

        private volatile ApplicationContext applicationContext;

        public void initialize(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public Props props(String actorBeanName) {
            return Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
        }
    }
}

