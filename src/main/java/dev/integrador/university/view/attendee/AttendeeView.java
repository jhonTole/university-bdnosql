package dev.integrador.university.view.attendee;

import dev.integrador.university.entities.Attendee;
import dev.integrador.university.entities.Event;
import dev.integrador.university.model.AttendeeModel;
import dev.integrador.university.service.AttendeeService;
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

public class AttendeeView implements Builder<Region> {
    private final AttendeeModel model;
    private final AttendeeService service;

    public AttendeeView(AttendeeModel model, AttendeeService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        FilteredList<Attendee> filteredAttendees = new FilteredList<>(model.attendees, event -> true);
        Node table = createAttendeesTable(filteredAttendees);
        Node newAttendeeButton = createNewAttendeeButton();
        Node deleteAttendeeButton = createDeleteAttendeeButton();
        Node updateAttendeeButton = createUpdateAttendeeButton();

        ImageView icon = new ImageView(new Image(
                getClass().getResource("/img/attendeesGroup.png").toExternalForm())
        );

        icon.setFitWidth(36);
        icon.setFitHeight(36);
        icon.setPreserveRatio(true);

        Label title = new Label("Gestión de Registros");
        title.getStyleClass().add("navigation-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar asistente por nombre o categoría...");
        searchField.setMaxWidth(Double.MAX_VALUE);

        searchField.textProperty().addListener((observable, previous, current) -> {
            String query = current == null ? "" : current.trim().toLowerCase(Locale.ROOT);

            filteredAttendees.setPredicate(attendee -> {
                if (query.isEmpty()) {return true;}

                String name = attendee.name == null ? "" : attendee.name.toLowerCase(Locale.ROOT);
                String documentNumber= attendee.documentNumber == null ? "" : attendee.documentNumber.toLowerCase(Locale.ROOT);

                return name.contains(query) || documentNumber.contains(query);
            });
        });

        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                icon,
                title,
                searchField,
                newAttendeeButton,
                updateAttendeeButton,
                deleteAttendeeButton
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

    private Node createAttendeesTable(ObservableList<Attendee> items) {
        TableView<Attendee> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("Asistente no encontrado"));
        // sincroniza la fila seleccionada con el modelo
        model.selectedAttendee.bind(tableView.getSelectionModel().selectedItemProperty());

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

        tableView.setItems(items);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return tableView;
    }

    private Stage createNewAttendeeModal(){
        Stage model = new Stage();

        model.initModality(Modality.APPLICATION_MODAL);
        model.setTitle("Crear Asistente - Universidad");

        NewAttendeeFormView newAttendeeForm = new NewAttendeeFormView(service);
        Scene scene = new Scene(newAttendeeForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        model.setScene(scene);
        model.setMinWidth(300);

        return model;
    }

    private Node createNewAttendeeButton(){
        Button button = new Button("+ Crear Asistente");
        button.getStyleClass().add("btn-create");
        Stage newAttendeeButton = createNewAttendeeModal();
        button.setOnAction(event -> {
            newAttendeeButton.showAndWait();
        });

        return button;
    }

    private Node createUpdateAttendeeButton(){
        Button button = new Button("Actualizar asistente");
        button.getStyleClass().add("btn-update");
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

        UpdateAttendeeFormView updateAttendeeForm = new UpdateAttendeeFormView(service, attendeeToUpdate);
        Scene scene = new Scene(updateAttendeeForm.build());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        modal.setScene(scene);
        modal.setMinWidth(290);

        return modal;
    }

    private Node createDeleteAttendeeButton(){
        Button button = new Button("Borrar");
        button.getStyleClass().add("btn-delete");
        button.disableProperty().bind(model.selectedAttendee.isNull());

        button.setOnAction(attendee -> {
            deleteAttendee(model.selectedAttendee.get().attendeeId);
            service.findAllAttendees();
        });

        return button;
    }

    private void deleteAttendee(String attendeeId){
        Attendee attendee = model.selectedAttendee.get();
        service.removeAttendeeById(attendeeId);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Evento eliminado");
        successAlert.setHeaderText("El Asistente " + attendee.name + " fue Eliminado exitosamente");
        successAlert.showAndWait();
    }
}
