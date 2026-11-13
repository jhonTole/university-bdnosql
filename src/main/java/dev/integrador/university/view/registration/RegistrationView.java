package dev.integrador.university.view.registration;

import dev.integrador.university.entities.Registration;
import dev.integrador.university.model.RegistrationModel;
import dev.integrador.university.service.RegistrationService;
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

public class RegistrationView implements Builder<Region> {
    private final RegistrationModel model;
    private final RegistrationService service;

    public RegistrationView(RegistrationModel model, RegistrationService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node table = createRegistrationTable();

        Node newRegistrationButton = createNewRegistrationButton();
        Node deleteRegistrationButton = createDeleteRegistrationButton();
        Node updateRegistrationButton = createUpdateRegistrationButton();

        HBox navigationBarHBox = new HBox(
                newRegistrationButton,
                updateRegistrationButton,
                deleteRegistrationButton
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

    private Node createRegistrationTable() {
        TableView<Registration> tableView = new TableView<>();

        // sincroniza la fila seleccionada con el modelo
        model.selectedRegistration.bind(tableView.getSelectionModel().selectedItemProperty());

        TableColumn<Registration, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().registrationId));
        tableView.getColumns().add(idColumn);

        TableColumn<Registration, String> statusColumn = new TableColumn<>("Estado");
        statusColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().status));
        tableView.getColumns().add(statusColumn);

        TableColumn<Registration, String> registrationDateColumn = new TableColumn<>("Fecha");
        registrationDateColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(
                p.getValue().registrationDate == null ? "" : p.getValue().registrationDate.toString()
        ));

        tableView.getColumns().add(registrationDateColumn);

        tableView.setItems(model.registrations);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewRegistrationModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Registro - Universidad");

        NewRegistrationFormView newRegistrationForm = new NewRegistrationFormView(service);
        Scene scene = new Scene(newRegistrationForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        model.setScene(scene);
        model.setMinWidth(300);
        return model;
    }

    private Node createNewRegistrationButton(){
        Button button = new Button("+ Crear Registro");
        button.getStyleClass().add("btn-create");
        Stage newRegistrationModal = createNewRegistrationModal();
        button.setOnAction(registration -> {
            newRegistrationModal.showAndWait();
        });

        return button;
    }

    private Node createUpdateRegistrationButton(){
        Button button = new Button("Actualizar Registro");
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
        modal.setTitle("Actualizar Registro - Univesidad");

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
            deleteRegistration(model.selectedRegistration.get().eventId);
            service.findAllRegistrations();
        });

        return button;
    }

    private void deleteRegistration(String registrationId){
        Registration registration = model.selectedRegistration.get();
        service.removeRegistrationById(registration.registrationId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Registro eliminado");
        successAlert.setHeaderText("Registro " + registration.status + " eliminado exitosamente");
        successAlert.showAndWait();
    }
}
