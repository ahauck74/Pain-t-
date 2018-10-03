package paint;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ahauc
 */
public class TextButton extends Button {

    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static Layer tempImageCanvas;
    private static Canvas tempCanvas;
    private static GraphicsContext tempGC;
    private static double startX;
    private static double startY;
    private static double width;
    private static double height;
    private static String text;
    private static int fontSize;
    private static TextArea textBox;

    public TextButton() {
        //ImageView textImage = new ImageView("resources/text.png");
        //textImage.setFitHeight(20);
        //textImage.setFitWidth(20);
        //this.setGraphic(textImage);
        this.setText("Text");
        setTooltip(new Tooltip("Draw Text"));
        this.setOnAction(e -> this.enterDrawEnvironment());
    }

    public static void enterDrawEnvironment() {
        Layer.setDrawEnvironment("text");
        myCanvas = Layer.getCurrentCanvas();
        gc = myCanvas.getGraphicsContext2D();
        myCanvas.setCursor(Cursor.DEFAULT);
        myCanvas.setOnMousePressed(canvasMousePressedHandler);
        myCanvas.setOnMouseDragged(null);
        myCanvas.setOnMouseReleased(null);
        

    }

    static EventHandler<MouseEvent> canvasMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Layer.prepareUndo();
            startX = t.getX();
            startY = t.getY();
            textBox = new TextArea();
            textBox.setWrapText(true);
            Paint.addText(textBox);
            tempImageCanvas = new Layer(true);
            tempCanvas = tempImageCanvas.getCanvas();
            tempGC = tempCanvas.getGraphicsContext2D();
            tempGC.setStroke(Color.BLACK);
            tempGC.setLineDashes(5);
            tempGC.setLineWidth(1);//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            width = 50;
            height = 15;
            textBox.setMaxWidth(width);
            textBox.setMaxHeight(height);
            
            fontSize = 10;
            tempGC.strokeRect(startX, startY, width, height);
            System.out.println("hi");

            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());

            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            if (Tools.fillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            text = "";
            tempGC.strokeText(text, startX, startY + fontSize);
            System.out.println("hi");
            tempCanvas.setOnMousePressed(finalizeDraw);
            
            textBox.requestFocus();
            textBox.setOnKeyTyped(new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent keyEvent) {
                    handleEvent(keyEvent);
                }
            }
            );
            
        }
    };

    static EventHandler<MouseEvent> finalizeDraw = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            System.out.println(startX);
            System.out.println(t.getX());
            System.out.println(startX + width);
            
            System.out.println(startY);
            System.out.println(t.getY());
            System.out.println(startY + height);
            
            if (t.getX() > startX && t.getY() > startY && t.getX() < startX + width && t.getY() < startY + height) {
                startX = t.getX();
                startY = t.getY();
                tempCanvas.setOnMouseDragged(handleDrag);
            } else if (almostEqual(t.getX(), startX, 5) && almostEqual(t.getY(), startY, 5)) {
                tempCanvas.setOnMouseDragged(handleResizeUpperLeft);
            } else if (almostEqual(t.getX(), startX+ width, 5) && almostEqual(t.getY(), startY, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeUpperRight);
            } else if (almostEqual(t.getX(), startX, 5) && almostEqual(t.getY(), startY + height, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeLowerLeft);
            } else if (almostEqual(t.getX(), startX + width, 5) && almostEqual(t.getY(), startY + height, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeLowerRight);
            } else {
                System.out.println("end");
                gc.setStroke(Tools.getCurrentColor());
                gc.setFill(Tools.getCurrentFillColor());
                gc.setLineWidth(1);//Takes type double as its argument
                //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
                if (Tools.fillShape()) {
                    gc.fillRect(startX, startY, width, height);
                }

                gc.strokeText(text, startX, startY + fontSize);

                LayerOrganizer.removeTempLayer(tempImageCanvas);

                Layer.updateCanvas(gc);
            }
        }
    };

    public static void handleEvent(KeyEvent event) {
        System.out.println("hi there");
        //text += event.getCharacter();
        text = textBox.getText().replaceAll("\n", System.getProperty("line.separator"));
        tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
        tempGC.setStroke(Color.BLACK);
        tempGC.setLineDashes(5);
        tempGC.setLineWidth(1);//Takes type double as its argument
        //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
        tempGC.strokeRect(startX, startY, width, height);

        tempGC.setStroke(Tools.getCurrentColor());
        tempGC.setFill(Tools.getCurrentFillColor());
        tempGC.setLineDashes(null);
        //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
        if (Tools.fillShape()) {
            tempGC.fillRect(startX, startY, width, height);
        }
        tempGC.strokeText(text, startX, startY + fontSize);
    }

    static EventHandler<MouseEvent> handleDrag
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            startX = t.getX();
            startY = t.getY();
            tempGC.setStroke(Color.BLACK);
            tempGC.setLineDashes(5);
            tempGC.setLineWidth(1);//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner

            tempGC.strokeRect(startX, startY, width, height);

            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());
            tempGC.setLineDashes(null);
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            if (Tools.fillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            tempGC.strokeText(text, startX, startY + fontSize);
        }
    };
    
    static EventHandler<MouseEvent> handleResizeUpperLeft
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            width  += startX - t.getX();
            height  += startY - t.getY();
            tempGC.setStroke(Color.BLACK);
            tempGC.setLineDashes(5);
            tempGC.setLineWidth(1);//Takes type double as its argument
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            startX = t.getX();
            startY = t.getY();
            tempGC.strokeRect(startX, startY, width, height);

            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());
            tempGC.setLineDashes(null);
            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            if (Tools.fillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            tempGC.strokeText(text, startX, startY + fontSize);
        }
    };
    
    
    
    

    private static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }
}
