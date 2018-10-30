
package paint;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
/**
 * The Tools class extends {@link ToolBar} to contain all the following tool classes {@link CircleButton}, 
 * {@link ColorMatcher}, {@link ColorPicker}, {@link EraserButton}, {@link FreeDrawButton},
 * {@link LineButton}, {@link NGonButton}, {@link RectangleButton}, {@link SnipButton},
 *  and {@link TextButton}. This class also maintains the relevant variables for these classes.
 * @author      Alec Hauck
 * @version     %I%, %G%
 * @since       1.0
 */
public class Tools extends ToolBar {

    static private ColorPicker colorPicker; //The current color is a color button. It contains a Color object as an instance variable
    /**
     * The {@link Color} representing the current stroke color.
     */
    static private Color currentColor;
    
    static private ColorPicker colorFillPicker; //The current color is a color button. It contains a Color object as an instance variable
    /**
     * The {@link Color} representing the current fill color.
     */
    static private Color currentFillColor;

    static private ComboBox<String> widthPicker;
    /**
     * The {@link double} representing the width used for drawing.
     */
    static private double drawWidth;
    
    static private ComboBox<String> sidesPicker;
    /**
     * The {@link int} representing the number of sides used in the {@link NGonButton}.
     */
    static private int nSides;
    
    /**
     * The {@link CheckBox} which, if checked, indicates shapes should be filled.
     */
    static private CheckBox setFill;
    
    static private ComboBox<String> fontPicker;
    static private ComboBox<String> fontSizePicker;
    
    /**
     * The {@link Font} representing the font used to by the text drawn with 
     * {@link TextButton}.
     */
    static private Font currentFont;
    
    /**
     * The {@link Integer} representing the current font size.
     */
    static private int currentFontSize;
    
    /**
     * The default constructor. Initializes all the tools
     */
    public Tools() {
        String style = "-fx-background-color: rgba(255, 255, 255, 0.5); -fx-background-radius: 8px; -fx-border-color: rgba(1, 1, 1, 0.7);";
        this.setStyle(style);
        currentColor = Color.BLACK;

        LineButton lineButton = new LineButton();
        TextButton textButton = new TextButton();
        FreeDrawButton drawButton = new FreeDrawButton();
        EraserButton eraserButton = new EraserButton();
        CircleButton circleButton = new CircleButton();
        RectangleButton rectangleButton = new RectangleButton();
        NGonButton nGonButton = new NGonButton();
        SnipButton snipButton = new SnipButton();
        ColorMatcher colorMatchButton = new ColorMatcher();
        
        //The following are tools which don't have their own unique class.
        //CheckBox for determining if shapes should be filled
        setFill = new CheckBox("Fill Shapes");
        
        //ColorPicker for the stroke of shapes and lines
        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setTooltip(new Tooltip("Stroke color"));
        colorPicker.setOnAction(e -> pickColor());
        
        //ColorPicker for the fill color of shapes
        colorFillPicker = new ColorPicker(Color.BLACK);
        colorFillPicker.setTooltip(new Tooltip("Fill color"));
        colorFillPicker.setOnAction(e -> pickFillColor());
        
        //Editable ComboBox for picking the number of sides of the NGon
        sidesPicker = new ComboBox();
        sidesPicker.setEditable(true);
        sidesPicker.getItems().addAll("3", "5", "6", "7", "8", "10", "12", "20", "100");
        sidesPicker.setPromptText("Sides");
        sidesPicker.setOnAction(e -> pickN());
        
        //Editable ComboBox used picking the stroke width of all shapes and lines including free draw
        widthPicker = new ComboBox();
        widthPicker.setEditable(true);
        widthPicker.getItems().addAll("1", "2", "4", "8", "12", "16", "20", "28", "36", "48", "64");
        widthPicker.setPromptText("Line Width");
        widthPicker.setOnAction(e -> pickWidth());
        
        //ComboBox for picking font used in TextButton
        List<String> families = Font.getFamilies();
        fontPicker = new ComboBox<>(FXCollections.observableArrayList(javafx.scene.text.Font.getFamilies()));
        fontPicker.setMaxWidth(Double.MAX_VALUE);
        fontPicker.setOnAction(e -> pickFont());
        
        //Editable ComboBox for the picking the font size of the used in TextButton
        fontSizePicker = new ComboBox();
        fontSizePicker.setEditable(true);
        fontSizePicker.getItems().addAll("1", "2", "4", "8", "12", "16", "20", "28", "36", "48", "64");
        fontSizePicker.setPromptText("Font Size");
        fontSizePicker.setOnAction(e -> pickFontSize());
        
          
        
        this.getItems().addAll(lineButton,circleButton, rectangleButton, nGonButton, sidesPicker,  setFill, 
                drawButton, eraserButton,  snipButton, textButton, colorMatchButton, colorPicker, 
                colorFillPicker, widthPicker, fontPicker ,fontSizePicker);

    }

    
    
        /**
     *  Sets the {@link Color} representing the current stroke color and updates the color on the 
     * {@link ColorPicker}.
     * @param color The {@link Color} representing the current stroke color and updates the color on the 
     * {@link ColorPicker}.
     */
    public static void setColor(Color color) {
        currentColor = color;
        colorPicker.setValue(color);
    }
    
      /**
     *Gets the {@link Color} representing the current stroke color.
     * @return The {@link Color} representing the current stroke color.
     */
    public static Color getCurrentColor() {
        return currentColor;
    }
    
    /**
     *Gets the {@link Color} representing the current fill color.
     * @return The {@link Color} representing the current fill color.
     */
    public static Color getCurrentFillColor() {
        return currentFillColor;
    }
    
     /**
     *Sets the {@link Color} representing the current fill color and updates the 
     * {@link ColorPicker} for the fill color.
     * @param color The {@link Color} representing the current fill color and updates the 
     * {@link ColorPicker} for the fill color.
     */
    public static void setFillColor(Color color) {
        currentFillColor = color;
        colorFillPicker.setValue(color);
    }
    
    /**
     *Gets the {@link int} representing the number of sides used in the {@link NGonButton}.
     * @return The {@link int} representing the number of sides used in the {@link NGonButton}.
     */
    public static int getN() {
        return nSides;
    }

    /**
     * Gets the {@link double} representing the width used for drawing.
     * @return The {@link double} representing the width used for drawing.
     */
    public static double getDrawWidth() {
        return drawWidth;
    }
    
    /**
     * Checks the value of the {@link Boolean} which represents whether shapes should be
     * filled.
     * @return The value of the {@link Boolean} which represents whether shapes should be
     * filled.
     */
    public static Boolean isSetToFillShape() {
        return setFill.isSelected();
    }
    
    /**
     * Called by {@link Tools#fontPicker}  and {@link Tools#fontSizePicker} to set the 
     * {@link Font} representing the current font.
     */
    public static void pickFont() {
        currentFont = Font.font(fontPicker.getValue(), currentFontSize);
    }
    
     /**
     * Called by {@link Tools#fontSizePicker} to set the 
     * {@link Font} representing the current font.
     */
    public static void pickFontSize() {
        currentFontSize =  Integer.parseInt(fontSizePicker.getValue());
        pickFont();
    }
    
    /**
     * Gets the {@link Font} representing the font used to by the text drawn with 
     * {@link TextButton}.
     * @return The {@link Font} representing the font used to by the text drawn with 
     * {@link TextButton}.
     */
    public static Font getCurrentFont() {
        return currentFont;
        
    }
    
    /**
     * Gets the {@link Integer} representing the current font size.
     * @return The {@link Integer} representing the current font size.
     */
    public static Integer getCurrentFontSize() {
        return currentFontSize;
    }
    
    private static void pickColor() {
        currentColor = colorPicker.getValue();
        colorPicker.setValue(currentColor);
    }
    
    private static void pickFillColor() {
        currentFillColor = colorFillPicker.getValue();
    }
    

    private static void pickWidth() {
        drawWidth =  Double.parseDouble(widthPicker.getValue());
    }
    
    private static void pickN() {
        
        nSides =  Integer.parseInt(sidesPicker.getValue());
    }

    
}
