
package paint;

import javafx.event.EventHandler;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * The FreeDrawButton class allows the user to draw freely on the current {@link Canvas}.
 * checked. The drawing  is affected by {@link Tools#currentColor} and 
 * {@link Tools#drawWidth}. 
 * @author ahauc
 */
public class FreeDrawButton extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static ImageView drawImage; 
    private static final int BUTTON_IMAGE_SIZE=20;


    /**
     * Default constructor.
     */
    public FreeDrawButton() {
        drawImage = new ImageView("resources/pencil.png");
        drawImage.setFitHeight(BUTTON_IMAGE_SIZE);
        drawImage.setFitWidth(BUTTON_IMAGE_SIZE);
        this.setGraphic(drawImage);
        setTooltip(new Tooltip("Free Draw"));
        this.setOnAction(e -> enterDrawEnvironment());
        
    }
    
    /**
     * Activates the {@link EventHandler}s for drawing on the {@link Layer#myCurrentLayer}.
     */
    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("draw");
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(new ImageCursor(drawImage.getImage(),0.0,300.0));
        myCanvas.setOnMouseClicked(mouseHandler);
        myCanvas.setOnMouseDragged(mouseHandler);
        myCanvas.setOnMouseEntered(mouseHandler);
        myCanvas.setOnMouseExited(mouseHandler);
        myCanvas.setOnMouseMoved(mouseHandler);
        myCanvas.setOnMousePressed(mouseHandler);
        myCanvas.setOnMouseReleased(mouseHandler);
    }

    static EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Layer.prepareUndo();
                gc.beginPath();
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.setStroke(Tools.getCurrentColor());
                gc.setLineWidth(Tools.getDrawWidth());

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.stroke();

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                gc.stroke();
                gc.closePath();
                Layer.updateCanvas(gc);
            }

        }

    };

}
