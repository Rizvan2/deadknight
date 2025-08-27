package org.example.deadknight.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.deadknight.init.GameInitializer;

public class CharacterSelectMenu {

    public static void show(Stage stage) {
        Button knightBtn = new Button("Рыцарь");
        knightBtn.setOnAction(e -> {
            GameInitializer.initGame("knight");
            stage.close();
        });

        Button pantherBtn = new Button();
        Image pantherImage = new Image("bleckpanter.png"); // путь к твоей картинке
        ImageView pantherView = new ImageView(pantherImage);

// Можно настроить размер картинки
        pantherView.setFitWidth(100);
        pantherView.setFitHeight(100);
        pantherView.setPreserveRatio(true);

        pantherBtn.setGraphic(pantherView);

        pantherBtn.setOnAction(e -> {
            GameInitializer.initGame("panther");
            stage.close();
        });

        VBox layout = new VBox(15, knightBtn, pantherBtn);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-spacing: 10;");

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Выбор персонажа");
        stage.show();
    }
}
