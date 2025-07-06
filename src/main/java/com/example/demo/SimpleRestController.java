package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class SimpleRestController {
    private static final Logger log = LoggerFactory.getLogger(SimpleRestController.class);

    @GetMapping("/hello")
    public String helloWorld(ServerHttpRequest request) {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        log.info("request by {}", ip);
        return "hello!" + ip;
    }

    public record EchoPayload(
        String content
    ){}

    @PostMapping("/echo")
    public String echo(ServerHttpRequest request, @RequestBody EchoPayload body) {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        log.info("request by {}", ip);
        return "reply " + body.content;
    }

    public record ProxyGetPayload(
        String uri
    ) {}

    @PostMapping("/proxy/get")
    public Mono<String> proxyGet(ServerHttpRequest request, @RequestBody ProxyGetPayload body) {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        log.info("request by {}", ip);
        return WebClient.create().get()
            .uri(body.uri)
            .retrieve()
            .bodyToMono(String.class);
    }

    public record ProxyPostPayload(
        String uri,
        String content
    ) {}

    @PostMapping("/proxy/post")
    public Mono<String> proxyPost(ServerHttpRequest request, @RequestBody ProxyPostPayload body) {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        log.info("request by {}", ip);
        return WebClient.create().post()
            .uri(body.uri)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new EchoPayload(body.content))
            .retrieve()
            .bodyToMono(String.class);
    }
}
