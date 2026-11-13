package dev.integrador.university.view.attendee;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.model.AttendeeModel;
import dev.integrador.university.service.AttendeeService;
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

public class AttendeeView implements Builder<Region> {
    private final AttendeeModel model;
    private final AttendeeService service;

    public AttendeeView(AttendeeModel model, AttendeeService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node table = createAttendeesTable();

        Node newAttendeeButton = createNewAttendeeButton();
        Node deleteAttendeeButton = createDeleteAttendeeButton();
        Node updateAttendeeButton = createUpdateAttendeeButton();

        HBox navigationBarHBox = new HBox(
                newAttendeeButton,
                updateAttendeeButton,
                deleteAttendeeButton
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

    private Node createAttendeesTable() {
        TableView<Attendee> tableView = new TableView<>();

        // sincroniza la fila seleccionada con el modelo
        model.selectedAttendee.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Attendee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().attendeeId));
        tableView.getColumns().add(idColumn);

        TableColumn<Attendee, String> nameColumn = new TableColumn<>("Nombre del Asistente");
        nameColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().name));
        tableView.getColumns().add(nameColumn);

        TableColumn<Attendee, String> documentNumberColumn = new TableColumn<>("Numero de Documento");
        documentNumberColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().documentNumber));
        tableView.getColumns().add(documentNumberColumn);

        TableColumn<Attendee, String> emailColumn = new TableColumn<>("Correo");
        emailColumn.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().email));
        tableView.getColumns().add(emailColumn);

        tableView.setItems(model.attendees);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewAttendeeModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Asistente - Universidad");

        NewAttendeeFormView newAttendeeForm = new NewAttendeeFormView(service);
        model.setScene(new Scene(newAttendeeForm.build()));

        return model;
    }

    private Node createNewAttendeeButton(){
        Button button = new Button("Crear Asistente");
        Stage newAttendeeButton = createNewAttendeeModal();
        button.setOnAction(event -> {
            newAttendeeButton.showAndWait();
        });
        return button;
    }

    private Node createUpdateAttendeeButton(){
        Button button = new Button("Actualizar asistente");

        button.disableProperty().bind(model.selectedAttendee.isNull());

        button.setOnAction(event -> {
            Attendee selectedAttendee = model.selectedAttendee.get();
            Stage updateAttendeeModal = createUpdateAttendeeModal(selectedAttendee);
            updateAttendeeModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateAttendeeModal(Attendee attendeeToUpdate){
        Stage modal = new Stage();
        // stage = ventana
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar asistente - Univesidad");

        UpdateAttendeeFormView updateAttendeeFormView = new UpdateAttendeeFormView(service, attendeeToUpdate);

        modal.setScene(new Scene(updateAttendeeFormView.build()));

        return modal;
    }

    private Node createDeleteAttendeeButton(){
        Button button = new Button("borrar");
        button.disableProperty().bind(
                model.selectedAttendee.isNull()
        );

        button.setOnAction(attendee -> {
            deleteAttendee(model.selectedAttendee.get().attendeeId);
            service.findAllAttendees();
        });

        return button;
    }

    private void deleteAttendee(String attendeeId){
        Attendee attendee = model.selectedAttendee.get();
        service.removeAttendeeById(attendee.attendeeId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Evento eliminado");
        successAlert.setHeaderText("El Asistente " + attendee.name + " fue Eliminado exitosamente");
        successAlert.showAndWait();
    }
}
