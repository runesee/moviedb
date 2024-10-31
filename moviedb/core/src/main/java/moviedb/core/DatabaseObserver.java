package moviedb.core;

public interface DatabaseObserver {

    void databaseChanged(AbstractObservableDatabase database);

}
