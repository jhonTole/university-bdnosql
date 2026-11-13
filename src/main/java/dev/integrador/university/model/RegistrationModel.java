package dev.integrador.university.model;

import dev.integrador.university.entities.Registration;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class RegistrationModel {
    public ListProperty<Registration> registrations = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );
    public ObjectProperty<Registration> selectedRegistration = new SimpleObjectProperty<>();
}
