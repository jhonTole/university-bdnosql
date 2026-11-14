package dev.integrador.university;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.integrador.university.controller.AttendeeController;
import dev.integrador.university.controller.EventController;
import dev.integrador.university.controller.RegistrationController;
import dev.integrador.university.controller.VenueController;
import dev.integrador.university.repository.AttendeeRepository;
import dev.integrador.university.repository.EventRepository;
import dev.integrador.university.repository.RegistrationRepository;
import dev.integrador.university.repository.VenueRepository;
import dev.integrador.university.view.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class FXApplication extends Application {
    public static final String MONGODB_URI = System.getenv().getOrDefault(
            "MONGODB_URI",
            "mongodb://127.0.0.1:27017/?serverSelectionTimeoutMS=3000"
    ); // Con eso la alerta sale en 3 segundos, y además cada quien puede cambiar la URI sin tocar el código si lo necesita.
    private MongoClient mongoClient;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            this.mongoClient = MongoClients.create(MONGODB_URI);
            MongoDatabase database = this.mongoClient.getDatabase("universitydb");

            EventRepository eventRepository = new EventRepository(database);
            EventController eventController = new EventController(eventRepository);

            AttendeeRepository attendeeRepository = new AttendeeRepository(database);
            AttendeeController attendeeController = new AttendeeController(attendeeRepository);

            RegistrationRepository registrationRepository = new RegistrationRepository(database);
            RegistrationController registrationController = new RegistrationController(registrationRepository);

            VenueRepository venueRepository = new VenueRepository(database);
            VenueController venueController = new VenueController(venueRepository);

            // Menú lateral + zona central (diseño del mockup del profe)
            BorderPane root = new BorderPane();
            VBox sidebar = new VBox(4);
            sidebar.getStyleClass().add("sidebar");
            sidebar.setPrefWidth(190);
            sidebar.setMinWidth(190);

            Button homeButton = createNavButton("Inicio", null, root, sidebar, () ->
                    new DashboardView(
                            eventRepository.findAll(),
                            venueRepository.findAll(),
                            attendeeRepository.getCount(),
                            registrationRepository.getCount()
                    ).build());
            Button eventButton = createNavButton("Eventos", "/img/eventsCalendar.png",
                    root, sidebar, eventController::getView);
            Button attendeeButton = createNavButton("Asistentes", "/img/attendeesGroup.png",
                    root, sidebar, attendeeController::getView);
            Button registrationButton = createNavButton("Inscripciones", "/img/registrationsList.png",
                    root, sidebar, registrationController::getView);
            Button venueButton = createNavButton("Espacios", "/img/venueSpace.png",
                    root, sidebar, venueController::getView);

            sidebar.getChildren().addAll(homeButton, eventButton, attendeeButton,
                    registrationButton, venueButton);
            root.setLeft(sidebar);

            // Pantalla inicial: Inicio (Dashboard)
            homeButton.fire();

            // crea una instancia de scene para configurar CSS
            Scene scene = new Scene(root, 1070, 600);

            // Cargar los estilos CSS
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            // Configurar la ventana
            primaryStage.setTitle("Universidad Santo Tomas");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(450);

            // ASIGNAR LA ESCENA A LA VENTANA
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error fatal");
            alert.setHeaderText("Conexion fallida a la base de datos");
            alert.setContentText(e.toString());

            alert.showAndWait();

            throw e;
        }
    }

    // Crea un botón del menú lateral. Al hacer clic cambia la zona central
    // y marca el botón como activo (clase CSS "nav-button-active").
    private Button createNavButton(String text, String iconPath, BorderPane root,
                                   VBox sidebar, Supplier<Region> viewSupplier) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setGraphicTextGap(10);

        if (iconPath != null) {
            ImageView icon = new ImageView(new Image(
                    getClass().getResource(iconPath).toExternalForm()));
            icon.setFitWidth(22);
            icon.setFitHeight(22);
            icon.setPreserveRatio(true);
            button.setGraphic(icon);
        } else {
            button.setText("\u2302  " + text); // icono de casa para Inicio
        }

        button.setOnAction(e -> {
            sidebar.getChildren().forEach(node -> node.getStyleClass().remove("nav-button-active"));
            button.getStyleClass().add("nav-button-active");
            root.setCenter(viewSupplier.get());
        });
        return button;
    }

    @Override
    public void stop() {
        if(mongoClient != null){
            mongoClient.close();

        }
    }
}
