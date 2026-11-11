package dev.integrador.university;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.integrador.university.controller.UniversityController;
import dev.integrador.university.repository.UniversityRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FXApplication extends Application {
    public static final String MONGODB_URI = "mongodb://127.0.0.1:27017";
    private MongoClient mongoClient;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            MongoClient client = MongoClients.create(MONGODB_URI);
            MongoDatabase database = client.getDatabase("universitydb");

            UniversityRepository repository = new UniversityRepository(database);

            UniversityController controller = new UniversityController(repository);

            Scene scene = new Scene(controller.getView());

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
