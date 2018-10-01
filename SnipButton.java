/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.nio.IntBuffer;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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
    private static Layer tempImageCanvas;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startXCut, startYCut; // Initially used for the mouse press
    // coordinates. Then is overwritten
    // by endXCut, endYCut if they are the upper
    // left corner.
    private static double endXCut, endYCut;
    private static double widthCut, heightCut; //width and height of the cut
    private static double endX, endY; //Location for placing the snip

    private static int[] pixels;
    private static PixelWriter pixelWriter;
    private static PixelWriter tempPixelWriter; //Used for previewing

    private static WritablePixelFormat<IntBuffer> format;

    public SnipButton() {
        ImageView scissorsImage = new ImageView("resources/scissors.png");
        scissorsImage.setFitHeight(20);
        scissorsImage.setFitWidth(20);
        setGraphic(scissorsImage);
        setTooltip(new Tooltip("Cut and Drag"));
        this.setOnAction(e -> this.getCut());
    }

    public void getCut() {
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(Cursor.DEFAULT);
        //First the user is prompted to make a cut with the following mouse actions
        myCanvas.setOnMousePressed(canvasMousePressedHandlerCut);
        myCanvas.setOnMouseDragged(canvasMouseDraggedHandlerCut);
        myCanvas.setOnMouseReleased(canvasMouseReleasedHandlerCut);

    }

    EventHandler<MouseEvent> canvasMousePressedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Layer.prepareUndo();
            startXCut = t.getX();
            startYCut = t.getY();
            tempImageCanvas = new Layer(true);
            tempCanvas = tempImageCanvas.getCanvas();
            tempGC = tempCanvas.getGraphicsContext2D();
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            endXCut = t.getX();
            endYCut = t.getY();
            tempGC.setStroke(Color.BLACK);
            tempGC.setLineDashes(5);
            tempGC.setLineWidth(1);//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            tempGC.strokeRect(Math.min(startXCut, endXCut), Math.min(startYCut, endYCut), Math.abs(startXCut - endXCut), Math.abs(startYCut - endYCut));

        }
    };

    EventHandler<MouseEvent> canvasMouseReleasedHandlerCut
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {

            //Once the mouse is released the pixels in the cut range are obtained
            endXCut = t.getX();
            endYCut = t.getY();
            widthCut = Math.abs(startXCut - endXCut);
            heightCut = Math.abs(startYCut - endYCut);
            startXCut = Math.min(startXCut, endXCut); //Finds the upper left corner
            startYCut = Math.min(startYCut, endYCut);

            //After the cut pixels are stored, mouse actions are changed to
            //press, drag, and release the newly cut pixels
            tempCanvas.setOnMousePressed(canvasMousePressedHandlerDrag);
            tempCanvas.setOnMouseDragged(canvasMouseDraggedHandlerDrag);
            tempCanvas.setOnMouseReleased(canvasMouseReleasedHandlerDrag);
            tempCanvas.setCursor(Cursor.OPEN_HAND);

        }
    };

    ////////////////////////////////////////////////////////////////////////
    //Start drag methods                                                  //
    ////////////////////////////////////////////////////////////////////////
    EventHandler<MouseEvent> canvasMousePressedHandlerDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Layer.prepareUndo();
            LayerOrganizer.reorder();
            tempCanvas.setCursor(Cursor.CLOSED_HAND);
            WritableImage img = Layer.getWImage();
            PixelReader reader = img.getPixelReader();
            
            
            format = WritablePixelFormat.getIntArgbInstance();
            pixels = new int[(int) widthCut * (int) heightCut];
            reader.getPixels((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);
            //Checks for pixels that ought to be transparent
            for (int i = (int) startXCut; i < (int) (startXCut + widthCut); i++) {
                for (int j = (int) startYCut; j < (int) (startYCut + heightCut); j++) {
                    
                    //check the transparency of the pixel at (i,j) and checkc that this isn't the bottom layer
                    //Adding transparancy to the bottom layer creates unwanted holes
                    if (pixels[i-(int)startXCut + (int) ((j-(int)startYCut)*widthCut)] == -1 && Layer.getCurrentLayerOrder() >= 1) {
                        pixels[i-(int)startXCut + (int) ((j-(int)startYCut)*widthCut)] = 0;
                    }

                }
            }

            pixelWriter = gc.getPixelWriter();
            tempPixelWriter = tempGC.getPixelWriter();
            
        }

    };

    EventHandler<MouseEvent> canvasMouseDraggedHandlerDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            //Clears the temporary canvas of any preview lines before drawing the next one
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            endX = t.getX();
            endY = t.getY();
            tempGC.setStroke(Color.WHITE);
            tempGC.setFill(Color.WHITE);
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            tempGC.clearRect((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut);
            tempPixelWriter.setPixels((int) endX, (int) endY, (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);

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
            if (Layer.getCurrentLayerOrder() >= 1) {
                gc.clearRect((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut);
            } else {
                gc.fillRect((int) startXCut, (int) startYCut, (int) widthCut, (int) heightCut);
            }
            
            pixelWriter.setPixels((int) endX, (int) endY, (int) widthCut, (int) heightCut, format, pixels, 0, (int) widthCut);



            Layer.updateCanvas(gc);

            LayerOrganizer.removeTempLayer(tempImageCanvas);
            LayerOrganizer.reorder();
                myCanvas.setOnMousePressed(null);
                myCanvas.setOnMouseDragged(null);
                myCanvas.setOnMouseReleased(null);

        }
    };

}
