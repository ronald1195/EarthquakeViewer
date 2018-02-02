package macbeth;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintain the ARCGis Map and the markers for all earthquakes.  ALso includes the listener
 * for mouse clicks to display earthquake information.
 *
 * https://developers.arcgis.com/java/latest/
 */
public class EarthquakeMap {

    private MapView mapView;
    private GraphicsOverlay graphicsOverlay;
    private Map<Graphic,EarthquakeEvent> earthquakeTable;

    /**
     * Return the MapView to display in the JavaFX Application Window
     *
     * @return MapView
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * Create the ARCGis Map and markers for all earthquakes.  Listener for mouse clicks
     * to display earthquake information is also included.
     *
     * @param earthquakeList - Contains the list of all earthquakes
     */
    public void createMap(EarthquakeList earthquakeList) {
        // create a ArcGISMap with the a Basemap instance with an Imagery base layer
        ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

        // create graphics overlay and add it to the mapview
        mapView = new MapView();
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // Create an earthquake table that maps graphic dots on the map to earthquake data (EarthquakeEvent)
        // The table is used to display data when the graphic is clicked
        earthquakeTable = new HashMap<Graphic,EarthquakeEvent>();

        // create points for each earthquake
        for (EarthquakeEvent event : earthquakeList.getEarthquakes()) {
            if(event.getDetail().getMag() >= 2.0) {

                Point point = new Point(event.getPoint().getLongitude(), event.getPoint().getLatitude(), SpatialReferences.getWgs84());
                SimpleMarkerSymbol dot = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, ColorUtil.colorToArgb(Color.RED), 12);
                Graphic graphic = new Graphic(point, dot);
                graphicsOverlay.getGraphics().add(graphic);

                // Add dot and earthquake data to the earthquake table
                earthquakeTable.put(graphic, event);
            }
        }

        mapView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
                // create a point from location clicked
                Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

                // identify graphics on the graphics overlay
                ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

                identifyGraphics.addDoneListener(() -> Platform.runLater(() -> {
                    try {
                        // get the list of graphics returned by identify
                        IdentifyGraphicsOverlayResult result = identifyGraphics.get();
                        List<Graphic> graphics = result.getGraphics();

                        // If there was at least one graphic clicked, then display information
                        // about the first graphic.  Multiple graphics can be selected if the
                        // graphics are overlapped.
                        if (!graphics.isEmpty()) {
                            EarthquakeEvent event = earthquakeTable.get(graphics.get(0));
                            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                            dialog.setHeaderText(null);
                            dialog.setTitle("Earthquake Detail");
                            dialog.setContentText(event.toString());
                            dialog.showAndWait();
                        }
                    } catch (Exception ex) {
                        // on any error, display the stack trace
                        ex.printStackTrace();
                    }
                }));
            }
        });

        // Add arcgis map to the view
        mapView.setMap(map);
    }

}
