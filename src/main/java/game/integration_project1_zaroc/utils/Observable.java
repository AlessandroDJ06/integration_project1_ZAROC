package game.integration_project1_zaroc.utils;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers(Object args) {
        for (Observer observer : observers) {
            observer.update(args);
        }
    }

    public void notifyLayout(Object args) {
        for (Observer observer : observers) {
            observer.updateLayout(args);
        }
    }
}
