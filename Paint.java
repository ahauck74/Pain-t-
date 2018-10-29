package paint;

import java.util.Collection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
 *
 * @author ahauc
 */
public class Paint extends Application {

    private static BorderPane root;
    private static StackPane imagePane;
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

        FileBar fileDrpDwn = new FileBar(imagePane, primaryStage);
        mainMenu.getMenus().addAll(fileDrpDwn);

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
        //attemptClose(primaryStage);
    }
    
    private static void dontSave(Stage unsavedWorkPrompt, Boolean makeNew) {
        unsavedWorkPrompt.close();
        noSave(makeNew);
    }
    
    private static void noSave(Boolean makeNew) {
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
     *
     * @param makeNew
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

            saveChanges.setOnAction(e -> saveAndClose(newWindow));
            close.setOnAction(e -> dontSave(newWindow, makeNew));
            cancel.setOnAction(e -> cancel(newWindow));

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();

        } else {
            noSave(makeNew);
        }
    }

    //This add a temporary canvas for previewing shapes while dragging
    //It could potentially be useful for an undo feature in the future

    /**
     *
     * @param newLayerCanvas
     */
    public static void addCanvas(Canvas newLayerCanvas) {
        imagePane.getChildren().add(0, newLayerCanvas);
        newLayerCanvas.toFront();
    }
    
    /**
     *
     * @param textBox
     */
    public static void addText(TextArea textBox) {
        imagePane.getChildren().add(0, textBox);
        textBox.toBack();
    }
    
    /**
     *
     * @param textBox
     */
    public static void removeText(TextArea textBox) {
        imagePane.getChildren().remove( textBox);
    }

    /**
     *
     * @param layers
     */
    public static void addCanvases(Collection layers) {

    }

    /**
     *
     */
    public static void removeAllCanvases() {
        imagePane.getChildren().clear();
    }

    /**
     *
     * @param oldLayerCanvas
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
     *
     */
    public static void indicateUnsaved() {
        primaryStage.setTitle("Pain(t)*");
    }
    
    /**
     *
     */
    public static void indicateSaved() {
        primaryStage.setTitle("Pain(t)");
    }

}
