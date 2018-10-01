
package paint;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author ahauc
 */
public class FreeDrawButton extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;


    public FreeDrawButton() {
        ImageView rectangleImage = new ImageView("resources/pencil.png");
        rectangleImage.setFitHeight(20);
        rectangleImage.setFitWidth(20);
        this.setGraphic(rectangleImage);
        this.setOnAction(e -> enterDrawEnvironment());
        
    }
    
    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("draw");
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        
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
