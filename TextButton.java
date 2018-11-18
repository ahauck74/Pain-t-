package paint;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * The TextButton class extends {@link Button} which controls the flow of {@link Canvas}
 * Event Handlers to add text and manipulate text on the canvas.
 * @author      Alec Hauck

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
    private static int defaultTextBoxWidth;
    private static int defaultTextBoxHeight;
    

    /**
     *Default constructor.
     */
    public TextButton() {
        this.setText("Text");
        setTooltip(new Tooltip("Draw Text"));
        this.setOnAction(e -> this.enterDrawEnvironment());
        defaultTextBoxWidth = 50;
        width = defaultTextBoxWidth;
        defaultTextBoxHeight = 15;
        height = defaultTextBoxHeight;
        
    }

    /**
     *Called when the button is clicked, this assigns the mouse handlers to the current canvas 
     * retrieved from {@link Layer#getCurrentCanvas}.
     */
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
            textBox.setMaxWidth(defaultTextBoxWidth);
            textBox.setMaxHeight(defaultTextBoxHeight);

            fontSize = Tools.getCurrentFontSize();
            tempGC.strokeRect(startX, startY, width, height);

            tempGC.setStroke(Tools.getCurrentColor());
            tempGC.setFill(Tools.getCurrentFillColor());

            //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
            if (Tools.isSetToFillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            text = "";
            tempGC.setFont(Tools.getCurrentFont());
            tempGC.setFill(Color.BLACK);
            tempGC.fillText(text, startX, startY + fontSize, width);
            tempCanvas.setOnMousePressed(finalizeDraw);

            textBox.requestFocus();
            textBox.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                    //text += event.getCharacter();

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
                    if (Tools.isSetToFillShape()) {
                        tempGC.fillRect(startX, startY, width, height);
                    }
                    text = textBox.getText().replaceAll("\n", System.getProperty("line.separator"));
                    tempGC.setFill(Color.BLACK);
                    tempGC.fillText(text, startX, startY + fontSize, width);
                }
            });

        }
    };

    
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
            if (Tools.isSetToFillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            tempGC.setFill(Color.BLACK);
            tempGC.fillText(text, startX, startY + fontSize);
        }
    };

    static EventHandler<MouseEvent> handleResizeUpperLeft
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            tempGC.clearRect(0, 0, Layer.getCanvasWidth(), Layer.getCanvasHeight());
            width += startX - t.getX();
            height += startY - t.getY();
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
            if (Tools.isSetToFillShape()) {
                tempGC.fillRect(startX, startY, width, height);
            }
            tempGC.setFill(Color.BLACK);
            tempGC.fillText(text, startX, startY + fontSize, width);
        }
    };
    
    //Redirects to resize if mouse clicks corner of text box and drag if mouse clicks inside text box. 
    //Otherwise it ends the textbox and adds the text to the main canvas
    static EventHandler<MouseEvent> finalizeDraw = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {


            if (t.getX() > startX && t.getY() > startY && t.getX() < startX + width && t.getY() < startY + height) {
                startX = t.getX();
                startY = t.getY();
                tempCanvas.setOnMouseDragged(handleDrag);
            } else if (almostEqual(t.getX(), startX, 5) && almostEqual(t.getY(), startY, 5)) {
                tempCanvas.setOnMouseDragged(handleResizeUpperLeft);
            } else if (almostEqual(t.getX(), startX + width, 5) && almostEqual(t.getY(), startY, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeUpperRight);
            } else if (almostEqual(t.getX(), startX, 5) && almostEqual(t.getY(), startY + height, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeLowerLeft);
            } else if (almostEqual(t.getX(), startX + width, 5) && almostEqual(t.getY(), startY + height, 5)) {
                //tempCanvas.setOnMouseDragged(handleResizeLowerRight);
            } else {
                Paint.removeText(textBox);
                gc.setStroke(Tools.getCurrentColor());
                gc.setFill(Tools.getCurrentFillColor());
                gc.setLineWidth(1);//Takes type double as its argument
                //Using the mininmum x and y coordinates, it dynamically finds the upper left corner
                if (Tools.isSetToFillShape()) {
                    gc.fillRect(startX, startY, width, height);
                }
                gc.setFont(Tools.getCurrentFont());
                gc.setFill(Color.BLACK);
                gc.fillText(text, startX, startY + fontSize, width);

                LayerOrganizer.removeTempLayer(tempImageCanvas);
                LayerOrganizer.reorder();
                Layer.updateCanvas(gc);
                Layer.clearHandlers();
            }
        }
    };


    /**
    *This method is used to determine if the mouse coordinates are almost equal to the coordinates
    * of the corner of the text box.
    */
    private static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }
}
