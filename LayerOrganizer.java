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
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author ahauc
 */
public class LayerOrganizer extends ListView{

    private static ListView listView;
    private static final ObservableList layers = 
        FXCollections.observableArrayList();

    public LayerOrganizer() {
        this.setEditable(true);
        this.setOrientation(Orientation.VERTICAL);
        this.setPrefWidth(238);
        this.setPrefHeight(70);
        listView = this;
        listView.setItems(layers);
         
    }
    
    public static ObservableList getLayers() {
        return layers;
    }

    public static void removeLayers() {
        layers.removeAll(layers);
        listView.setItems(layers);
        Paint.removeAllCanvases();

    }

    public static void removeLayer(Layer layer) {
        layers.remove(layer);
        listView.setItems(layers);
        Paint.removeCanvas(layer.getCanvas());

    }

    public static void addTempLayer(Layer layer) {
        Paint.addCanvas(layer.getCanvas());
    }

    public static void removeTempLayer(Layer layer) {
        Paint.removeCanvas(layer.getCanvas());
    }

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

    public static void makeNewLayer() {
        Layer newLayer = new Layer(false);
        
    }

    private void layerAction() {

    }

    public static void reorder() {
        Collections.sort(layers);
        listView.setItems(layers);
        Paint.addCanvases(layers);
    }

    //This method hides all the layers higher than the given rank to bring
    //a lower layer to the front
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
