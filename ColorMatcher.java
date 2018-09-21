/*
 * This class isn't implemented yet, as I haven't found a way update the colorpicker
 * when a new color is selected with this tool.
 */
package paint;

import java.util.HashSet;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class ColorMatcher extends Button{
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    
    public ColorMatcher() {
        this.setText("_Color Match");

        this.setOnAction(e -> this.matchColor());
    }

    public void matchColor() {
        myCanvas = ImageCanvas.getCanvas();
        gc = myCanvas.getGraphicsContext2D();

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
            
            PixelReader pixelReader = (ImageCanvas.getImage()).getPixelReader();
            Color newColor = pixelReader.getColor((int)Math.round(t.getX()), (int)Math.round(t.getY()));
            Tools.setColor(newColor);
            
        }

    };
}
