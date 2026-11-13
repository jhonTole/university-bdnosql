package dev.integrador.university.view.venue;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;
import dev.integrador.university.model.VenueModel;
import dev.integrador.university.service.VenueService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;

public class VenueView implements Builder<Region> {
    private final VenueModel model;
    private final VenueService service;

    public VenueView(VenueModel model, VenueService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node table = createVenuesTable();

        Node newVenueButton = createNewVenueButton();
        Node deleteVenueButton = createDeleteVenueButton();
        Node updateVenueButton = createUpdateVenueButton();

        HBox navigationBarHBox = new HBox(
                newVenueButton,
                updateVenueButton,
                deleteVenueButton
        );

        navigationBarHBox.setPadding(new Insets(10));
        navigationBarHBox.setSpacing(8);

        // VBox organiza su contenido verticalmente
        VBox layout = new VBox(
                navigationBarHBox,
                table
        );

        // Permitir que la tabla crezca verticalmente
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    private Node createVenuesTable() {
        TableView<Venue> tableView = new TableView<>();

        // sincroniza la fila seleccionada con el modelo
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

        tableView.setItems(model.venues);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewVenueModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Espacio - Universidad");

        NewVenueFormView newVenueForm = new NewVenueFormView(service);
        model.setScene(new Scene(newVenueForm.build()));

        return model;
    }

    private Node createNewVenueButton(){
        Button button = new Button("+ Crear Espacio");
        Stage newVenueModal = createNewVenueModal();
        button.setOnAction(venue -> {
            newVenueModal.showAndWait();
        });
        button.setStyle(
                "-fx-background-color: #007BFF;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );
        return button;
    }

    private Node createUpdateVenueButton(){
        Button button = new Button("Actualizar Espacio");

        button.disableProperty().bind(model.selectedVenue.isNull());
        button.setOnAction(venue -> {
            Venue selectedVenue = model.selectedVenue.get();
            Stage updateVenueModal = createUpdateVenueModal(selectedVenue);
            updateVenueModal.showAndWait();
        });
        button.setStyle(
                "-fx-background-color: #F0AD4E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );

        return button;
    }

    private Stage createUpdateVenueModal(Venue venueToUpdate) {
        Stage modal = new Stage();
        // stage = ventana
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar Espacio - Univesidad");

        UpdateVenueFormView updateVenueFormView = new UpdateVenueFormView(service, venueToUpdate);

        modal.setScene(new Scene(updateVenueFormView.build()));

        return modal;
    }

    private Node createDeleteVenueButton(){
        Button button = new Button("borrar");
        button.disableProperty().bind(
                model.selectedVenue.isNull()
        );
        button.setStyle(
                "-fx-background-color: #DC3545;" +
                        "-fx-text-fill: white;" + // texto
                        "-fx-font-weight: bold;" + // fondo
                        "-fx-background-radius: 5;"
        );

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
