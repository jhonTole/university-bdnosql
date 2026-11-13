package dev.integrador.university.model;

import dev.integrador.university.entities.Venue;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class VenueModel {
    public ListProperty<Venue> venues = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    public ObjectProperty<Venue> selectedVenue = new SimpleObjectProperty<>();
}
