/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.Collections;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * The LayerOrganizer class handles the ordering of Layer objects and displays
 * them as Button objects in a ListView on the right of the BorderPane. If a new
 * Layer object is created it is managed by this class to ensure that is added
 * to the StackPane with proper relation to the other Layers. This class also
 * ensures that the current Layer, {@link Layer#myCurrentLayer}, is always at the top
 * of the StackPane.
]* @author Alec Hauck
 */
public class LayerOrganizer extends ListView {

    /**
     * The {@link ListView} for organizing and viewing the list of
     * {@link Layer}s.
     */
    private static ListView listView;

    /**
     * The {@link ObservableList} which contains the {@link Layer}s.
     */
    private static final ObservableList layers
            = FXCollections.observableArrayList();

    /**
     * Class Constructor.
     */
    public LayerOrganizer() {
        this.setEditable(true);
        this.setOrientation(Orientation.VERTICAL);
        this.setPrefWidth(238);
        this.setPrefHeight(70);
        listView = this;
        listView.setItems(layers);

    }

    /**
     * Gets the {@link ObservableList} representing the {@link Canvas} layers on
     * the {@link StackPane}.
     *
     * @return The {@link ObservableList} representing the {@link Canvas} layers
     * on the {@link StackPane}.
     */
    public static ObservableList getLayers() {
        return layers;
    }

    /**
     * Removes all the {@link Layer} objects from the {@link LayerOrganizer#layers},
     * {@link LayerOrganizer#listView}, and {@link Paint#imagePane}.
     */
    public static void removeLayers() {
        layers.removeAll(layers);
        listView.setItems(layers);
        Paint.removeAllCanvases();

    }

    /**
     * Removes the given Layer from layers and the StackPane.
     *
     * @param layer The Layer object to be removed.
     */
    public static void removeLayer(Layer layer) {
        layers.remove(layer);
        listView.setItems(layers);
        Paint.removeCanvas(layer.getCanvas());

    }

    /**
     * Adds the given Layer object to the front of the StackPane. It is not
     * added to the list of layers, since it will be called to be removed once
     * drawing is completed.
     *
     * @param layer The temporary layer to be added to the top.
     */
    public static void addTempLayer(Layer layer) {
        Paint.addCanvas(layer.getCanvas());
    }

    /**
     * Removes the given layer from {@link Paint#imagePane}.
     *
     * @param layer The temporary layer to be removed to the top.
     */
    public static void removeTempLayer(Layer layer) {
        Paint.removeCanvas(layer.getCanvas());
    }

    /**
     * Adds a {@link Layer} to the {@link LayerOrganizer#layers},
     * {@link LayerOrganizer#listView}, and {@link Paint#imagePane}. Creates an
     * {@link EventHandler} to move the layer to the front when and hide all
     * higher layers upon click.
     *
     * @param layer The {@link Layer} object to be added.
     */
    public static void addLayer(Layer layer) {
        layers.add(layer);
        layer.setText("Layer " + Double.toString(layer.getLayerOrder()));
        Paint.addCanvas(layer.getCanvas());
        layer.setMinWidth(200);
        reorder();
        listView.setItems(layers);
        layer.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                MouseButton button = event.getButton();
                if (button == MouseButton.PRIMARY) {
                    Layer.clearHandlers();
                    layer.setCurrentLayer();
                    Layer.resetMouseHandlers();
                    Double rank = layer.getLayerOrder();
                    hideUpperLayers(rank);

                } else if (button == MouseButton.SECONDARY) {

                }
            }
        });
    }

    /**
     * Creates a new {@link Layer} that is not temporary.
     */
    public static void makeNewLayer() {
        Layer newLayer = new Layer(false);

    }

    /**
     * Sorts the {@link LayerOrganizer} by the {@link Layer#layerOrder} and
     * updates the {@link LayerOrganizer#listView} with the new order.
     */
    public static void reorder() {
        Collections.sort(layers);
        listView.setItems(layers);
    }

    /**
     * Removes all layers from the {@link Paint#imagePane}, then adds them back
     * if their {@link Layer#layerOrder} is less than or equal to the given
     * rank.
     *
     * @param rank The {@link double} that is used to determine the upper limit
     * for layers shown.
     */
    private static void hideUpperLayers(double rank) {
        //First remove all layers from the stackPane
        Paint.removeAllCanvases();
        //Then add the layers lower than or equal to rank back
        Iterator layerIterator = layers.iterator();
        while (layerIterator.hasNext()) {
            Layer layer = ((Layer) layerIterator.next());
            double layerRank = layer.getLayerOrder();
            if (layerRank <= rank) {
                Paint.addCanvas(layer.getCanvas());
            }
        }
    }

}
