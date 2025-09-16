package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.deadknight.gameplay.components.UpgradeComponent;

/**
 * Сервис для отображения UI игрока.
 * В частности, счётчика апгрейд-эссенций.
 */
public class PlayerUIService {

    private Text essenceText;

    public void initUI(Entity player) {
        essenceText = FXGL.getUIFactoryService().newText("Essences: 0", Color.WHITE, 24);
        FXGL.addUINode(essenceText, 20, 40); // фиксированная позиция на экране

        FXGL.getGameTimer().runAtInterval(() -> {
            if (player.hasComponent(UpgradeComponent.class)) {
                UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
                essenceText.setText("Essences: " + upgrade.getCount());
            } else {
                System.out.println("No upgrade found");
            }
        }, Duration.seconds(0.1));

    }


}
