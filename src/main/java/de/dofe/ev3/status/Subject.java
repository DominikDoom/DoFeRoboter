package de.dofe.ev3.status;

import java.util.ArrayList;

public abstract class Subject {

    private final ArrayList<StatusObserver> observers = new ArrayList<>();

    public void registerObserver(StatusObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StatusObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Status status) {
        for (StatusObserver observer : observers) {
            observer.update(status);
        }
    }
}
