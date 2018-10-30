
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
 * The RectangleButton class allows the user to draw a rectangle with opposite corners 
 * in the mouse press and mouse release location. The rectangle will be filled with the 
 * {@link Tools#currentFillColor} if {@link Tools#setFill} is checked. The outline of the rectangle is
 * affected by {@link Tools#currentColor} and {@link Tools#drawWidth}.
 * @author ahauc
 */
public class RectangleButton extends Button {
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
     * for the rectangle.
     */
    private static double startX;
    
    /**
     * The {@link double} representing the starting Y coordinate on the {@link Canvas} 
     * for the rectangle.
     */
    private static double startY;
    
    /**
     * The {@link double} representing the ending X coordinate on the {@link Canvas} 
     * for the rectangle.
     */
    private static double endX;
    
    /**
     * The {@link double} representing the ending Y coordinate on the {@link Canvas} 
     * for the rectangle.
     */
    private static double endY;

    /**
     * Class constructor.
     */
    public RectangleButton() {
        ImageView rectangleImage = new ImageView("resources/rectangle.png");
        rectangleImage.setFitHeight(20);
        rectangleImage.setFitWidth(20);
        this.setGraphic(rectangleImage);
        setTooltip(new Tooltip("Draw Rectangle"));
        this.setOnAction(e -> this.enterDrawEnvironment());
    }

    /**
     * Activates the {@link EventHandler}s for drawing rectangles on the {@link Layer#myCurrentLayer}.
     */
    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("rectangle");
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
                tempGC.fillRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            tempGC.strokeRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            

            
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
                gc.fillRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            }
            gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            
            //Removing the temporary layer leaves the currentCanvas in the front
            LayerOrganizer.removeTempLayer(tempImageCanvas);
            Layer.updateCanvas(gc);


        }
    };
}
