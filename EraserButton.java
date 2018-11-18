/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * The EraserButton class allows the user to enter the erase draw-environment
 * when clicked. While in in the erase draw-environment, dragging the mouse on the bottom layer
 * of the canvas will draw {@link Color#WHITE} or {@link Color#TRANSPARENT} otherwise. Drawing transparently in 
 * this case will replace pixels with transparent ones.
 * @author Alec Hauck
 */
public class EraserButton extends Button{

    
    /**
     * The {@link Canvas} on the top of the {@link StackPane} selected from {@link Layer#myCurrentLayer}.
     */
    private static Canvas myCanvas;
    
    /**
     * The {@link GraphicsContext} obtained from {@link myCanvas}.
     */
    private static GraphicsContext gc;
    
    /**
     * The {@link double} representing the width for the eraser.
     */
    private static double width;

    /**
     * Default constructor. 
     */
    public EraserButton() {
        ImageView eraserImage = new ImageView("resources/eraser.png");
        eraserImage.setFitHeight(20);
        eraserImage.setFitWidth(20);
        this.setGraphic(eraserImage);
        setTooltip(new Tooltip("Eraser"));
        this.setOnAction(e -> enterDrawEnvironment());

    }

    /**
     * Activates the {@link EventHandler}s for erasing on the {@link Layer#myCurrentLayer}.
     */
    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("erase");
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(Cursor.DEFAULT);
        myCanvas.setOnMouseClicked(mouseHandler);
        myCanvas.setOnMouseDragged(mouseHandler);
        myCanvas.setOnMouseEntered(mouseHandler);
        myCanvas.setOnMouseExited(mouseHandler);
        myCanvas.setOnMouseMoved(mouseHandler);
        myCanvas.setOnMousePressed(mouseHandler);
        myCanvas.setOnMouseReleased(mouseHandler);
    }

    static EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Layer.prepareUndo();
                gc.beginPath();
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.setStroke(Color.WHITE);
                width = (Tools.getDrawWidth());
                gc.setLineWidth(width);
                
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                
                if(Layer.getCurrentLayerOrder() == 0) {
                     gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.stroke();
                } else {
                gc.clearRect(mouseEvent.getX()-0.5*width, mouseEvent.getY() - 0.5*width, width, width);
                }
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if(Layer.getCurrentLayerOrder() == 0) {
                     gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.stroke();
                    gc.closePath();
                } else {
                gc.clearRect(mouseEvent.getX()-0.5*width, mouseEvent.getY() - 0.5*width, width, width);
                    }
                Layer.updateCanvas(gc);
                
            }

        }

    };

}
