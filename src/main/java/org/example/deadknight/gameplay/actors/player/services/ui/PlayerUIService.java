package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.deadknight.gameplay.components.UpgradeComponent;

public class PlayerUIService {

    private Text essenceText;
    private Rectangle background;

    public void initUI(Entity player) {
        essenceText = new Text("Осколки памяти: 0");
        essenceText.setFill(Color.WHITE);
        essenceText.setFont(Font.font("Consolas", 24));

        background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setHeight(essenceText.getLayoutBounds().getHeight() + 10);

        FXGL.getGameScene().addUINode(background);
        FXGL.getGameScene().addUINode(essenceText);

        // Устанавливаем позиции после рендера
        Platform.runLater(() -> updatePosition());

        FXGL.getGameTimer().runAtInterval(() -> {
            if (player.hasComponent(UpgradeComponent.class)) {
                UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
                essenceText.setText("Осколки памяти: " + upgrade.getCount());

                // Обновляем фон и позицию после изменения текста
                updatePosition();
            }
        }, Duration.seconds(0.1));
    }

    private void updatePosition() {
        double textWidth = essenceText.getLayoutBounds().getWidth();
        double textHeight = essenceText.getLayoutBounds().getHeight();
        double padding = 10;
        double x = FXGL.getAppWidth() - padding - textWidth;
        double y = 10;

        background.setWidth(textWidth + padding);
        background.setHeight(textHeight + padding);
        background.setTranslateX(x - padding / 2);
        background.setTranslateY(y - padding / 2);

        essenceText.setTranslateX(x);
        essenceText.setTranslateY(textHeight / 2 + 15); // текст по центру фона
    }
}
