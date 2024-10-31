package moviedb.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import moviedb.core.*;
import moviedb.json.JsonFileHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@ContextConfiguration(classes = { MoviedbRestController.class, MovieDatabaseRepository.class,
        SeriesDatabaseRepository.class, MoviedbServerApplication.class })
@WebMvcTest
public class MoviedbRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper movieObjectMapper;
    private ObjectMapper seriesObjectMapper;
    public static String PING_PATH = "/moviedb/ping";
    public static String MOVIES_PATH = "/moviedb/movies";
    public static String SERIES_PATH = "/moviedb/series";

    @BeforeEach
    public void setup() {
        movieObjectMapper = JsonFileHandler.createMovieObjectMapper();
        seriesObjectMapper = JsonFileHandler.createSeriesObjectMapper();
    }

    @Test
    public void testIsRunning() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(PING_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertEquals("pong", result.getResponse().getContentAsString());
    }

    @Test
    public void testPutMovies() throws Exception {
        MovieDatabase movieDatabaseTest = new MovieDatabase();
        movieDatabaseTest.addMovieUnchecked(new Movie("Test1", 2000, List.of("a", "b"), 200, 9.0));
        movieDatabaseTest.addMovieUnchecked(new Movie("Test2", 2002, List.of("b", "c"), 180, 7.0));
        mockMvc.perform(MockMvcRequestBuilders.put(MOVIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(movieObjectMapper.writeValueAsString(movieDatabaseTest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    @Test
    public void testPutSeries() throws Exception {
        SeriesDatabase seriesDatabaseTest = new SeriesDatabase();
        seriesDatabaseTest.addSeriesUnchecked(new Series("Test1", 2000, List.of("a", "b"), 5, 9.0));
        seriesDatabaseTest.addSeriesUnchecked(new Series("Test2", 2002, List.of("b", "c"), 3, 7.0));
        mockMvc.perform(MockMvcRequestBuilders.put(SERIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(seriesObjectMapper.writeValueAsString(seriesDatabaseTest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    @Test
    public void testGetMovies() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(MOVIES_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        try {
            MovieDatabase movieDatabase = movieObjectMapper.readValue(result.getResponse().getContentAsString(),
                    MovieDatabase.class);
            Iterator<Movie> movieIterator = movieDatabase.iterator();
            assertTrue(movieIterator.hasNext());
            Movie movie1 = movieIterator.next();
            assertEquals("Test1", movie1.getName());
            assertEquals(2000, movie1.getReleaseYear());
            assertEquals(List.of("a", "b"), movie1.getGenres());
            assertEquals(200, movie1.getRuntime());
            assertEquals(9.0, movie1.getRating());

            assertTrue(movieIterator.hasNext());
            Movie movie2 = movieIterator.next();
            assertEquals("Test2", movie2.getName());
            assertEquals(2002, movie2.getReleaseYear());
            assertEquals(List.of("b", "c"), movie2.getGenres());
            assertEquals(180, movie2.getRuntime());
            assertEquals(7.0, movie2.getRating());

            assertFalse(movieIterator.hasNext());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetSeries() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(SERIES_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        try {
            SeriesDatabase seriesDatabase = seriesObjectMapper.readValue(result.getResponse().getContentAsString(),
                    SeriesDatabase.class);
            Iterator<Series> seriesIterator = seriesDatabase.iterator();
            assertTrue(seriesIterator.hasNext());
            Series series1 = seriesIterator.next();
            assertEquals("Test1", series1.getName());
            assertEquals(2000, series1.getReleaseYear());
            assertEquals(List.of("a", "b"), series1.getGenres());
            assertEquals(5, series1.getSeasons());
            assertEquals(9.0, series1.getRating());

            assertTrue(seriesIterator.hasNext());
            Series series2 = seriesIterator.next();
            assertEquals("Test2", series2.getName());
            assertEquals(2002, series2.getReleaseYear());
            assertEquals(List.of("b", "c"), series2.getGenres());
            assertEquals(3, series2.getSeasons());
            assertEquals(7.0, series2.getRating());

            assertFalse(seriesIterator.hasNext());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    public static void clean() {
        LocalDatabaseSaver.clearDatabase("movies.json");
        LocalDatabaseSaver.clearDatabase("series.json");
    }

}
