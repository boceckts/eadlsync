package io.github.adr.eadlsync.gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import io.github.adr.eadlsync.model.diff.DiffManager;

/**
 *
 */
public class ConflictManagerView {

    private ConflictManagerController conflictManagerController;

    public ConflictManagerView(DiffManager diffManager) {
        conflictManagerController = new ConflictManagerController(diffManager);
    }

    public boolean showDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/conflict/resolve-conflicts-view.fxml"));
        try {
            loader.setController(conflictManagerController);
            DialogPane root = loader.load();
            root.getStylesheets().add(getClass().getResource("/gui/conflict/resolve-conflicts-view.css").toExternalForm());

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Resolve conflicts");
            alert.setResizable(true);
            alert.setDialogPane(root);
            alert.initModality(Modality.WINDOW_MODAL);

            Window window = alert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());

            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return conflictManagerController.isFinishedSuccessfully();
    }


}
