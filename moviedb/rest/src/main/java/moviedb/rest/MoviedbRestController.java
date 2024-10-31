package moviedb.rest;

import moviedb.core.MovieDatabase;
import moviedb.core.SeriesDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MoviedbRestController.SERVICE_PATH)
public class MoviedbRestController {

    private final MovieDatabaseRepository movieDatabaseRepository;
    private final SeriesDatabaseRepository seriesDatabaseRepository;

    public static final String SERVICE_PATH = "/moviedb";

    public MoviedbRestController(MovieDatabaseRepository movieDatabaseRepository,
            SeriesDatabaseRepository seriesDatabaseRepository) {
        this.movieDatabaseRepository = movieDatabaseRepository;
        this.seriesDatabaseRepository = seriesDatabaseRepository;
    }

    public MoviedbRestController() {
        this(new MovieDatabaseRepository(), new SeriesDatabaseRepository());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/movies")
    public MovieDatabase getMovies() {
        return movieDatabaseRepository.getMovies();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/movies")
    public void setMovies(@RequestBody MovieDatabase movies) {
        movieDatabaseRepository.setMovies(movies);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/series")
    public SeriesDatabase getSeries() {
        return seriesDatabaseRepository.getSeries();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/series")
    public void setSeries(@RequestBody SeriesDatabase series) {
        seriesDatabaseRepository.setSeries(series);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ping")
    public String getStatus() {
        return "pong";
    }

}
