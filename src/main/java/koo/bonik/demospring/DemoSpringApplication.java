package koo.bonik.demospring;

import koo.bonik.demospring.model.GithubCommit;
import koo.bonik.demospring.model.GithubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@SpringBootApplication
public class DemoSpringApplication {

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    WebClient.Builder webClientBuild;

    String repositoryUrl = "https://api.github.com/users/Koobonik/repos";

    String commitUrl = "https://api.github.com/repos/Koobonik/Spring_Boot/commits";

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return args -> {
            StopWatch stopWatch = new StopWatch();

            stopWatch.start();

            WebClient webClient = webClientBuild.baseUrl("https://api.github.com").build();

//            Mono<String[]> stringMono = webClient.get().uri("https://cafecostes.com/practice/getMenu")
//                    .retrieve()
//                    .bodyToMono(String[].class);
//
//            stringMono.subscribe(sm -> {
//               System.out.println(sm);
//            });

//            System.out.println(stringMono);

            // 비동기적으로 받으려면 웹클라이언트 쓰는게 좋음
            // Mono 를 이용하여 받는 방법 - 다 받으면 출력
            Mono<GithubRepository[]> reposMono = webClient.get().uri("/users/Koobonik/repos")
                    .retrieve()
                    .bodyToMono(GithubRepository[].class);

            Mono<GithubCommit[]> commitsMono = webClient.get().uri("repos/Koobonik/Spring_Boot/commits")
                    .retrieve()
                    .bodyToMono(GithubCommit[].class);

            reposMono.doOnSuccess(ra -> {
               Arrays.stream(ra).forEach(r ->{
                   System.out.println("repo : " + r.getUrl());
               });
            }).subscribe();

            commitsMono.doOnSuccess(ca ->{
                Arrays.stream(ca).forEach(c -> {
                    System.out.println("commits : " + c.getSha());
                });
            }).subscribe();



            // Flux 를 이용하여 받는 방법 - 받을 때 마다 출력
            Flux<GithubRepository> reposFlux = webClient.get().uri("/users/Koobonik/repos")
                    .retrieve()
                    .bodyToFlux(GithubRepository.class);

            reposFlux.subscribe(r -> {
               System.out.println("리포 : " + r.getUrl());
            });



            Flux<GithubCommit> commitsFlux = webClient.get().uri("repos/Koobonik/Spring_Boot/commits")
                    .retrieve()
                    .bodyToFlux(GithubCommit.class);


            commitsFlux.subscribe(r -> {
               System.out.println("겟샤 : " + r.getSha());
            });

            reposFlux.doOnNext(r -> {
                System.out.println("repo : " + r.getUrl());
            }).subscribe();

            commitsFlux.doOnNext(ca ->{
                System.out.println("commits : " + ca.getSha());
            }).subscribe();



            // RestTemplate 이용하면 동기식으로 받음
//            RestTemplate restTemplate = restTemplateBuilder.build();
//
//            GithubRepository[] result = restTemplate.getForObject(repositoryUrl, GithubRepository[].class);
//            // String result = restTemplate.getForObject("https://cafecostes.com/practice/getMenu", String.class);
//            System.out.println(result);
//
//            Arrays.stream(result).forEach(r -> {
//                System.out.println("repo : " + r.getUrl());
//            });
//
//            GithubCommit[] commit = restTemplate.getForObject( commitUrl, GithubCommit[].class);
//            Arrays.stream(commit).forEach(c -> {
//                System.out.println("repo : " + c.getSha());
//            });

            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeSeconds());


        };
    }

}
