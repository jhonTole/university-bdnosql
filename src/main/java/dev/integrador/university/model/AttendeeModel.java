package dev.integrador.university.model;

import dev.integrador.university.entities.Attendee;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class AttendeeModel {
    public ListProperty<Attendee> attendees = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    public ObjectProperty<Attendee> selectedAttendee = new SimpleObjectProperty<>();
}
