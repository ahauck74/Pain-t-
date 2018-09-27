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
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author ahauc
 */
public class Layer extends Button implements Comparable {

    private static Layer myCurrentLayer;
    private Canvas myCanvas;
    private GraphicsContext gc;

    private static double height;
    private static double width;
    private static Boolean changesMade = false;
    private static int numLayers;
    private double layerOrder;
    private Stack undoStack;
    private Stack redoStack;
    private static SnapshotParameters sp;

    public double getLayerOrder() {
        return layerOrder;
    }
    
    public static int getNumLayers() {
        return numLayers;
    }
    
    public static double getCurrentLayerOrder() {
        return myCurrentLayer.layerOrder;
    }

    //This is the blank canvas that appears when the program starts.
    public Layer() {
        undoStack = new Stack();
        redoStack = new Stack();
        Layer.height = 600;//TODO: Add user input for these values
        Layer.width = 1200;
        myCanvas = new Canvas(width, height);
        gc = myCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        setUpToDate(); //resets changesMade to false
        layerOrder = 0;
        numLayers = 1;
        LayerOrganizer.addLayer(this);
        this.setCurrentLayer();
    }

    //Creates a new canvas with the selected image
    public Layer(Image image) {
        undoStack = new Stack();
        redoStack = new Stack();
        double[] size = resize(image);
        width = size[0];
        height= size[1];
        
        myCanvas = new Canvas(width, height);

        gc = myCanvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, width, height);
        setUpToDate(); //resets changesMade to false
        layerOrder = 0;
        numLayers = 1;
        LayerOrganizer.addLayer(this);
        this.setCurrentLayer();
    }
    
    private static double[] resize(Image img) {
        double maxWidth = 1920;
        double maxHeight = 720;
        double imgHeight = img.getHeight();
        double imgWidth = img.getWidth();
        double imgToFrameHeight = imgHeight/maxHeight;
        double imgToFrameWidth = imgWidth/maxWidth;
        if (imgToFrameHeight > 1 || imgToFrameWidth > 1) {
            if (imgToFrameHeight > imgToFrameWidth) {
                imgWidth = (imgWidth/imgHeight)*maxHeight;
                imgHeight = maxHeight;
            } else {
                System.out.println(imgWidth);
                System.out.println(imgHeight);
                System.out.println(maxHeight);
                
                imgHeight = (imgWidth/imgHeight)*maxWidth;
                imgWidth = maxWidth;
            }
            
            
        }
        double[] size = {imgWidth, imgHeight};
        return size;
    }

    //This constructor is used for adding additional layers
    public Layer(Boolean isTemp) {

        myCanvas = new Canvas(width, height);
        gc = myCanvas.getGraphicsContext2D();
        if (isTemp) {
            layerOrder = myCurrentLayer.layerOrder + 0.5;
            LayerOrganizer.addTempLayer(this);
        } else {
            undoStack = new Stack();
            redoStack = new Stack();
            layerOrder = numLayers;
            setCurrentLayer();
            numLayers += 1;
            LayerOrganizer.addLayer(this);
        }
        
    }

    public void setCurrentLayer() {
        myCurrentLayer = this;
    }
    
    public static Canvas getCurrentCanvas() {
        return myCurrentLayer.myCanvas;
    }
    
    public static GraphicsContext getCurrentContext() {
        return myCurrentLayer.gc;
    }

    //Anytime changes are made to the image canvas this method should be called
    //so that this class maintains an updated gc
    public static void updateCanvas(GraphicsContext gc) {
        myCurrentLayer.gc = gc;
        Layer.changesMade = true; //To prevent losing unsaved progress
    }
 

    public Canvas getCanvas() {
        return myCanvas;
    }

    
    public static Image getImage() {
        return myCurrentLayer.myCanvas.snapshot(null, null);
    }
    
    public static WritableImage getWImage() {
        return myCurrentLayer.myCanvas.snapshot(null, null);
    }

    public static Boolean hasUnsavedProgress() {
        return changesMade;
    }

    public static void setUpToDate() {
        changesMade = false;
    }


    public static double getCanvasWidth() {
        return width;
    }

    public static double getCanvasHeight() {
        return height;
    }

    //When changes are made to this canvas this method should be called 
    //in addition to updateCanvas() 
    //Uundo features are static since they apply to the current canvas
    public static void prepareUndo() {
        sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        myCurrentLayer.undoStack.push(myCurrentLayer.myCanvas.snapshot(sp, null));
        //prevImage = Layer.myCanvas.snapshot(null, null);
    }

    public static void prepareRedo() {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        myCurrentLayer.redoStack.push(myCurrentLayer.myCanvas.snapshot(sp, null));
    }

    public static void undo() {
        if (myCurrentLayer.undoStack.empty()) {
            return;
        }
        prepareRedo(); //Adds the current canvas to the top of the redo stack before
        //replacing it with the old canvas
        if (myCurrentLayer.layerOrder == 0) { //This if-else is necessary to handle transparency issues
            myCurrentLayer.gc.setFill(Color.WHITE);
            myCurrentLayer.gc.fillRect(0,0, width, height);
        } else {
            myCurrentLayer.gc.clearRect(0,0, width, height);
        }
        myCurrentLayer.gc.drawImage((Image) myCurrentLayer.undoStack.pop(), 0, 0, width, height);
        if (myCurrentLayer.undoStack.empty()) {
            setUpToDate();
        }

    }

    public static void redo() {
        if (myCurrentLayer.redoStack.empty()) {
            return;
        }
        prepareUndo();
        if (myCurrentLayer.layerOrder == 0) { //This if-else is necessary to handle transparency issues
            myCurrentLayer.gc.setFill(Color.WHITE);
            myCurrentLayer.gc.fillRect(0,0, width, height);
        } else {
            myCurrentLayer.gc.clearRect(0,0, width, height);
        }
        myCurrentLayer.gc.drawImage((Image) myCurrentLayer.redoStack.pop(), 0, 0, width, height);
        updateCanvas(myCurrentLayer.gc); 

    }
    //For sorting the layers by their rank
    @Override
    public int compareTo(Object o) {
        double otherOrder = ((Layer) o).getLayerOrder();
        /* For Ascending order*/
        return (int) (this.layerOrder - otherOrder);

    }
    
    public static void exitDrawEnvironment() {
        myCurrentLayer.getCanvas().setOnMouseClicked(null);
        myCurrentLayer.getCanvas().setOnMouseDragged(null);
        myCurrentLayer.getCanvas().setOnMouseEntered(null);
        myCurrentLayer.getCanvas().setOnMouseExited(null);
        myCurrentLayer.getCanvas().setOnMouseMoved(null);
        myCurrentLayer.getCanvas().setOnMousePressed(null);
        myCurrentLayer.getCanvas().setOnMouseReleased(null);
    }

}
