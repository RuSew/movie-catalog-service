package com.tutorial.moviecatalogservice.resources;

import com.tutorial.moviecatalogservice.models.CatalogItem;
import com.tutorial.moviecatalogservice.models.Movie;
import com.tutorial.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // get all rated movie ids
        List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );

        // for each movie id, call movie info service and get the details
        return ratings.stream().map(rating -> {
//                    Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
                    Movie movie = webClientBuilder.build()
                            .get()// get request
                            .uri("http://localhost:8082/movies/" + rating.getMovieId())// request url
                            .retrieve()// fetch
                            .bodyToMono(Movie.class)// convert the body to an instance of the object. Mono is like a promise, asynchronous request.
                            .block();// block the execution until a list of CatalogItem is received

                    return new CatalogItem(movie.getName(), "Test", rating.getRating());
                })
                .collect(Collectors.toList());

        // put them all together

    }
}
