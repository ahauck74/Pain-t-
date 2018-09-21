/*
 * This class handles the action events of the Line button on the tool bar.
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
public class Line extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;

    public Line() {
        this.setText("_Line");

        this.setOnAction(e -> this.drawLine());
    }

    public void drawLine() {
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
            tempGC.strokeLine(startX, startY, endX, endY);
            
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
            gc.strokeLine(startX, startY, endX, endY);
            myCanvas.toFront();
            
            //Updates ImageCanvas version of the canvas with the new one that 
            //has a freshly drawn line
            ImageCanvas.updateCanvas(gc);
            //myCanvas.setOnMousePressed(null);
            //myCanvas.setOnMouseDragged(null);
            //myCanvas.setOnMouseReleased(null);
            Paint.removeLayer(tempCanvas);
            //myCanvas.requestFocus();

        }
    };
}
