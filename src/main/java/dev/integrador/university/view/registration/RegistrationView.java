package dev.integrador.university.view.registration;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.entities.Registration;
import dev.integrador.university.model.RegistrationModel;
import dev.integrador.university.service.RegistrationService;
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

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RegistrationView implements Builder<Region> {
    private final RegistrationModel model;
    private final RegistrationService service;

    public RegistrationView(RegistrationModel model, RegistrationService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        FilteredList<Registration> filteredRegistration = new FilteredList<>(model.registrations, event -> true);
        Node table = createRegistrationTable(filteredRegistration);
        Node newRegistrationButton = createNewRegistrationButton();
        Node deleteRegistrationButton = createDeleteRegistrationButton();
        Node updateRegistrationButton = createUpdateRegistrationButton();
        ImageView icon = new ImageView(new Image(
                getClass().getResource("/img/registrationsList.png").toExternalForm())
        );

        icon.setFitWidth(36);
        icon.setFitHeight(36);
        icon.setPreserveRatio(true);

        Label title = new Label("Gestión de Inscripciones");
        title.getStyleClass().add("navigation-title");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar inscripcion por fecha o estado...");
        searchField.setMaxWidth(Double.MAX_VALUE);

        searchField.textProperty().addListener((observable, previous, current) -> {
            String query = current == null ? "" : current.trim().toLowerCase(Locale.ROOT);

            filteredRegistration.setPredicate(registration -> {
                if (query.isEmpty()) {return true;}

                String registrationDate = registration.registrationDate == null ? "" : dateFormat.format(registration.registrationDate);
                String status = registration.status == null ? "" : registration.status.toLowerCase(Locale.ROOT);

                return registrationDate.contains(query) || status.contains(query);
            });
        });

        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                icon,
                title,
                searchField,
                newRegistrationButton,
                updateRegistrationButton,
                deleteRegistrationButton
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

    private Node createRegistrationTable(ObservableList<Registration> items) {
        TableView<Registration> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("Inscripcion no encontrada"));

        model.selectedRegistration.bind(tableView.getSelectionModel().selectedItemProperty());

        TableColumn<Registration, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().registrationId));
        tableView.getColumns().add(idColumn);

        TableColumn<Registration, String> statusColumn = new TableColumn<>("Estado");
        statusColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().status));
        tableView.getColumns().add(statusColumn);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        TableColumn<Registration, String> dateColumn = new TableColumn<>("Fecha");
        dateColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().registrationDate == null ? "" : dateFormat.format(p.getValue().registrationDate)
        ));
        tableView.getColumns().add(dateColumn);

        tableView.setItems(items);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewRegistrationModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Llenar Inscripcion - Universidad");

        NewRegistrationFormView newRegistrationForm = new NewRegistrationFormView(service);
        Scene scene = new Scene(newRegistrationForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        model.setScene(scene);
        model.setMinWidth(300);
        return model;
    }

    private Node createNewRegistrationButton(){
        Button button = new Button("+ Nueva Incripcion");
        button.getStyleClass().add("btn-create");
        Stage newRegistrationModal = createNewRegistrationModal();
        button.setOnAction(registration -> {
            newRegistrationModal.showAndWait();
        });

        return button;
    }

    private Node createUpdateRegistrationButton(){
        Button button = new Button("Actualizar Inscripcion");
        button.getStyleClass().add("btn-update");
        button.disableProperty().bind(model.selectedRegistration.isNull());
        button.setOnAction(registration -> {
            Registration selectedRegistration = model.selectedRegistration.get();
            Stage updateRegistrationModal = createUpdateRegistrationModal(selectedRegistration);
            updateRegistrationModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateRegistrationModal(Registration registrationToUpdate){
        Stage modal = new Stage();
        // stage = ventana
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar Inscripcion - Univesidad");

        UpdateRegistrationFormView updateRegistrationForm = new UpdateRegistrationFormView(service, registrationToUpdate);
        Scene scene = new Scene(updateRegistrationForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        modal.setScene(scene);
        modal.setMinWidth(300);
        return modal;
    }

    private Node createDeleteRegistrationButton(){
        Button button = new Button("Borrar");
        button.getStyleClass().add("btn-delete");
        button.disableProperty().bind(model.selectedRegistration.isNull());

        button.setOnAction(registration -> {
            deleteRegistration(model.selectedRegistration.get().registrationId);
            service.findAllRegistrations();
        });

        return button;
    }

    private void deleteRegistration(String registrationId){
        Registration registration = model.selectedRegistration.get();
        service.removeRegistrationById(registrationId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Registro eliminado");
        successAlert.setHeaderText("Registro " + registration.status + " eliminado exitosamente");
        successAlert.showAndWait();
    }
}
