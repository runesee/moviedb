package moviedb.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import moviedb.json.JsonFileHandler;

public class MoviedbWebClient {

    private MovieDatabase movieDatabase;
    private SeriesDatabase seriesDatabase;
    private static ObjectMapper movieObjectMapper = JsonFileHandler.createMovieObjectMapper();
    private static ObjectMapper seriesObjectMapper = JsonFileHandler.createSeriesObjectMapper();
    private final URI baseUri;

    public MoviedbWebClient(String baseUri) {
        this.baseUri = URI.create(baseUri);
    }

    /**
     * Method for geting the movie database.
     * 
     * @return MovieDatabase
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    public MovieDatabase getMovieDatabase() throws IOException, InterruptedException {
        if (movieDatabase == null) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUri + "/movies"))
                    .GET()
                    .build();
            final HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                    HttpResponse.BodyHandlers.ofString());
            movieDatabase = movieObjectMapper.readValue(response.body(), MovieDatabase.class);
        }
        return movieDatabase;
    }

    /**
     * Method for geting the series database.
     * 
     * @return SeriesDatabase
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    public SeriesDatabase getSeriesDatabase() throws IOException, InterruptedException {
        if (seriesDatabase == null) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUri + "/series"))
                    .GET()
                    .build();
            final HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                    HttpResponse.BodyHandlers.ofString());
            seriesDatabase = seriesObjectMapper.readValue(response.body(), SeriesDatabase.class);
        }
        return seriesDatabase;
    }

    /**
     * Method that updates remote server to match that of the movieDatabase taken as
     * parameter.
     * 
     * @param movieDatabase the database for the movie
     */
    public void updateRemoteMovies(MovieDatabase movieDatabase) {
        String json;
        try {
            json = movieObjectMapper.writeValueAsString(movieDatabase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUri + "/movies"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that updates remote server to match that of the seriesDatabase taken.
     * as parameter.
     * 
     * @param seriesDatabase the database for the series
     */
    public void updateRemoteSeries(SeriesDatabase seriesDatabase) {
        String json;
        try {
            json = seriesObjectMapper.writeValueAsString(seriesDatabase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUri + "/series"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that checks wether the request is running or not.
     * 
     * @return boolean
     */
    public boolean isRunning() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUri + "/ping"))
                .GET()
                .build();
        final HttpResponse<String> response;
        try {
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return response.body().equals("pong");
    }

}
