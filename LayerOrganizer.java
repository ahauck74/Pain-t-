/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author ahauc
 */
public class LayerOrganizer extends FlowPane {

    private static LayerOrganizer flowPane;
    private static ArrayList<Layer> layers;

    public LayerOrganizer(double hGap, double vGap) {
        this.setHgap(hGap);
        this.setVgap(vGap);

        this.setPrefWrapLength(200);
        layers = new ArrayList();
        flowPane = this;
    }

    public static void removeLayers() {
        layers.removeAll(layers);
        flowPane.getChildren().clear();
        Paint.removeAllCanvases();
        
    }
    
    public static void removeLayer(Layer layer) {
        flowPane.getChildren().remove(layer);
        layers.remove(layer); 
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
        layer.setText(Double.toString(layer.getLayerOrder()));
        Paint.addCanvas(layer.getCanvas());
        
        layer.setMinWidth(200);

        layer.setOnMouseClicked(new EventHandler<MouseEvent>() {
 
            @Override
            public void handle(MouseEvent event) {
                MouseButton button = event.getButton();
                if(button==MouseButton.PRIMARY){
                    layer.setCurrentLayer();
                    Double rank = layer.getLayerOrder();
                    hideUpperLayers(rank);
                    
                }else if(button==MouseButton.SECONDARY){
                    
                }
            }
        });

        reorder();
    }

    public static void makeNewLayer() {
        Layer newLayer = new Layer(false);
    }

    private void layerAction() {
        
    }
    
    public static void reorder() {
        flowPane.getChildren().clear();
        Collections.sort(layers);
        flowPane.getChildren().addAll(layers);
        Paint.addCanvases(layers);
    }
    
    //This method hides all the layers higher than the given rank to bring
    //a lower layer to the front
    private static void hideUpperLayers(double rank) {
        System.out.println("Set rank: " + rank);
        //First remove all layers from the stackPane
        Paint.removeAllCanvases();
        //Then add the layers lower than or equal to rank back
        Iterator layerIterator = layers.iterator();
        while(layerIterator.hasNext()) {
            Layer layer = ((Layer) layerIterator.next());
            double layerRank = layer.getLayerOrder();
            System.out.println("This rank: " + layerRank);
            if (layerRank <= rank) {    
                Paint.addCanvas(layer.getCanvas());
            }
        }
        
    }

}
