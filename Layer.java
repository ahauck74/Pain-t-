
package paint;

import java.util.Stack;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * The Layer class contains the {@link Canvas} objects which are displayed in 
 * {@link Paint#imagePane}.  Each Layer object has its own {@link Stack} for both undo and 
 * redo functions as well as a {@link Layer#layerOrder} used to determine the ordering of the 
 * layers in the {@link StackPane}. 
 * @author ahauc
 */
public class Layer extends Button implements Comparable {

    /**
     * A pointer to the current Layer object.
     */
    private static Layer myCurrentLayer;
    
    /**
     * A {@link String} representing the type of draw environment being used. These 
     * include: rectangle, circle, line, draw, erase.
     */
    private static String drawEnvironment;
    
    /**
     * The {@link Canvas} contained by the Layer which is used for drawing.
     */
    private final Canvas myCanvas;
    
    /**
     * The {@link GraphicsContext} of for {@link Layer#myCanvas}.
     */
    private GraphicsContext gc;
    
    /**
     * The {@link double} representing the height of the {@link Canvas}es.
     */
    private static double height;
    
    /**
     * The {@link double} representing the width of the {@link Canvas}es.
     */
    private static double width;
    
    /**
     * The {@link Boolean} representing whether there are have been changes made to 
     * any Layer since the last save.
     */
    private static Boolean changesMade = false;
    
    /**
     * The {@link int} representing the number of layers not including any temporary 
     * layers.
     */
    private static int numLayers;
    
    /**
     * The {@link SnapshotParameters} used to let upper layers be merged with
     * transparent pixels during save.
     */
    private static SnapshotParameters sp;

    
    /**
     * The {@link double} representing the order of the layer.
     */
    private double layerOrder;
    
    /**
     * The {@link Stack} containing all previous changes to the layer in order to undo 
     * them.
     */
    private Stack undoStack;
    
    /**
     * The {@link Stack} containing the original layers before undo calls to allow redo.
     */
    private Stack redoStack;
    
    
    /**
     * The default constructor which creates a Layer with a blank {@link Canvas} and 
     * adds it to the {@link LayerOrganizer}.
     */
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

    
    /**
     * The constructor used for creating Layer objects with an {@link Image}.
     * @param image The {@link Image} to be displayed in the {@link Canvas}.
     */
    public Layer(Image image) {
        undoStack = new Stack();
        redoStack = new Stack();
        double[] size = resize(image);
        width = size[0];
        height = size[1];
        myCanvas = new Canvas(width, height);
        gc = myCanvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, width, height);
        setUpToDate(); //resets changesMade to false
        layerOrder = 0;
        numLayers = 1;
        LayerOrganizer.addLayer(this);
        this.setCurrentLayer();
    }
    
    
    /**
     * The constructor used for creating additionally layers. If the new layer is temporary,
     * the isTemp layer should be set to False.
     * @param isTemp This should be set to false to indicate the new layer being temporary and used 
     * for previewing drawing.
     */
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
            clearHandlers();
            setCurrentLayer();
            resetMouseHandlers();
            numLayers += 1;
            LayerOrganizer.addLayer(this);
        }
    }
    
    /**
     * Gets the {@link double} that represents the order of the Layer.
     * @return The {@link double} that represents the order of the Layer.
     */
    public double getLayerOrder() {
        return layerOrder;
    }

    /**
     * Gets an {@link int} representing the number of Layers.
     * @return An {@link int} representing the number of Layers.
     */
    public static int getNumLayers() {
        return numLayers;
    }

    /**
     * Gets the {@link Layer#layerOrder}, of the {@link Layer#myCurrentLayer}.
     * @return  The {@link Layer#layerOrder}, of the {@link Layer#myCurrentLayer}.
     */
    public static double getCurrentLayerOrder() {
        return myCurrentLayer.layerOrder;
    }
    
    //This method is used to scale large images to fit within the window while maintaining their aspect ratio
    private static double[] resize(Image img) {
        double maxWidth = 1920;
        double maxHeight = 720;
        double imgHeight = img.getHeight();
        double imgWidth = img.getWidth();
        double imgToFrameHeight = imgHeight / maxHeight;
        double imgToFrameWidth = imgWidth / maxWidth;
        if (imgToFrameHeight > 1 || imgToFrameWidth > 1) {
            if (imgToFrameHeight > imgToFrameWidth) {
                imgWidth = (imgWidth / imgHeight) * maxHeight;
                imgHeight = maxHeight;
            } else {
                System.out.println(imgWidth);
                System.out.println(imgHeight);
                System.out.println(maxHeight);

                imgHeight = (imgWidth / imgHeight) * maxWidth;
                imgWidth = maxWidth;
            }

        }
        double[] size = {imgWidth, imgHeight};
        return size;
    }

    
    //When changing the current layer, this method should be called so that the user won't have to 
    //reselect the draw button to continue drawing

    /**
     * This method moves the mouse handlers to the current layer. This is used when a new 
     * current layer is selected or created while in a draw environment.
     */
    public static void resetMouseHandlers() {
        switch (drawEnvironment) {
            case "rectangle":
                RectangleButton.enterDrawEnvironment();
                break;
            case "circle":
                CircleButton.enterDrawEnvironment();
                break;
            case "line":
                LineButton.enterDrawEnvironment();
                break;
            case "draw":
                FreeDrawButton.enterDrawEnvironment();
                break;
            case "erase":
                EraserButton.enterDrawEnvironment();
                break;
        }
    }

    /**
     * Sets the {@link String} which represents what drawing environment the user is in.
     * @param drawEnvironment The {@link String} which represents what drawing environment the user is in.
     */
    public static void setDrawEnvironment(String drawEnvironment) {
        Layer.drawEnvironment = drawEnvironment;
    }

    /**
     * Changes the current layer to the layer used to call this method.
     */
    public void setCurrentLayer() {
        myCurrentLayer = this;
    }

    /**
     * Gets the {@link Canvas} of the current layer.
     * @return The {@link Canvas} of the current layer.
     */
    public static Canvas getCurrentCanvas() {
        return myCurrentLayer.myCanvas;
    }

    /**
     * Gets the {@link GraphicsContext} of the current layer.
     * @return The {@link GraphicsContext} of the current layer.
     */
    public static GraphicsContext getCurrentContext() {
        return myCurrentLayer.gc;
    }

    
    /**
     * This method updates the {@link GraphicsContext} of the current layer and it 
     * indicates that changes have been made.
     * @param gc The updated {@link GraphicsContext} of the current layer.
     */
    public static void updateCanvas(GraphicsContext gc) {
        myCurrentLayer.gc = gc;
        Layer.changesMade = true; //To indicate losing unsaved progress
        Paint.indicateUnsaved();
    }

    /**
     * Gets the {@link Canvas} of the given layer.
     * @return The {@link Canvas} of the given layer.
     */
    public Canvas getCanvas() {
        return myCanvas;
    }

    /**
     * Gets the {@link Image} from a 
     * {@link Canvas#snapshot(javafx.scene.SnapshotParameters, 
     * javafx.scene.image.WritableImage)} of the {@link Canvas} of the current layer. 
     * @return the {@link Image} associated with the current layer.
     */
    public static Image getImage() {
        return myCurrentLayer.myCanvas.snapshot(null, null);
    }

    /**
     * Gets the {@link WritableImage} from a 
     * {@link Canvas#snapshot(javafx.scene.SnapshotParameters, 
     * javafx.scene.image.WritableImage)} of the {@link Canvas} of the current layer. 
     * @return the {@link WritableImage} associated with the current layer.
     */
    public static WritableImage getWImage() {
        return myCurrentLayer.myCanvas.snapshot(null, null);
    }

    /**
     * Checks if the application has any unsaved changes.
     * @return A {@link Boolean} that is true if there are unsaved changes.
     */
    public static Boolean hasUnsavedProgress() {
        return changesMade;
    }

    /**
     * Sets {@link Layer#changesMade} to False and indicates that the application has 
     * been saved since the last change. This can be done without saving if the user wishes to 
     * discard unsaved progress.
     */
    public static void setUpToDate() {
        changesMade = false;
        Paint.indicateSaved();
    }

    /**
     * Gets the {@link double} representing the width of the {@link Canvas}es.
     * @return The {@link double} representing the width of the {@link Canvas}es.
     */
    public static double getCanvasWidth() {
        return width;
    }

    /**
     * Gets the {@link double} representing the height of the {@link Canvas}es.
     * @return The {@link double} representing the height of the {@link Canvas}es.
     */
    public static double getCanvasHeight() {
        return height;
    }

    //When changes are made to this canvas this method should be called 
    //in addition to updateCanvas() 
    //Uundo features are static since they apply to the current canvas
    /**
     * This method adds a {@link Canvas#snapshot} of the current {@link Canvas} to its 
     * {@link Layer#undoStack}. This should be called just before the {@link Canvas} is updated.
     */
    public static void prepareUndo() {
        sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        myCurrentLayer.undoStack.push(myCurrentLayer.myCanvas.snapshot(sp, null));
        //prevImage = Layer.myCanvas.snapshot(null, null);
    }

    /**
     *This method adds a {@link Canvas#snapshot} of the current {@link Canvas} to its 
     * {@link Layer#redoStack}. This method is called just before an undo.
     */
    public static void prepareRedo() {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        myCurrentLayer.redoStack.push(myCurrentLayer.myCanvas.snapshot(sp, null));
    }

    /**
     * This method clears the current {@link Canvas} and then pops the top 
     * {@link Image} from the {@link Layer#undoStack} to draw on it.
     */
    public static void undo() {
        if (myCurrentLayer.undoStack.empty()) {
            return;
        }
        prepareRedo(); //Adds the current canvas to the top of the redo stack before
        //replacing it with the old canvas
        if (myCurrentLayer.layerOrder == 0) { //This if-else is necessary to handle transparency issues
            myCurrentLayer.gc.setFill(Color.WHITE);
            myCurrentLayer.gc.fillRect(0, 0, width, height);
        } else {
            myCurrentLayer.gc.clearRect(0, 0, width, height);
        }
        myCurrentLayer.gc.drawImage((Image) myCurrentLayer.undoStack.pop(), 0, 0, width, height);
        if (myCurrentLayer.undoStack.empty()) {
            setUpToDate();
        }

    }

    /**
     * This method clears the current {@link Canvas} and then pops the top 
     * {@link Image} from the {@link Layer#redoStack} to draw on it.
     */
    public static void redo() {
        if (myCurrentLayer.redoStack.empty()) {
            return;
        }
        prepareUndo();
        if (myCurrentLayer.layerOrder == 0) { //This if-else is necessary to handle transparency issues
            myCurrentLayer.gc.setFill(Color.WHITE);
            myCurrentLayer.gc.fillRect(0, 0, width, height);
        } else {
            myCurrentLayer.gc.clearRect(0, 0, width, height);
        }
        myCurrentLayer.gc.drawImage((Image) myCurrentLayer.redoStack.pop(), 0, 0, width, height);
        updateCanvas(myCurrentLayer.gc);

    }

    //For sorting the layers by their rank
    @Override
    public int compareTo(Object o) {
        double otherOrder = ((Layer) o).getLayerOrder();
        //For Ascending order
        return (int) (this.layerOrder - otherOrder);

    }

    /**
     * Clears all mouse action handlers from the current layer.
     */
    public static void clearHandlers() {
        myCurrentLayer.getCanvas().setOnMouseClicked(null);
        myCurrentLayer.getCanvas().setOnMouseDragged(null);
        myCurrentLayer.getCanvas().setOnMouseEntered(null);
        myCurrentLayer.getCanvas().setOnMouseExited(null);
        myCurrentLayer.getCanvas().setOnMouseMoved(null);
        myCurrentLayer.getCanvas().setOnMousePressed(null);
        myCurrentLayer.getCanvas().setOnMouseReleased(null);
    }

}
