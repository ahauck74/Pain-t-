/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class EraserButton extends Button{

    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static double width;

    public EraserButton() {
        this.setText("Eraser");
        //ImageView rectangleImage = new ImageView("resources/pencil.png");
        //rectangleImage.setFitHeight(20);
        //rectangleImage.setFitWidth(20);
        //this.setGraphic(rectangleImage);
        this.setOnAction(e -> enterDrawEnvironment());

    }

    private void enterDrawEnvironment() {

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

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                Layer.prepareUndo();
                gc.beginPath();
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.setStroke(Color.WHITE);
                
                width = (Tools.getDrawWidth());

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                
                if(Layer.getCurrentLayerOrder() == 0) {
                     gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.stroke();
                } else {
                gc.clearRect(mouseEvent.getX()-0.5*width, mouseEvent.getY() - 0.5*width, width, width);
                }
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if(Layer.getCurrentLayerOrder() == 0) {
                     gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                    gc.stroke();
                    gc.closePath();
                } else {
                gc.clearRect(mouseEvent.getX()-0.5*width, mouseEvent.getY() - 0.5*width, width, width);
                    }
                Layer.updateCanvas(gc);
            }

        }

    };

}
