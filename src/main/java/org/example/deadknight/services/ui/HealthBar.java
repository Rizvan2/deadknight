package org.example.deadknight.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.deadknight.gameplay.components.HealthComponent;
import com.almasb.fxgl.entity.Entity;

/**
 * UI-компонент для отображения здоровья персонажа.
 * <p>
 * Показывает красный фон и зеленую полоску здоровья,
 * которая плавно уменьшается при уроне.
 */
public class HealthBar {

    private final Rectangle backgroundBar;
    private final Rectangle foregroundBar;
    private final Entity knight;
    private final Text hpText;

    /**
     * Создаёт индикатор здоровья для указанного персонажа.
     *
     * @param knight сущность персонажа (например, рыцаря)
     */
    public HealthBar(Entity knight) {
        this.knight = knight;

        // Красный фон
        backgroundBar = new Rectangle(200, 20);
        backgroundBar.setFill(Color.DARKRED);
        backgroundBar.setTranslateX(20);
        backgroundBar.setTranslateY(20);

        // Зелёная полоска с градиентом
        foregroundBar = new Rectangle(200, 20);
        foregroundBar.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.LIMEGREEN),
                new Stop(1, Color.DARKGREEN)
        ));
        foregroundBar.setTranslateX(20);
        foregroundBar.setTranslateY(20);

        // Текст с текущим HP
        hpText = new Text("100");
        hpText.setFont(Font.font(16));
        hpText.setFill(Color.WHITE);
        hpText.setTranslateX(100);  // центрируем примерно
        hpText.setTranslateY(35);

        FXGL.getGameScene().addUINode(backgroundBar);
        FXGL.getGameScene().addUINode(foregroundBar);
        FXGL.getGameScene().addUINode(hpText);
    }

    /**
     * Обновляет ширину зелёной полоски и текстовое значение HP.
     */
    public void update() {
        if (knight == null || knight.getWorld() == null) {
            return;
        }

        HealthComponent health = knight.getComponent(HealthComponent.class);
        double hp = health.getValue();
        double targetWidth = 200 * Math.max(0, hp / 100.0);

        // Анимация изменения ширины
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(foregroundBar.widthProperty(), targetWidth))
        );
        timeline.play();

        // Обновление текста
        hpText.setText((int) hp + " HP");
    }

}
