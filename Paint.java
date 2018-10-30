package paint;

import java.util.Collection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *The Paint class is the main file for the paint application. It extends {@link Application} 
 * sets the {@link Stage}. 
 * @author ahauc
 */
public class Paint extends Application {

    /**
     * The {@link BorderPane} that holds the general layout for the application. In the top 
     * is a {@link VBox} that contains the {@link MenuBar} and {@link Tools}. 
     */
    private static BorderPane root;
    
    /**
     * The {@link StackPane} contains all the {@link Layer} objects which hold the 
     * {@link Canvas} objects.
     */
    private static StackPane imagePane;
    
    /**
     * The {@link Stage} is the primary window for the application.
     */
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Paint.primaryStage = primaryStage;
        primaryStage.setTitle("Pain(t)");

        root = new BorderPane();
        VBox topContainer = new VBox();
        MenuBar mainMenu = new MenuBar();
        Tools toolBar = new Tools();
        topContainer.getChildren().addAll(mainMenu, toolBar);
        imagePane = new StackPane();

        FileBar fileDropDown = new FileBar(imagePane, primaryStage);
        mainMenu.getMenus().addAll(fileDropDown);

        LayerOrganizer layerBar = new LayerOrganizer();
        Layer blank = new Layer(); //Creates a blank white canvas

        imagePane.setAlignment(Layer.getCurrentCanvas(), Pos.CENTER);
        root.setTop(topContainer);
        root.setCenter(imagePane);
        root.setRight(layerBar);

        Scene scene = new Scene(root);
        root.requestFocus(); //This makes it so no button is highlighted upon opening the program

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent c) {
                attemptClose(null);
                c.consume();
            }
        });
        primaryStage.show();
    }

    private static void saveAndClose(Stage unsavedWorkPrompt) {
        FileBar.saveToFile();
        unsavedWorkPrompt.close();
    }
    
    private static void dontSave(Stage unsavedWorkPrompt, Boolean makeNew) {
        unsavedWorkPrompt.close();
        noSaveNeeded(makeNew);
    }
    
    private static void noSaveNeeded(Boolean makeNew) {
        if (makeNew == null) {
            System.out.println("Closing: " + makeNew);
            Platform.exit();
        } else if (makeNew) {
            FileBar.newBlank();
        } else {
            FileBar.openFile();
        }
    }

    private static void cancel(Stage unsavedWorkPrompt) {
        unsavedWorkPrompt.close();
    }

    /**
     * This method is called when the user tries to close, open, or open new with 
     * unsaved progress.
     * @param makeNew For attempting to exit the program, makeNew is passed as null. If 
     * attempting to create a new canvas, makeNew is true, and false for opening an image.
     */
    public static void attemptClose(Boolean makeNew) {//null for close, true for new canvas, false for open image
        
        // hasUnsavedProgress checks if the canvas has been changed since the last save
        // and isNotSaved returns true when a save file hasn't been created yet.
        // This prevents the program from closing when exiting the save dialog after 
        // choosing to save changes.
        if ((Layer.hasUnsavedProgress() || FileBar.isNotSaved()) && !(!Layer.hasUnsavedProgress() && FileBar.isNotSaved())) {

            BorderPane warningPane = new BorderPane();
            HBox buttons = new HBox();
            Label warningMessage = new Label("Do you want to save your progress?");
            warningPane.setTop(warningMessage);
            Button saveChanges = new Button("Save");
            Button close = new Button("Don't Save");
            Button cancel = new Button("Cancel");

            buttons.getChildren().addAll(saveChanges, close, cancel);
            warningPane.setCenter(buttons);

            Scene secondScene = new Scene(warningPane, 250, 100);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Warning: Unsaved Changes");
            newWindow.setScene(secondScene);

            //Button action handlers
            saveChanges.setOnAction(e -> saveAndClose(newWindow));
            close.setOnAction(e -> dontSave(newWindow, makeNew));
            cancel.setOnAction(e -> cancel(newWindow));

            // Set position of second window, relative to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);
            newWindow.show();
        } else {
            noSaveNeeded(makeNew);
        }
    }

    /**
     * Adds a new {@link Canvas} to the front of the {@link Paint#imagePane}
     * @param newCanvas The {@link Canvas} to be added to the {@link Paint#imagePane}.
     */
    public static void addCanvas(Canvas newCanvas) {
        imagePane.getChildren().add(0, newCanvas);
        newCanvas.toFront();
    }
    
    /**
     * Adds a {@link TextArea} to handle keyboard input for the {@link TextButton}. It 
     * is added to the back of the {@link Paint#imagePane} so that it isn't seen. Instead the text is 
     * copied from it and added to the front {@link Canvas} with {@link GraphicsContext#strokeText}.
     * @param  textBox The {@link TextArea} to be added inconspicuously to the back 
     * of the {@link imagePane}.
     */
    public static void addText(TextArea textBox) {
        imagePane.getChildren().add(0, textBox);
        textBox.toBack();
    }
    
    /**
     * Removes the {@link TextArea} added by {@link Paint#addText}. 
     * @param textBox The {@link TextArea} to be removed.
     */
    public static void removeText(TextArea textBox) {
        imagePane.getChildren().remove( textBox);
    }

    /**
     * Removes all the {@link Object}s from the {@link Paint#imagePane}.
     */
    public static void removeAllCanvases() {
        imagePane.getChildren().clear();
    }

    /**
     * Removes a single {@link Canvas} from the {@link Paint#imagePane}.
     * @param oldLayerCanvas The {@link Canvas} to be removed.
     */
    public static void removeCanvas(Canvas oldLayerCanvas) {
        imagePane.getChildren().remove(oldLayerCanvas);
    }

    private static void openAnyway(Stage unsavedWorkPrompt, Boolean blankCanvas) {
        if (blankCanvas) {
            FileBar.newBlank();
        } else {
            FileBar.openFile();
        }
        unsavedWorkPrompt.hide();
    }
    
    /**
     * Indicates in the application heading that there is unsaved work.
     */
    public static void indicateUnsaved() {
        primaryStage.setTitle("Pain(t)*");
    }
    
    /**
     * Updates the application heading to indicate that there is no unsaved work.
     */
    public static void indicateSaved() {
        primaryStage.setTitle("Pain(t)");
    }

}
