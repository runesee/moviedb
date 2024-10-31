package moviedb.core;

public class RemoteDatabaseSaver implements DatabaseObserver {

    private MoviedbWebClient moviedbWebClient;

    public RemoteDatabaseSaver(String endPointUri) {
        this.moviedbWebClient = new MoviedbWebClient(endPointUri);
    }

    public MoviedbWebClient getWebClient() {
        return moviedbWebClient;
    }

    @Override
    public void databaseChanged(AbstractObservableDatabase database) {
        if (database instanceof MovieDatabase) {
            moviedbWebClient.updateRemoteMovies((MovieDatabase) database);
        } else {
            moviedbWebClient.updateRemoteSeries((SeriesDatabase) database);
        }
    }
}
