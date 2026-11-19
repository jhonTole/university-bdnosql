package dev.integrador.university.view;

import dev.integrador.university.entities.Event;
import dev.integrador.university.entities.Venue;


import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
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
import javafx.scene.shape.Line;
import javafx.util.Builder;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

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
        Node icon = safeIcon(iconPath, caption.substring(0, 1), 32);

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

        // Eventos desde hoy en adelante
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Event> upcoming = events.stream()
                .filter(e -> e.date != null && !e.date.before(today))
                .sorted(Comparator.comparing((Event e) -> e.date))
                .toList(); // muestra todos

        VBox eventList = new VBox();
        eventList.setSpacing(0);

        if (upcoming.isEmpty()) {
            Label empty = new Label("No hay eventos próximos");
            empty.setStyle("-fx-text-fill: " + MUTED + ";");
            empty.setPadding(new Insets(10, 0, 0, 0));
            eventList.getChildren().add(empty);
        }else {
            Map<String, String> venueNames = new HashMap<>();
            for (Venue venue : venues) {
                venueNames.put(venue.venueId, venue.name);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Event e : upcoming) {
                String place = "Sin espacio asignado";
                if (e.venueId != null && !e.venueId.isBlank()) {
                    place = venueNames.getOrDefault(e.venueId, e.venueId);
                }
                String time = e.startTime == null ? "" : e.startTime;
                eventList.getChildren().add(createEventRow(e.name, place, dateFormat.format(e.date), time));
            }
        }
        // ScrollPane transparente para que aproveche toda la altura de la tarjeta
        ScrollPane scrollPane = new ScrollPane(eventList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        " -fx-background-color: transparent;" +
                        " -fx-border-color: transparent;" +
                        " color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        // Se declara la tarjeta una sola vez aquí
        VBox card = new VBox(8, title, scrollPane);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);
        VBox.setVgrow(card, Priority.ALWAYS);

        return card;
    }

    private Node createEventRow(String name, String place, String date, String time) {
        Node icon = safeIcon("/img/eventsCalendar.png", "E", 26);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: #111827;");
        Label placeLabel = new Label(place);
        placeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + ";");
        VBox info = new VBox(2, nameLabel, placeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #111827;");
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

    // Usa una letra de respaldo si la imagen no está en resources/img.
    private Node safeIcon(String path, String fallback, double size) {
        java.net.URL resource = getClass().getResource(path);
        if (resource != null) {
            ImageView image = new ImageView(new Image(resource.toExternalForm(), false));
            image.setFitWidth(size);
            image.setFitHeight(size);
            image.setPreserveRatio(true);
            return image;
        }
        Label label = new Label(fallback);
        label.setAlignment(Pos.CENTER);
        label.setMinSize(size, size);
        label.setStyle("-fx-background-color: #E8EFF9; -fx-background-radius: 8; "
                + "-fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 17px;");
        return label;
    }

    private Node createCategoriesCard() {
        Label title = new Label("Dispersión y tendencia de eventos");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Label description = new Label("Cada punto es un evento: fecha y capacidad.");
        description.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + ";");
        description.setWrapText(true);

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        // Eje X: Ahora muestra fechas reales (ej: 19/11,    20/11, 25/11) Y: capacidad registrada del evento.
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Fechas del evento");
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false); // Quita divisiones intermedias confusas
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object == null) return "";
                double val = object.doubleValue();
                long days = Math.round(val);
                // Solo muestra texto en días enteros, no en decimales
                if (Math.abs(val - days) > 0.1) return "";
                return today.plusDays(days).format(dateFormatter);
            }
            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        yAxis.setLabel("Capacidad (personas)");
        yAxis.setForceZeroInRange(true);

        TrendScatterChart chart = new TrendScatterChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setMinSize(0, 0);
        chart.setPrefHeight(230);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        // 1. Recolectar puntos válidos
        List<double[]> points = new java.util.ArrayList<>();
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;

        for (Event event : events) {
            if (event.date == null || event.capacity == null) continue;
            LocalDate eventDate = event.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long days = ChronoUnit.DAYS.between(today, eventDate);

            XYChart.Data<Number, Number> point = new XYChart.Data<>(days, event.capacity);
            series.getData().add(point);

            points.add(new double[]{days, event.capacity});
            if (days < minX) minX = days;
            if (days > maxX) maxX = days;

            point.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) Tooltip.install(node, new Tooltip(
                        (event.name == null ? "Evento" : event.name)
                        + "\nFecha: " + eventDate + "\nCapacidad: " + event.capacity));
            });
        }
        chart.getData().add(series);

        // 2. Cálculo de la regresión lineal (y = m*x + b) si hay al menos 2 puntos con X distinta
        String trendInfo = "A la derecha: eventos futuros. Más arriba: mayor capacidad.";
        if (points.size() >= 2 && maxX > minX) {
            double n = points.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
            for (double[] p : points) {
                sumX += p[0];
                sumY += p[1];
                sumXY += p[0] * p[1];
                sumX2 += p[0] * p[0];
            }

            double denominator = (n * sumX2) - (sumX * sumX);
            if (Math.abs(denominator) > 1e-6) {
                double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
                double intercept = (sumY - (slope * sumX)) / n;
                double y1 = slope * minX + intercept;
                double y2 = slope * maxX + intercept;
                chart.setTrendLine(minX, y1, maxX, y2);
                if (slope > 0.05) {
                    trendInfo = String.format(java.util.Locale.US,
                            "Tendencia CRECIENTE (+%.1f cupos/día). Línea roja punteada.", slope);
                } else if (slope < -0.05) {
                    trendInfo = String.format(java.util.Locale.US,
                            "Tendencia DECRECIENTE (%.1f cupos/día). Línea roja punteada.", slope);
                } else {
                    trendInfo = "Tendencia CONSTANTE (estable). Línea roja punteada.";
                }
            }
        } else if (points.size() == 1) {
            trendInfo = "Registra al menos 2 eventos en días distintos para trazar la línea de tendencia.";
        }

        Label note = new Label(trendInfo);
        note.setWrapText(true);
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: " + MUTED + "; -fx-font-weight: bold;");
        VBox card = new VBox(8, title, description, chart, note);
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(1);
        card.setStyle(CARD_STYLE);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return card;
    }
    // Gráfico de dispersión personalizado con línea de regresión lineal
    private static class TrendScatterChart extends ScatterChart<Number, Number> {
        private final Line trendLine = new Line();
        private Double x1, y1, x2, y2;
        public TrendScatterChart(NumberAxis xAxis, NumberAxis yAxis) {
            super(xAxis, yAxis);
            trendLine.setStroke(Color.web("#E11D48")); // Rojo / coral visible
            trendLine.setStrokeWidth(2.5);
            trendLine.getStrokeDashArray().addAll(6.0, 4.0); // Línea punteada
            trendLine.setManaged(false);
            trendLine.setVisible(false);
            getPlotChildren().add(trendLine);
        }
        public void setTrendLine(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.trendLine.setVisible(true);
            requestLayout();
        }
        @Override
        protected void layoutPlotChildren() {
            super.layoutPlotChildren();
            if (x1 != null && trendLine.isVisible()) {
                trendLine.setStartX(getXAxis().getDisplayPosition(x1));
                trendLine.setStartY(getYAxis().getDisplayPosition(y1));
                trendLine.setEndX(getXAxis().getDisplayPosition(x2));
                trendLine.setEndY(getYAxis().getDisplayPosition(y2));
                trendLine.toFront();
            }
        }
    }
}
