/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class NGonButton extends Button{
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Layer tempImageCanvas;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;
    private static int n;

    public NGonButton() {
        ImageView polygonImage = new ImageView();
        //rectangleImage.setFitHeight(20);
        //rectangleImage.setFitWidth(20);
        //this.setGraphic(rectangleImage);
        setTooltip(new Tooltip("Draw N-Gon"));
        setText("N-Gon");
        this.setOnAction(e -> this.enterDrawEnvironment());
    }

    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("ngon");
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
            n = Tools.getN();
            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            double radius = Math. sqrt((endX-startX)*(endX-startX) + (endY-startY)*(endY-startY));
            double theta_0 =Math.atan((startY-endY)/(startX - endX));
            if (endX > startX) {
                theta_0 += Math.PI;
            }
            double theta = theta_0;
            tempGC.beginPath();
            tempGC.moveTo(startX, startY);
            for (int i=0; i<n; i++) {
                theta += 2*Math.PI/n;
                tempGC.lineTo(endX + radius*Math.cos(theta), endY + radius*Math.sin(theta));
                tempGC.moveTo(endX + radius*Math.cos(theta), endY + radius*Math.sin(theta));
            }
            
            tempGC.lineTo(startX, startY);
            tempGC.moveTo(startX, startY);
            tempGC.stroke();
            tempGC.closePath();
            
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
            double radius = Math. sqrt((endX-startX)*(endX-startX) + (endY-startY)*(endY-startY));
            double theta_0 =Math.atan((startY-endY)/(startX - endX));
            if (endX > startX) {
                theta_0 += Math.PI;
            }
            double theta = theta_0;
            gc.beginPath();
            gc.moveTo(startX, startY);
            System.out.println("hello");
            for (int i=0; i<n; i++) {
                theta += 2*Math.PI/n;
                gc.lineTo(endX + radius*Math.cos(theta), endY + radius*Math.sin(theta));
                gc.moveTo(endX + radius*Math.cos(theta), endY + radius*Math.sin(theta));
            }
            
            gc.lineTo(startX, startY);
            gc.moveTo(startX, startY);
            gc.stroke();
            gc.closePath();
            //Removing the temporary layer leaves the currentCanvas in the front
            LayerOrganizer.removeTempLayer(tempImageCanvas);
            Layer.updateCanvas(gc);


        }
    };
}
