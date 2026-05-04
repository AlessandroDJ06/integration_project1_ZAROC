package game.integration_project1_zaroc.utils;

public interface Observer {
    default void update(Object args) {}
    default void updateLayout(Object args) {}
}