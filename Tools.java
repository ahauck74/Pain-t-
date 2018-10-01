/*
 *This class contains and initializes all the buttons and such on the tool bar.
 *It also stores the current color
 */
package paint;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import static javafx.scene.input.DataFormat.URL;
import javafx.scene.paint.Color;
/**
 *
 * @author ahauc
 */
public class Tools extends ToolBar {

    static private ColorPicker colorPicker; //The current color is a color button. It contains a Color object as an instance variable
    static private Color currentColor;
    
    static private ColorPicker colorFillPicker; //The current color is a color button. It contains a Color object as an instance variable
    static private Color currentFillColor;

    static private ComboBox<String> widthPicker;
    static private double drawWidth;
    
    static private CheckBox setFill;
    

    public Tools() {
        String style = "-fx-background-color: rgba(255, 255, 255, 0.5); -fx-background-radius: 8px; -fx-border-color: rgba(1, 1, 1, 0.7);";
        this.setStyle(style);
        currentColor = Color.BLACK;

        LineButton lineButton = new LineButton();
        FreeDrawButton drawButton = new FreeDrawButton();
        EraserButton eraserButton = new EraserButton();
        CircleButton circleButton = new CircleButton();
        RectangleButton rectangleButton = new RectangleButton();
        SnipButton snipButton = new SnipButton();
        setFill = new CheckBox("Fill Shapes");

        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setTooltip(new Tooltip("Stroke color"));
        colorPicker.setOnAction(e -> pickColor());
        
        colorFillPicker = new ColorPicker(Color.BLACK);
        colorFillPicker.setTooltip(new Tooltip("Fill color"));
        colorFillPicker.setOnAction(e -> pickFillColor());
        
        
        

        widthPicker = new ComboBox();
        widthPicker.setEditable(true);
        widthPicker.getItems().addAll("1", "2", "4", "8", "12", "16", "20", "28", "36", "48", "64");
        widthPicker.setPromptText("Line Width");
        widthPicker.setOnAction(e -> pickWidth());
        
          

        this.getItems().addAll(lineButton,circleButton, rectangleButton, setFill, drawButton, eraserButton,  snipButton, colorPicker, colorFillPicker, widthPicker);

    }

    private static void pickColor() {
        currentColor = colorPicker.getValue();
    }
    
    private static void pickFillColor() {
        currentFillColor = colorFillPicker.getValue();
    }
    

    private static void pickWidth() {
        drawWidth =  Double.parseDouble(widthPicker.getValue());
    }
    
    public static Color getCurrentColor() {
        return currentColor;
    }
    
    public static Color getCurrentFillColor() {
        return currentFillColor;
    }
    //The GraphicsContext setLineWidth() takes in a double
    public static double getDrawWidth() {
        return drawWidth;
    }
    
    public static void setColor(Color c) {
        currentColor = c;
        //colorPicker.setColor(c);
    }
    
    public static void setFillColor(Color c) {
        currentFillColor = c;
        //colorPicker.setColor(c);
    }
    
    public static Boolean fillShape() {
        return setFill.isSelected();
    }
}
