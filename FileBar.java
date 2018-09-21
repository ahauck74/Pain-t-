/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author ahauc
 */
public class FileBar extends Menu {

    private static Pane imagePane;
    private static Stage stage;
    private static Image image;
    private static File file;
    private static FileChooser fileChooser;

    public FileBar(Pane pane, Stage stage) {
        this.setText("File");
        FileBar.imagePane = pane;
        MenuItem makeNew = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem save = new MenuItem("_Save");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        
        this.getItems().addAll(makeNew, open, save, saveAs, undo, redo);
        
        
        
        //Keyboard shortcuts
        
        makeNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        
        
        FileBar.stage = stage;

        makeNew.setOnAction(e -> Paint.attemptOpenNew(true)); //This checks for unsaved progress before opening a new canvas
        save.setOnAction(e -> saveToFile());
        saveAs.setOnAction(e -> saveAs());
        open.setOnAction(e -> Paint.attemptOpenNew(false));
        undo.setOnAction(e -> ImageCanvas.undo());
        redo.setOnAction(e -> ImageCanvas.redo());
        
        fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG
                = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg
                = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG
                = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters()
                .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
    }

    

    //Sets filepath to null so that when saving, it prompts for a new filepath
    public static void saveAs() {
        file = null;
        saveToFile();
    }

    public static void saveToFile() {
        System.out.println("Saving...");

        //Show save file dialog if file hasn't been saved yet
        if (file == null) {
            file = fileChooser.showSaveDialog(stage); //Returns null if the dialog is closed without saving
        }

        if (file != null) {
            saveFile(file);
        }
    }

    public static void saveFile(File file) {
        Canvas myCanvas = ImageCanvas.getCanvas();
        WritableImage wImage = myCanvas.snapshot(null, null);

        BufferedImage bImage = SwingFXUtils.fromFXImage(wImage, null);
        try {
            ImageIO.write(bImage, "png", file);
            ImageCanvas.savedProgress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
   
    public static void newBlank() {
        Canvas myCanvas = ImageCanvas.defaultCanvas();
        imagePane.getChildren().clear();
        imagePane.getChildren().add(myCanvas);
    }
    
    public static void openFile() {
        //Use a different file variable when opening to avoid defaulting to 
        //overwriting the original file.
        File openFile = fileChooser.showOpenDialog(null);
        image = new Image(openFile.toURI().toString());
        Canvas myCanvas = ImageCanvas.newCanvas(image);
        imagePane.getChildren().clear();
        imagePane.getChildren().add(myCanvas);
    }

    public static Image getImage() {
        return image;
    }

    public static Boolean isNotSaved() {
        return (file == null);
    }
    
    

}
