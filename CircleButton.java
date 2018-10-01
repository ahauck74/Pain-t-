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

/**
 *
 * @author ahauc
 */
public class CircleButton extends Button{
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Layer tempImageCanvas;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;

    public CircleButton() {
        ImageView circleImage = new ImageView("resources/circle.png");
        circleImage.setFitHeight(20);
        circleImage.setFitWidth(20);
        this.setGraphic(circleImage);
        setTooltip(new Tooltip("Draw Circle"));
        this.setOnAction(e -> this.enterDrawEnvironment());
    }

    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("circle");
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(Cursor.DEFAULT);
        myCanvas.setOnMousePressed(canvasMousePressedHandler);
        myCanvas.setOnMouseDragged(canvasMouseDraggedHandler);
        myCanvas.setOnMouseReleased(canvasMouseReleasedHandler);

    }

    static EventHandler<MouseEvent> canvasMousePressedHandler
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

    static EventHandler<MouseEvent> canvasMouseDraggedHandler
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
                tempGC.fillOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            tempGC.strokeOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            

            
        }
    };

    static EventHandler<MouseEvent> canvasMouseReleasedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            endX = t.getX();
            endY = t.getY();
            gc.setStroke(Tools.getCurrentColor());
            gc.setFill(Tools.getCurrentFillColor());
            
            gc.setLineWidth(Tools.getDrawWidth());
            if (Tools.fillShape()) {
                gc.fillOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            
            LayerOrganizer.removeTempLayer(tempImageCanvas);

            Layer.updateCanvas(gc);

        }
    };
}
