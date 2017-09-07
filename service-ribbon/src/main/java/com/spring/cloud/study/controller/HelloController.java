package com.spring.cloud.study.controller;

import static com.spring.cloud.study.utils.SpringExtension.SpringExtProvider;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import co.paralleluniverse.fibers.FiberUtil;
import co.paralleluniverse.fibers.SuspendExecution;
import com.spring.cloud.study.service.HelloService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * @author Jeffrey
 * @since 2017/05/10 13:57
 */
@RestController
public class HelloController {

    /**
     * thread pool
     */
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(300);

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name) {
        return helloService.hiService(name);
    }

    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(required = false, defaultValue = "false") Boolean origin) {
        return origin ? helloService.originHelloService() : helloService.helloService();
    }

    /*============================================================*/

    @GetMapping("/test/future")
    public String testFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture
            .supplyAsync(() -> helloService.hiRemoteService("JJ,"), threadPool);
        CompletableFuture<String> future2 = CompletableFuture
            .supplyAsync(() -> helloService.hiRemoteService("future"), threadPool);
        return future1.thenCombine(future2, (f1, f2) -> f1 + f2).get();
    }

    @GetMapping("/test/quasar")
    public String testQuasar() throws ExecutionException, InterruptedException {
        return FiberUtil
            .runInFiber(() -> helloService.hiRemoteService("JJ,")
                + helloService.hiRemoteService("quasar"));
    }

    @GetMapping("/test/quasar2")
    public String testQuasar2() throws ExecutionException, InterruptedException {
        return FiberUtil.runInFiber(() -> helloService.hiRemoteService("JJ,")) + FiberUtil
            .runInFiber(() -> helloService.hiRemoteService("quasar"));
    }

    @GetMapping("/test/akka")
    public String testAkka() throws Exception {
        Future<Object> future1 = Patterns
            .ask(actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("helloActor")),
                "JJ", 10000);
        Future<Object> future2 = Patterns
            .ask(actorSystem.actorOf(SpringExtProvider.get(actorSystem).props("helloActor")),
                "akka", 10000);
        Duration duration = Duration.create(10, TimeUnit.SECONDS);
        return Await.result(future1, duration) + (String) Await.result(future2, duration);
    }

    /*============================================================*/

    @GetMapping("/test/hi/origin")
    public String testQuasarHi() throws InterruptedException, SuspendExecution {
        return helloService.hiRemoteService("quasar-hi");
    }

    @GetMapping("/test/hi/quasar")
    public String testFutureHi() throws ExecutionException, InterruptedException {
        return FiberUtil.runInFiber(() -> helloService.hiRemoteService("quasar-hi"));
    }

}
