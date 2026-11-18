package dev.integrador.university.view;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Builder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de inicio (Dashboard) según el mockup del profe.
 * El menú lateral ya NO vive aquí: ahora está en FXApplication.
 * Los números y las listas salen de la base de datos (antes eran datos de prueba).
 */
public class DashboardView implements Builder<Region> {
    // Paleta y estilos reutilizables
    private static final String PRIMARY = "#1F4E8C";
    private static final String BACKGROUND = "#F4F6FA";
    private static final String BORDER = "#D9DEE7";
    private static final String MUTED = "#6B7280";

    private static final String CARD_STYLE =
            "-fx-background-color: white;"
                    + "-fx-background-radius: 8;"
                    + "-fx-border-color: " + BORDER + ";"
                    + "-fx-border-radius: 8;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);";

    private final List<Event> events;
    private final List<Venue> venues;
    private final long attendeesCount;
    private final long registrationsCount;

    public DashboardView(List<Event> events, List<Venue> venues,
                         long attendeesCount, long registrationsCount) {
        this.events = events;
        this.venues = venues;
        this.attendeesCount = attendeesCount;
        this.registrationsCount = registrationsCount;
    }

    @Override
    public Region build() {
        Node header = createHeader();
        Node kpis = createKpiRow();
        Node bottom = createBottomSection();
        VBox.setVgrow(bottom, Priority.ALWAYS);

        VBox content = new VBox(header, kpis, bottom);
        content.setSpacing(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + BACKGROUND + ";");
        return content;
    }

    // ---------------------------------------------------------------
    // Encabezado
    // ---------------------------------------------------------------
    private Node createHeader() {
        Circle circle = new Circle(28, Color.web("#F2B705"));
        circle.setStroke(Color.web(PRIMARY));
        circle.setStrokeWidth(3);
        Label logoText = new Label("UST");
        logoText.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");
        StackPane logo = new StackPane(circle, logoText);

        Label title = new Label("Universidad Santo Tomás");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label subtitle = new Label("Gestión de Eventos Universitarios");
        subtitle.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: " + MUTED + ";");

        VBox titles = new VBox(2, title, subtitle);
        titles.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(16, logo, titles);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    // ---------------------------------------------------------------
    // KPIs (números reales)
    // ---------------------------------------------------------------
    private Node createKpiRow() {
        HBox row = new HBox(
                createKpiCard("/img/eventsCalendar.png", String.valueOf(events.size()), "Eventos"),
                createKpiCard("/img/attendeesGroup.png", String.valueOf(attendeesCount), "Asistentes"),
                createKpiCard("/img/registrationsList.png", String.valueOf(registrationsCount), "Inscripciones"),
                createKpiCard("/img/venueSpace.png", String.valueOf(venues.size()), "Espacios")
        );
        row.setSpacing(16);
        return row;
    }

    private Node createKpiCard(String iconPath, String value, String caption) {
        ImageView icon = new ImageView(new Image(
                getClass().getResource(iconPath).toExternalForm()));
        icon.setFitWidth(32);
        icon.setFitHeight(32);
        icon.setPreserveRatio(true);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + MUTED + ";");

        VBox card = new VBox(4, icon, valueLabel, captionLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setMinHeight(110);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(CARD_STYLE);
        HBox.setHgrow(card, Priority.ALWAYS);
        // Mismo ancho para las 4 tarjetas
        card.setPrefWidth(1);
        return card;
    }

    // ---------------------------------------------------------------
    // Sección inferior: próximos eventos + categorías
    // ---------------------------------------------------------------
    private Node createBottomSection() {
        Node upcoming = createUpcomingEventsCard();
        Node categories = createCategoriesCard();

        HBox bottom = new HBox(upcoming, categories);
        bottom.setSpacing(16);
        HBox.setHgrow(upcoming, Priority.ALWAYS);
        HBox.setHgrow(categories, Priority.ALWAYS);
        return bottom;
    }

    private Node createUpcomingEventsCard() {
        Label title = new Label("Próximos Eventos");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        VBox card = new VBox(title);
        card.setSpacing(8);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);

        // Eventos desde hoy en adelante, los 3 más cercanos
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Event> upcoming = events.stream()
                .filter(e -> e.date != null && !e.date.before(today))
                .sorted(Comparator.comparing((Event e) -> e.date))
                .limit(3)
                .toList();

        if (upcoming.isEmpty()) {
            Label empty = new Label("No hay eventos próximos");
            empty.setStyle("-fx-text-fill: " + MUTED + ";");
            card.getChildren().add(empty);
            return card;
        }

        // id del espacio -> nombre del espacio
        Map<String, String> venueNames = new HashMap<>();
        for (Venue venue : venues) {
            venueNames.put(venue.venueId, venue.name);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Event e : upcoming) {
            String place = venueNames.getOrDefault(e.venueId, "Sin espacio");
            String time = e.startTime == null ? "" : e.startTime;
            card.getChildren().add(createEventRow(e.name, place, dateFormat.format(e.date), time));
        }
        return card;
    }

    private Node createEventRow(String name, String place, String date, String time) {
        ImageView icon = new ImageView(new Image(
                getClass().getResource("/img/eventsCalendar.png").toExternalForm()));
        icon.setFitWidth(26);
        icon.setFitHeight(26);
        icon.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label placeLabel = new Label(place);
        placeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + ";");
        VBox info = new VBox(2, nameLabel, placeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + ";");
        VBox when = new VBox(2, dateLabel, timeLabel);
        when.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(12, icon, info, spacer, when);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 4, 8, 4));
        row.setStyle("-fx-border-color: #EEF1F6; -fx-border-width: 1 0 0 0;");
        return row;
    }

    private Node createCategoriesCard() {
        Label title = new Label("Categorías");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        // Cuenta cuántos eventos hay por categoría
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Event e : events) {
            String category = (e.category == null || e.category.isBlank()) ? "Sin categoría" : e.category;
            counts.merge(category, 1, Integer::sum);
        }
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        counts.forEach((category, total) -> data.add(new PieChart.Data(category, total)));

        PieChart chart = new PieChart(data);
        chart.setLegendSide(Side.BOTTOM);   // leyenda abajo: ya no le quita ancho al círculo
        chart.setLabelsVisible(false);
        chart.setMinSize(0, 0);             // deja que el gráfico se encoja dentro del cuadro
        chart.setPrefSize(300, 220);
        chart.setMaxHeight(240);            // tope para que no se desborde
        VBox.setVgrow(chart, Priority.ALWAYS);

        VBox card = new VBox(8, title, chart);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);
        return card;
    }
}
