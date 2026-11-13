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
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

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

            TabPane tabPane = new TabPane();

            Tab eventTab = new Tab("Eventos", eventController.getView());
            Tab attendeeTab = new Tab("Asistentes", attendeeController.getView());
            Tab registrationTab = new Tab("Registros", registrationController.getView());
            Tab venueTab = new Tab("Espacios", venueController.getView());

            eventTab.setClosable(false);
            attendeeTab.setClosable(false);
            registrationTab.setClosable(false);
            venueTab.setClosable(false);

            tabPane.getTabs().addAll(eventTab, attendeeTab, registrationTab, venueTab);
            // crea una instancia de scene para configurar CSS
            Scene scene = new Scene(tabPane, 1070, 600);

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

    @Override
    public void stop() {
        if(mongoClient != null){
            mongoClient.close();

        }
    }
}
