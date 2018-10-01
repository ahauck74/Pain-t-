/*
 * This class handles the action events of the LineButton button on the tool bar.
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
public class LineButton extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;

    public LineButton() {
        ImageView lineImage = new ImageView("resources/line.png");
        lineImage.setFitHeight(20);
        lineImage.setFitWidth(20);
        this.setGraphic(lineImage);
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
            tempCanvas = new Canvas(Layer.getCanvasWidth(), Layer.getCanvasHeight());
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
            
            //Updates Layer version of the canvas with the new one that 
            //has a freshly drawn line
            Layer.updateCanvas(gc);
            //myCanvas.setOnMousePressed(null);
            //myCanvas.setOnMouseDragged(null);
            //myCanvas.setOnMouseReleased(null);
            //myCanvas.requestFocus();

        }
    };
}
