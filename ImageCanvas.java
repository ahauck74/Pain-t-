/*
 * This class is responsible for holding and distributing the most up to date 
 * version of the Canvas object. If a class wishes to draw on the Canvas, it will
 * first access it through this class, then after modifying the Canvas, it will 
 * send the updated version to this class.
 * This class also handles undos and redos
 * 
 * IMPORTANT: Anytime the canvas is changed updateCanvas() and prepareUndo() 
 * should be called.
 */
package paint;

import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class ImageCanvas {
    private static Stack undoStack;
    private static Stack redoStack;
    private static Canvas myCanvas;
    private static GraphicsContext gc;
    private static double height;
    private static double width;
    private static Boolean changesMade = false;

    
    //This is the blank canvas that appears when the program starts.
    public static Canvas defaultCanvas() {
        undoStack = new Stack();
        redoStack = new Stack();
        ImageCanvas.height = 600;//TODO: Add user input for these values
        ImageCanvas.width = 1200;
        myCanvas = new Canvas(width, height);
        gc = myCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0, width, height);
        discardUnsavedProgress(); //resets changesMade to false
        return myCanvas;
    }
    
    //Creates a new canvas with the selected image
    public static Canvas newCanvas(Image image) {
        undoStack = new Stack();
        redoStack = new Stack();
        height = image.getHeight();
        width = image.getWidth();
        myCanvas = new Canvas(width, height);
        
        gc = myCanvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, width, height);
        discardUnsavedProgress(); //resets changesMade to false
        return myCanvas;
    }
 
    
    public static GraphicsContext getContext() {
        return gc;
    }
    
    //Anytime changes are made to the image canvas this method should be called
    //so that this class maintains an updated gc
    public static void updateCanvas(GraphicsContext gc) {
        ImageCanvas.gc = gc;
        ImageCanvas.changesMade = true; //To prevent losing unsaved progress
    }
    
  
    public static Canvas getCanvas() {
        return myCanvas;
    }
    
    public static Image getImage() {
        return myCanvas.snapshot(null, null);
    }
    
    public static WritableImage getWImage() {
        return myCanvas.snapshot(null, null);
    }
    
    public static Boolean hasUnsavedProgress() {
        return changesMade;
    }
    
    public static void discardUnsavedProgress() {
        changesMade = false;
    }
    //This method and the above do the same thing, but I can't think of a fitting
    //name for combining the two
    public static void savedProgress() {
        changesMade = false;
    }
    
    public static double getWidth() {
        return width;
    }
    
    public static double getHeight() {
        return height;
    }
    
    //When changes are made to this canvas this method should be called 
    //in addition to updateCanvas() 
    public static void prepareUndo() {
        undoStack.push(ImageCanvas.myCanvas.snapshot(null, null));
        //prevImage = ImageCanvas.myCanvas.snapshot(null, null);
    }
    
    public static void prepareRedo() {
        redoStack.push(ImageCanvas.myCanvas.snapshot(null, null));
    }
    
    public static void undo() {
        if (undoStack.empty()) { return; }
        prepareRedo(); //Adds the current canvas to the top of the redo stack before
                       //replacing it with the old canvas
        gc.drawImage((Image) undoStack.pop(), 0, 0, width, height);
        if (undoStack.empty()) { 
            discardUnsavedProgress();
        }
        
        
    }
    
    public static void redo() {
        if (redoStack.empty()) { return; }
        prepareUndo();
        gc.drawImage((Image) redoStack.pop(), 0, 0, width, height);
        updateCanvas(gc);
        
    }
    
    
    
}
