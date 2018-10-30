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
 * The CircleButton class allows the user to draw a circle with opposite corners 
 * in the mouse press and mouse release location. The circle will be filled with the 
 * {@link Tools#currentFillColor} if {@link Tools#setFill} is checked. The outline of the circle is
 * affected by {@link Tools#currentColor} and {@link Tools#drawWidth}.
 * @author ahauc
 */
public class CircleButton extends Button{
    /**
     * The {@link Canvas} on the top of the {@link StackPane} selected from {@link Layer#myCurrentLayer}.
     */
    private static Canvas myCanvas;
    
    /**
     * The {@link GraphicsContext} obtained from {@link myCanvas}.
     */
    private static GraphicsContext gc;
    
    /**
     * The {@link Layer} which is temporary and used to preview the drawing.
     */
    private static Layer tempImageCanvas;
    
    /**
     * The {@link Canvas} which is temporary and used to preview the drawing.
     */
    private static Canvas tempCanvas;
    
    /**
     * The {@link GraphicsContext} which is temporary and used to preview the drawing.
     */
    private static GraphicsContext tempGC;
    
    /**
     * The {@link double} representing the starting X coordinate on the {@link Canvas} 
     * for the circle.
     */
    private static double startX;
    
    /**
     * The {@link double} representing the starting Y coordinate on the {@link Canvas} 
     * for the circle.
     */
    private static double startY;
    
    /**
     * The {@link double} representing the ending X coordinate on the {@link Canvas} 
     * for the circle.
     */
    private static double endX;
    
    /**
     * The {@link double} representing the ending Y coordinate on the {@link Canvas} 
     * for the circle.
     */
    private static double endY;

    /**
     * Class constructor.
     */
    public CircleButton() {
        ImageView circleImage = new ImageView("resources/circle.png");
        circleImage.setFitHeight(20);
        circleImage.setFitWidth(20);
        this.setGraphic(circleImage);
        setTooltip(new Tooltip("Draw Circle"));
        this.setOnAction(e -> enterDrawEnvironment());
    }

    /**
     *  Activates the {@link EventHandler}s for drawing circles on the {@link Layer#myCurrentLayer}.
     */
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
            if (Tools.isSetToFillShape()) {
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
            if (Tools.isSetToFillShape()) {
                gc.fillOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            
            LayerOrganizer.removeTempLayer(tempImageCanvas);

            Layer.updateCanvas(gc);

        }
    };
}
