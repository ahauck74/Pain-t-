/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author ahauc
 */
public class RectangleButton extends Button {
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Layer tempImageCanvas;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;

    public RectangleButton() {
        ImageView rectangleImage = new ImageView("resources/rectangle.png");
        rectangleImage.setFitHeight(20);
        rectangleImage.setFitWidth(20);
        this.setGraphic(rectangleImage);
        this.setOnAction(e -> this.drawLine());
    }

    public void drawLine() {
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();

        myCanvas.setOnMousePressed(canvasMousePressedHandler);
        myCanvas.setOnMouseDragged(canvasMouseDraggedHandler);
        myCanvas.setOnMouseReleased(canvasMouseReleasedHandler);

    }

    EventHandler<MouseEvent> canvasMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Layer.prepareUndo();
            startX = t.getX();
            startY = t.getY();
            tempImageCanvas = new Layer(true);
            tempCanvas = tempImageCanvas.getCanvas();
            tempGC = tempCanvas.getGraphicsContext2D(); 
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            endX = t.getX();
            endY = t.getY();
            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            if (Tools.fillShape()) {
                tempGC.fillRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            tempGC.strokeRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            

            
        }
    };

    EventHandler<MouseEvent> canvasMouseReleasedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            endX = t.getX();
            endY = t.getY();
            gc.setStroke(Tools.getCurrentColor());
            gc.setFill(Tools.getCurrentFillColor());
            gc.setLineWidth(Tools.getDrawWidth());
            if (Tools.fillShape()) {
                gc.fillRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            
            //Removing the temporary layer leaves the currentCanvas in the front
            LayerOrganizer.removeTempLayer(tempImageCanvas);
            Layer.updateCanvas(gc);


        }
    };
}
