
package paint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
        Canvas blank = ImageCanvas.defaultCanvas(); //Creates a blank white canvas
        FileBar fileDrpDwn = new FileBar(imagePane, primaryStage);
        Menu homeBtn = new Menu("Home");
        Menu viewBtn = new Menu("View");
        mainMenu.getMenus().addAll(fileDrpDwn, homeBtn, viewBtn);

        imagePane.getChildren().add(blank);
        imagePane.setAlignment(blank, Pos.CENTER);
        root.setTop(topContainer);
        root.setCenter(imagePane);

        Scene scene = new Scene(root);
        root.requestFocus(); //This makes it so no button is highlighted upon opening the program

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent c) {
                attemptClose();
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

    private static void close(Stage unsavedWorkPrompt) {
        primaryStage.hide();
        unsavedWorkPrompt.hide();
        Platform.exit();
    }

    private static void cancel(Stage unsavedWorkPrompt) {
        unsavedWorkPrompt.close();
    }

    public static void attemptClose() {
        // hasUnsavedProgress checks if the canvas has been changed since the last save
        // and isNotSaved returns true when a save file hasn't been created yet.
        // This prevents the program from closing when exiting the save dialog after 
        // choosing to save changes.
        if ((ImageCanvas.hasUnsavedProgress() || FileBar.isNotSaved()) && !(!ImageCanvas.hasUnsavedProgress() && FileBar.isNotSaved())) {

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
            close.setOnAction(e -> close(newWindow));
            cancel.setOnAction(e -> cancel(newWindow));

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();

        } else {
            Platform.exit();
        }
    }

    //This add a temporary canvas for previewing shapes while dragging
    //It could potentially be useful for an undo feature in the future
    public static void addLayer(Canvas newLayer) {
        imagePane.getChildren().add(0, newLayer);
        newLayer.toFront();

    }
    
    public static void removeLayer(Canvas oldLayer) {
        imagePane.getChildren().remove(oldLayer);
    }

    public static void attemptOpenNew(Boolean blankCanvas) {
        // hasUnsavedProgress checks if the canvas has been changed since the last save
        // and isNotSaved returns true when a save file hasn't been created yet.
        // This prevents the program from closing when exiting the save dialog after 
        // choosing to save changes.
        if ((ImageCanvas.hasUnsavedProgress() || FileBar.isNotSaved()) && !(!ImageCanvas.hasUnsavedProgress() && FileBar.isNotSaved())) {

            BorderPane warningPane = new BorderPane();
            HBox buttons = new HBox();
            Label warningMessage = new Label("Do you want to save your progress?");
            warningPane.setTop(warningMessage);
            Button saveChanges = new Button("Save");
            Button openAnyway = new Button("Don't Save");
            Button cancel = new Button("Cancel");

            buttons.getChildren().addAll(saveChanges, openAnyway, cancel);
            warningPane.setCenter(buttons);

            Scene secondScene = new Scene(warningPane, 250, 100);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Warning: Unsaved Changes");
            newWindow.setScene(secondScene);

            saveChanges.setOnAction(e -> saveAndClose(newWindow));
            openAnyway.setOnAction(e -> openAnyway(newWindow, blankCanvas));
            
            cancel.setOnAction(e -> cancel(newWindow));

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();

        } else {
            if (blankCanvas) {
                FileBar.newBlank();
            } else {
                FileBar.openFile();
            }

        }
    }
    
    private static void openAnyway(Stage unsavedWorkPrompt, Boolean blankCanvas) {
        if (blankCanvas) {
                FileBar.newBlank();
            } else {
                FileBar.openFile();
            }
        unsavedWorkPrompt.hide();
    }
    
    

}
