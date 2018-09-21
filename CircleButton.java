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
import javafx.scene.input.MouseEvent;

/**
 *
 * @author ahauc
 */
public class CircleButton extends Button{
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;

    public CircleButton() {
        this.setText("_Circle");

        this.setOnAction(e -> this.drawCircle());
    }

    public void drawCircle() {
        myCanvas = ImageCanvas.getCanvas();
        gc = myCanvas.getGraphicsContext2D();

        myCanvas.setOnMousePressed(canvasMousePressedHandler);
        myCanvas.setOnMouseDragged(canvasMouseDraggedHandler);
        myCanvas.setOnMouseReleased(canvasMouseReleasedHandler);

    }

    EventHandler<MouseEvent> canvasMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            ImageCanvas.prepareUndo();
            startX = t.getX();
            startY = t.getY();
            tempCanvas = new Canvas(ImageCanvas.getWidth(), ImageCanvas.getHeight());
            tempGC = tempCanvas.getGraphicsContext2D(); 
            Paint.addLayer(tempCanvas); 
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, ImageCanvas.getWidth(), ImageCanvas.getHeight());
            endX = t.getX();
            endY = t.getY();
            
            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            tempGC.strokeOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));

            
        }
    };

    EventHandler<MouseEvent> canvasMouseReleasedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            endX = t.getX();
            endY = t.getY();
            gc.setStroke(Tools.getCurrentColor());
            gc.setLineWidth(Tools.getDrawWidth());
            gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            myCanvas.toFront();

            ImageCanvas.updateCanvas(gc);

            Paint.removeLayer(tempCanvas);

        }
    };
}
