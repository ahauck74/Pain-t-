/*
 * This class handles the action events of the LineButton button on the tool bar.
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
 * The LineButton class allows the user to draw a line from a point (x1, y1) to (x2, y2) 
 * from mouse input. The format of the line is affected by {@link Tools#drawWidth} and
 * {@link Tools#currentColor}.
 * @author ahauc
 */
public class LineButton extends Button {

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
     * Class constructor.
     */
    public LineButton() {
        Layer.setDrawEnvironment("line");
        ImageView lineImage = new ImageView("resources/line.png");
        lineImage.setFitHeight(20);
        lineImage.setFitWidth(20);
        this.setGraphic(lineImage);
        setTooltip(new Tooltip("Draw Line"));
        this.setOnAction(e -> this.enterDrawEnvironment());
    }

    /**
     * Activates the {@link EventHandler}s for drawing lines on the {@link Layer#myCurrentLayer}.
     */
    public static void enterDrawEnvironment() {
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
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            tempGC.strokeLine(startX, startY, endX, endY);
            
        }
    };

    static EventHandler<MouseEvent> canvasMouseReleasedHandler
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

        }
    };
}
