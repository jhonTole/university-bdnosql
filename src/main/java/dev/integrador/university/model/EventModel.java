package dev.integrador.university.model;

import dev.integrador.university.entities.Event;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class EventModel {
    public ListProperty<Event> events = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    public ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();
}
