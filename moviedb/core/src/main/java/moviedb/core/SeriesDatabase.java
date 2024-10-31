package moviedb.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import moviedb.json.JsonFileHandler;

public class SeriesDatabase extends AbstractObservableDatabase implements Iterable<Series> {
    private List<Series> seriesList;

    public SeriesDatabase() { // constructor needs to be here because of json, but actual content doesn't
        // matter
        this.seriesList = new ArrayList<>(); // creates empty database if the json-file is empty
    }

    /**
     * Adds the given series to the database, if not already saved.
     * 
     * @param series a seriesobject
     * @return boolean
     */
    public boolean addSeries(Series series) {
        if ((this.seriesList).contains(series) || (seriesAlreadySaved(series))) {
            return false;
        } else {
            this.seriesList.add(series);
            notifyObservers();
            return true;
        }
    }

    /**
     * Adds the given series to database, without checking if it is already saved.
     * 
     * @param series a seriesobject
     */
    public void addSeriesUnchecked(Series series) {
        // This method is only used when importing a SeriesDatabase-object from a file,
        // and therefore doesn't need to check if the series is already added
        this.seriesList.add(series);
        notifyObservers();
    }

    /**
     * Removes the given series from database.
     * 
     * @param series a seriesobject
     * @return boolean
     */
    public boolean removeSeries(Series series) {
        if (this.seriesList.contains(series)) {
            this.seriesList.remove(series);
            notifyObservers();
            return true;
        }
        return false;
    }

    public List<Series> getSeries() {
        return this.seriesList;
    }

    public void setSeries(List<Series> series) {
        this.seriesList = series;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        for (DatabaseObserver observer : this.observers) {
            observer.databaseChanged(this);
        }
    }

    @Override
    public Iterator<Series> iterator() {
        return seriesList.iterator();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Series saveFileSeries : this.seriesList) {
            str.append(saveFileSeries.getName()).append(", ");
        }
        try {
            str.deleteCharAt(str.length() - 1);
            str.deleteCharAt(str.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
        return str.toString();
    }

    // Method to check if the series is already saved
    private boolean seriesAlreadySaved(Series series) {
        boolean isLocal = false;
        boolean isRemote = false;
        RemoteDatabaseSaver remoteSaver = null;
        for (DatabaseObserver observer : observers) {
            if (observer instanceof LocalDatabaseSaver) {
                isLocal = true;
            } else if (observer instanceof RemoteDatabaseSaver) {
                isRemote = true;
                remoteSaver = (RemoteDatabaseSaver) observer;
            }
        }
        if (observers.size() != 0) {
            if (isLocal) {
                SeriesDatabase savedDatabase = null;
                try {
                    savedDatabase = JsonFileHandler.createSeriesObjectMapper().readValue(
                            new File(LocalDatabaseSaver.findPath("series.json").toString()), SeriesDatabase.class);
                } catch (IOException e) {
                    // Exception is thrown when file is empty, has no impact reading from file
                    // overall
                }
                if (savedDatabase != null) {
                    if (savedDatabase.getSeries().size() == 0) {
                        return false;
                    }
                    for (Series savedSeries : savedDatabase) {
                        if (savedSeries.getName().equals(series.getName())) {
                            if (savedSeries.getReleaseYear() == series.getReleaseYear()
                                    && savedSeries.getSeasons() == series.getSeasons()) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                return false;
            }
            if (isRemote) {
                try {
                    for (Series remoteSeries : remoteSaver.getWebClient().getSeriesDatabase()) {
                        if (remoteSeries.getName().equals(series.getName())) {
                            if (remoteSeries.getReleaseYear() == series.getReleaseYear()
                                    && remoteSeries.getSeasons() == series.getSeasons()) {
                                return true;
                            }
                        }
                    }
                    return false;
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException("Could not check if series is already saved!");
                }
            }
        }
        return false;

    }
}