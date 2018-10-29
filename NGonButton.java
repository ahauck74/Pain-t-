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
 * The NGonButton class allows the user to draw an N-sided regular polygon. The
 * N-gon is affected by {@link Tools#nSides}, {@link  Tools#drawWidth}, and
 * {@link Tools#currentColor}.
 * @author ahauc
 */
public class NGonButton extends Button {

    /**
     * The {@link Canvas} on the top of the {@link StackPane} selected from {@link Layer#myCurrentLayer}.
     */
    private static Canvas myCanvas;
    
    /**
     * The {@link GraphicsContext} for {@link myCanvas}.
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
     * for the line.
     */
    private static double startX;
    
    /**
     * The {@link double} representing the starting Y coordinate on the {@link Canvas} 
     * for the line.
     */
    private static double startY;
    
    /**
     * The {@link double} representing the ending X coordinate on the {@link Canvas} 
     * for the line.
     */
    private static double endX;
    
    /**
     * The {@link double} representing the ending Y coordinate on the {@link Canvas} 
     * for the line.
     */
    private static double endY;
    
    /**
     * The {@link int} representing the number of sides of the N-Gon.
     */
    private static int n;

    /**
     * Class constructor.
     */
    public NGonButton() {
        ImageView polygonImage = new ImageView();
        setTooltip(new Tooltip("N-Gon"));
        setText("N-Gon");
        this.setOnAction(e -> enterDrawEnvironment());
    }

    /**
     * Activates the {@link EventHandler}s for drawing N-Gons on the
     * {@link Layer#myCurrentLayer}.
     */
    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("ngon");
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
            n = Tools.getN();
            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            double radius = Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
            double theta_0 = Math.atan((startY - endY) / (startX - endX));
            if (endX > startX) {
                theta_0 += Math.PI;
            }
            double theta = theta_0;
            tempGC.beginPath();
            tempGC.moveTo(startX, startY);
            for (int i = 0; i < n; i++) {
                theta += 2 * Math.PI / n;
                tempGC.lineTo(endX + radius * Math.cos(theta), endY + radius * Math.sin(theta));
                tempGC.moveTo(endX + radius * Math.cos(theta), endY + radius * Math.sin(theta));
            }

            tempGC.lineTo(startX, startY);
            tempGC.moveTo(startX, startY);
            tempGC.stroke();
            tempGC.closePath();

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
            double radius = Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
            double theta_0 = Math.atan((startY - endY) / (startX - endX));
            if (endX > startX) {
                theta_0 += Math.PI;
            }
            double theta = theta_0;
            gc.beginPath();
            gc.moveTo(startX, startY);
            System.out.println("hello");
            for (int i = 0; i < n; i++) {
                theta += 2 * Math.PI / n;
                gc.lineTo(endX + radius * Math.cos(theta), endY + radius * Math.sin(theta));
                gc.moveTo(endX + radius * Math.cos(theta), endY + radius * Math.sin(theta));
            }

            gc.lineTo(startX, startY);
            gc.moveTo(startX, startY);
            gc.stroke();
            gc.closePath();
            //Removing the temporary layer leaves the currentCanvas in the front
            LayerOrganizer.removeTempLayer(tempImageCanvas);
            Layer.updateCanvas(gc);

        }
    };
}
