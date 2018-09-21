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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author ahauc
 */
public class FreeDraw extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;


    public FreeDraw() {

        this.setText("_Draw");
        this.setOnAction(e -> enterDrawEnvironment());
        
    }
    
    private void enterDrawEnvironment() {
        
        myCanvas = ImageCanvas.getCanvas();
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
                ImageCanvas.prepareUndo();
                myCanvas = ImageCanvas.getCanvas();
                gc = myCanvas.getGraphicsContext2D();
                gc.beginPath();
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.setStroke(Tools.getCurrentColor());
                gc.setLineWidth(Tools.getDrawWidth());

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                gc.moveTo(mouseEvent.getX(), mouseEvent.getY());
                gc.stroke();
                //path.getElements()
                //      .add(new LineTo(mouseEvent.getX(), mouseEvent.getY()));
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                gc.lineTo(mouseEvent.getX(), mouseEvent.getY());
                gc.stroke();
                gc.closePath();
            }

        }

    };

}
