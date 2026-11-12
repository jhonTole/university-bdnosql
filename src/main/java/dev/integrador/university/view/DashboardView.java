package dev.integrador.university.view;

import dev.integrador.university.model.EventModel;
import dev.integrador.university.service.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.util.Builder;

public class DashboardView implements Builder<Region> {
    // Paleta y estilos reutilizables (baja fidelidad, sin hoja CSS externa)
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

    private final EventModel model;
    private final EventService service;

    public DashboardView(EventModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");
        root.setLeft(createSidebar());
        root.setCenter(createMainContent());
        return root;
    }

    // ---------------------------------------------------------------
    // Sidebar
    // ---------------------------------------------------------------
    private Node createSidebar() {
        VBox sidebar = new VBox(
                createNavButton("\uD83C\uDFE0", "Inicio", true),
                createNavButton("\uD83D\uDCC5", "Eventos", false),
                createNavButton("\uD83D\uDC65", "Asistentes", false),
                createNavButton("\u2611", "Inscripciones", false),
                createNavButton("\uD83C\uDFDB", "Espacios", false)
        );
        sidebar.setSpacing(4);
        sidebar.setPadding(new Insets(16, 8, 16, 8));
        sidebar.setPrefWidth(190);
        sidebar.setMinWidth(190);
        sidebar.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: " + BORDER + ";"
                        + "-fx-border-width: 0 1 0 0;"
        );
        return sidebar;
    }

    private Button createNavButton(String icon, String text, boolean active) {
        Button button = new Button(icon + "   " + text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 12, 10, 12));
        if (active) {
            button.setStyle(
                    "-fx-background-color: #DCE8F7;"
                            + "-fx-text-fill: " + PRIMARY + ";"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 6;"
            );
        } else {
            button.setStyle(
                    "-fx-background-color: transparent;"
                            + "-fx-text-fill: #374151;"
                            + "-fx-background-radius: 6;"
            );
        }
        return button;
    }

    // ---------------------------------------------------------------
    // Contenido principal
    // ---------------------------------------------------------------
    private Node createMainContent() {
        Node header = createHeader();
        Node kpis = createKpiRow();
        Node bottom = createBottomSection();
        VBox.setVgrow(bottom, Priority.ALWAYS);

        VBox content = new VBox(header, kpis, bottom);
        content.setSpacing(20);
        content.setPadding(new Insets(20));
        return content;
    }

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
    // KPIs
    // ---------------------------------------------------------------
    private Node createKpiRow() {
        HBox row = new HBox(
                createKpiCard("\uD83D\uDCC5", "12", "Eventos"),
                createKpiCard("\uD83D\uDC65", "248", "Asistentes"),
                createKpiCard("\u2705", "176", "Inscripciones"),
                createKpiCard("\uD83C\uDFDB", "8", "Espacios")
        );
        row.setSpacing(16);
        return row;
    }

    private Node createKpiCard(String icon, String value, String caption) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: " + PRIMARY + ";");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + MUTED + ";");

        VBox card = new VBox(4, iconLabel, valueLabel, captionLabel);
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
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Hyperlink seeAll = new Hyperlink("Ver todos \u2192");
        seeAll.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-size: 12px;");

        HBox header = new HBox(title, spacer, seeAll);
        header.setAlignment(Pos.CENTER_LEFT);

        // Datos estáticos de prueba
        VBox card = new VBox(
                header,
                createEventRow("Cumbre 5.0", "Auditorio Mayor", "Nov. 20", "08:00"),
                createEventRow("Feria Cultural", "Plaza USTA", "Nov. 25", "14:00"),
                createEventRow("Torneo Deportivo", "Coliseo", "Nov. 28", "10:00")
        );
        card.setSpacing(8);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);
        return card;
    }

    private Node createEventRow(String name, String place, String date, String time) {
        Label icon = new Label("\uD83D\uDCC5");
        icon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + PRIMARY + ";");

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

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Académico", 40),
                new PieChart.Data("Cultural", 25),
                new PieChart.Data("Deportivo", 20),
                new PieChart.Data("Investigación", 15)
        );
        PieChart chart = new PieChart(data);
        chart.setLegendSide(Side.RIGHT);
        chart.setLabelsVisible(false);
        chart.setPrefHeight(220);
        VBox.setVgrow(chart, Priority.ALWAYS);

        VBox card = new VBox(8, title, chart);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);
        return card;
    }
}