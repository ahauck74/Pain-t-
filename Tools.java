/*
 *This class contains and initializes all the buttons and such on the tool bar.
 *It also stores the current color
 */
package paint;

import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class Tools extends ToolBar {

    static private ColorPicker colorPicker; //The current color is a color button. It contains a Color object as an instance variable
    static private Color currentColor;
    
    static private ColorMatcher colorMatcher;

    static private ComboBox<String> widthPicker;
    static private double drawWidth;

    public Tools() {
        String style = "-fx-background-color: rgba(255, 255, 255, 0.5); -fx-background-radius: 8px; -fx-border-color: rgba(1, 1, 1, 0.7);";
        this.setStyle(style);
        currentColor = Color.BLACK;

        Line lineButton = new Line();
        FreeDraw drawButton = new FreeDraw();
        CircleButton circleButton = new CircleButton();
        RectangleButton rectangleButton = new RectangleButton();
        
        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setTooltip(new Tooltip("This color"));
        colorPicker.setOnAction(e -> pickColor());
        
        colorMatcher = new ColorMatcher();//Doesn't work yet
        
        

        widthPicker = new ComboBox();
        widthPicker.setEditable(true);
        widthPicker.getItems().addAll("1", "2", "4", "8", "12", "16", "20", "28", "36", "48", "64");
        widthPicker.setPromptText("Line Width");
        widthPicker.setOnAction(e -> pickWidth());

        this.getItems().addAll(lineButton,circleButton, rectangleButton, drawButton, colorPicker,  widthPicker);

    }

    private static void pickColor() {
        currentColor = colorPicker.getValue();
    }

    private static void pickWidth() {
        drawWidth =  Double.parseDouble(widthPicker.getValue());
    }
    
    public static Color getCurrentColor() {
        return currentColor;
    }
    //The GraphicsContext setLineWidth() takes in a double
    public static double getDrawWidth() {
        return drawWidth;
    }
    
    public static void setColor(Color c) {
        currentColor = c;
        //colorPicker.setColor(c);
    }
}
