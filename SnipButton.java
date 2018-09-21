/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import static java.lang.String.format;
import java.nio.IntBuffer;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class SnipButton extends Button {
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startXCut, startYCut;
    private static double endXCut, endYCut;
    private static double widthCut, heightCut;
    private static double startX;
    private static double startY;
    private static double endX;
    private static double endY;
    
    private static int[] pixels;
    private static PixelWriter pixelWriter;
    private static PixelWriter tempPixelWriter;
    
    private static WritablePixelFormat<IntBuffer>  format;
    
    
    public SnipButton() {
        this.setText("Snip");
        this.setOnAction(e -> this.getCut());
    }
    
    public void getCut() {
        myCanvas = ImageCanvas.getCanvas();
        gc = myCanvas.getGraphicsContext2D();

        myCanvas.setOnMousePressed(canvasMousePressedHandlerCut);
        myCanvas.setOnMouseDragged(canvasMouseDraggedHandlerCut);
        myCanvas.setOnMouseReleased(canvasMouseReleasedHandlerCut);

    }
    
    EventHandler<MouseEvent> canvasMousePressedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            ImageCanvas.prepareUndo();
            startXCut = t.getX();
            startYCut = t.getY();
            tempCanvas = new Canvas(ImageCanvas.getWidth(), ImageCanvas.getHeight());
            tempGC = tempCanvas.getGraphicsContext2D(); 
            Paint.addLayer(tempCanvas); 
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, ImageCanvas.getWidth(), ImageCanvas.getHeight());
            endXCut = t.getX();
            endYCut = t.getY();
            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setLineDashes(2);
            tempGC.setLineWidth(Tools.getDrawWidth());//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            tempGC.strokeRect(Math.min(startXCut, endXCut), Math.min(startYCut, endYCut), Math.abs(startXCut-endXCut), Math.abs(startYCut-endYCut));

            
        }
    };

    EventHandler<MouseEvent> canvasMouseReleasedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            endXCut = t.getX();
            endYCut = t.getY();
            gc.setStroke(Tools.getCurrentColor());
            gc.setLineWidth(Tools.getDrawWidth());
            //gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY), Math.abs(startX-endX), Math.abs(startY-endY));
            widthCut = Math.abs(startXCut-endXCut);
            heightCut = Math.abs(startYCut-endYCut);
            startXCut = Math.min(startXCut, endXCut);
            startYCut = Math.min(startYCut, endYCut);
            myCanvas.toFront();
            Image img = FileBar.getImage();
            PixelReader reader = img.getPixelReader();
            format = WritablePixelFormat.getIntArgbInstance();
            pixels = new int[(int) widthCut * (int) heightCut];
            reader.getPixels((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);
            
            pixelWriter = gc.getPixelWriter();
            
            
            

            //ImageCanvas.updateCanvas(gc);

            //Paint.removeLayer(tempCanvas);
            myCanvas.setOnMousePressed(canvasMousePressedHandlerDrag);
            myCanvas.setOnMouseDragged(canvasMouseDraggedHandlerDrag);
            myCanvas.setOnMouseReleased(canvasMouseReleasedHandlerDrag);

        }
    };
    
    
    ////////////////////////////////////////////////////////////////////////
    //Start drag methods                                                  //
    ////////////////////////////////////////////////////////////////////////
    
    EventHandler<MouseEvent> canvasMousePressedHandlerDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            ImageCanvas.prepareUndo();
            startX = t.getX();
            startY = t.getY();
            tempCanvas = new Canvas(ImageCanvas.getWidth(), ImageCanvas.getHeight());
            tempGC = tempCanvas.getGraphicsContext2D(); 
            tempPixelWriter = tempGC.getPixelWriter();
            Paint.addLayer(tempCanvas); 
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandlerDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, ImageCanvas.getWidth(), ImageCanvas.getHeight());
            endX = t.getX();
            endY = t.getY();
            tempGC.setStroke(Color.WHITE);
            tempGC.setFill(Color.WHITE);
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            tempGC.fillRect((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut);
            tempPixelWriter.setPixels((int) Math.min(startX, endX), (int) Math.min(startY, endY), (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);

            
        }
    };

    EventHandler<MouseEvent> canvasMouseReleasedHandlerDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            endX = t.getX();
            endY = t.getY();
            gc.setStroke(Color.WHITE);
            gc.setFill(Color.WHITE);
            //Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            
            gc.fillRect((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut);
            pixelWriter.setPixels((int) Math.min(startX, endX), (int) Math.min(startY, endY), (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);
            
            myCanvas.toFront();

            ImageCanvas.updateCanvas(gc);

            Paint.removeLayer(tempCanvas);
            myCanvas.setOnMousePressed(null);
            myCanvas.setOnMouseDragged(null);
            myCanvas.setOnMouseReleased(null);

        }
    };
    
}
