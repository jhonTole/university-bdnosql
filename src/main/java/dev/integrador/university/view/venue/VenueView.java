package dev.integrador.university.view.venue;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;
import dev.integrador.university.model.VenueModel;
import dev.integrador.university.service.VenueService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;

import java.util.Locale;

public class VenueView implements Builder<Region> {
    private final VenueModel model;
    private final VenueService service;

    public VenueView(VenueModel model, VenueService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        FilteredList<Venue> filteredVenues = new FilteredList<>(model.venues, venue -> true);
        Node table = createVenuesTable(filteredVenues);
        Node newVenueButton = createNewVenueButton();
        Node deleteVenueButton = createDeleteVenueButton();
        Node updateVenueButton = createUpdateVenueButton();

        ImageView icon = new ImageView(new Image(
                getClass().getResource("/img/venueSpace.png").toExternalForm())
        );

        icon.setFitWidth(36);
        icon.setFitHeight(36);
        icon.setPreserveRatio(true);

        Label title = new Label("Espacios");
        title.getStyleClass().add("navigation-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar espacios por nombre o estado...");
        searchField.setMaxWidth(Double.MAX_VALUE);

        searchField.textProperty().addListener((observable, previous, current) -> {
            String query = current == null ? "" : current.trim().toLowerCase(Locale.ROOT);

            filteredVenues.setPredicate(venue -> {
                if (query.isEmpty()) {return true;}

                String name = venue.name == null ? "" : venue.name.toLowerCase(Locale.ROOT);
                String type = venue.type == null ? "" : venue.type.toLowerCase(Locale.ROOT);

                return name.contains(query) || type.contains(query);
            });
        });

        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                icon,
                title,
                searchField,
                newVenueButton,
                updateVenueButton,
                deleteVenueButton
        );

        navigationBarHBox.setPadding(new Insets(10));
        navigationBarHBox.setSpacing(8);

        VBox layout = new VBox(
                navigationBarHBox,
                table
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    private Node createVenuesTable(ObservableList<Venue> items) {
        TableView<Venue> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("Espacio no encontrado"));

        model.selectedVenue.bind(tableView.getSelectionModel().selectedItemProperty());

        TableColumn<Venue, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().venueId));
        tableView.getColumns().add(idColumn);

        TableColumn<Venue, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().name));
        tableView.getColumns().add(nameColumn);

        TableColumn<Venue, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().type));
        tableView.getColumns().add(typeColumn);

        TableColumn<Venue, String> capacityColumn = new TableColumn<>("Capacidad");
        capacityColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().capacity== null ? "" : p.getValue().capacity.toString()));
        tableView.getColumns().add(capacityColumn);

        tableView.setItems(items);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewVenueModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Espacio - Universidad");

        NewVenueFormView newVenueForm = new NewVenueFormView(service);
        Scene scene = new Scene(newVenueForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        model.setScene(scene);
        model.setMinWidth(300);

        return model;
    }

    private Node createNewVenueButton(){
        Button button = new Button("+ Crear Espacio");
        button.getStyleClass().add("btn-create");
        Stage newVenueModal = createNewVenueModal();
        button.setOnAction(venue -> {
            newVenueModal.showAndWait();
        });

        return button;
    }

    private Node createUpdateVenueButton(){
        Button button = new Button("Actualizar Espacio");
        button.getStyleClass().add("btn-update");
        button.disableProperty().bind(model.selectedVenue.isNull());
        button.setOnAction(venue -> {
            Venue selectedVenue = model.selectedVenue.get();
            Stage updateVenueModal = createUpdateVenueModal(selectedVenue);
            updateVenueModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateVenueModal(Venue venueToUpdate) {
        Stage modal = new Stage();
        // stage = ventana
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar Espacio - Univesidad");

        UpdateVenueFormView updateVenueFormView = new UpdateVenueFormView(service, venueToUpdate);
        Scene scene = new Scene(updateVenueFormView.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        modal.setScene(scene);
        modal.setMinWidth(300);

        return modal;
    }

    private Node createDeleteVenueButton(){
        Button button = new Button("Borrar");
        button.getStyleClass().add("btn-delete");
        button.disableProperty().bind(model.selectedVenue.isNull());

        button.setOnAction(venue -> {
            deleteVenue(model.selectedVenue.get().venueId);
            service.findAllVenues();
        });

        return button;
    }

    private void deleteVenue(String venueId){
        Venue venue = model.selectedVenue.get();
        service.removeVenueById(venue.venueId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Espacio eliminado");
        successAlert.setHeaderText("Espacio " + venue.name + " eliminado exitosamente");
        successAlert.showAndWait();
    }
}
