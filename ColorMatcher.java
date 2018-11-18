/*
 * 
 */
package paint;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * The ColorMatcher class allows the user to select {@link Tools#currentColor} by clicking on a pixel
 * from the {@link Layer#myCurrentLayer}. 
 * @author Alec Hauck
 */
public class ColorMatcher extends Button{
    
     /**
     * The {@link Canvas} on the top of the {@link StackPane} selected from {@link Layer#myCurrentLayer}.
     */
    private static Canvas myCanvas;
    
    /**
     * The {@link GraphicsContext} obtained from {@link myCanvas}.
     */
    private static GraphicsContext gc;
    
    /**
     * Default constructor.
     */
    public ColorMatcher() {
        this.setText("_Color Match");
        this.setOnAction(e -> this.matchColor());
    }

    /**
     * Activates the {@link EventHandler}s for matching color on the {@link Layer#myCurrentLayer}.
     */
    public void matchColor() {
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(Cursor.DEFAULT);
        myCanvas.setOnMousePressed(canvasMousePressedHandler);
        myCanvas.setOnMouseClicked(null);
        myCanvas.setOnMouseDragged(null);
        myCanvas.setOnMouseEntered(null);
        myCanvas.setOnMouseExited(null);
        myCanvas.setOnMouseMoved(null);
        myCanvas.setOnMouseReleased(null);


    }

    EventHandler<MouseEvent> canvasMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            
            PixelReader pixelReader = (Layer.getImage()).getPixelReader();
            Color newColor = pixelReader.getColor((int)Math.round(t.getX()), (int)Math.round(t.getY()));
            Tools.setColor(newColor);
            
        }

    };
}
