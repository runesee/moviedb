package moviedb.core;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractObservableDatabase {

    protected Collection<DatabaseObserver> observers = new ArrayList<>();

    public void addObserver(DatabaseObserver observer) {
        observers.add(observer);
    }

    public abstract void notifyObservers();
}
