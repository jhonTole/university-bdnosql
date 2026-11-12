package dev.integrador.university;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.integrador.university.controller.EventController;
import dev.integrador.university.repository.EventRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

            EventRepository repository = new EventRepository(database);

            EventController controller = new EventController(repository);

            Scene scene = new Scene(controller.getView());

            primaryStage.setTitle("University"); // añade titulo a la pestaña
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
