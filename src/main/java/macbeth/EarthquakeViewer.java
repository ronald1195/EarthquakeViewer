package macbeth;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX Application.  Creates a standard sized window with an EarthquakeMap
 * in the window.  The EarthquakeMap is populated using the EarthquakeLoader.
 */
public class EarthquakeViewer extends Application {

    private EarthquakeMap map;
    private EarthquakeLoader loader;

    /**
     * Start the JavaFX APplication.  THis will create the
     * Earthquake Map and populate it with Earthquake Data.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        // create stack pane and application scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);

        // set size, and add scene to stage
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setScene(scene);
        stage.show();

        // Load earthquake data and use it to create the
        // earthquake map.  Add the map to the application window.
        loader = new EarthquakeLoader();
        loader.loadEarthquakes();
        map = new EarthquakeMap();
        map.createMap(loader.getEarthquakeList());
        stackPane.getChildren().add(map.getMapView());
        stage.setTitle("Earthquake Viewer - " + map.getEarthquakeCount() + " Earthquakes Displayed");
    }


    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() throws Exception {

        if (map.getMapView() != null) {
            map.getMapView().dispose();
        }
    }

    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
