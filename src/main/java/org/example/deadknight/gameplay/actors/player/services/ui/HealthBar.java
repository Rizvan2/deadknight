package org.example.deadknight.gameplay.actors.player.services.ui;

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

    /** Ширина всей полоски здоровья. */
    private static final double BAR_WIDTH = 400;

    /** Высота всей полоски здоровья. */
    private static final double BAR_HEIGHT = 40;

    /** Координата X верхнего левого угла полоски здоровья. */
    private static final double BAR_X = 40;

    /** Координата Y верхнего левого угла полоски здоровья. */
    private static final double BAR_Y = 40;

    /** Размер шрифта для текста текущего HP. */
    private static final double HP_TEXT_FONT = 32;

    /** Смещение текста HP по вертикали относительно центра полоски. */
    private static final double HP_TEXT_OFFSET_Y = 10;

    /** Красный фон полоски здоровья. */
    private final Rectangle backgroundBar;

    /** Зелёная полоска, показывающая текущее здоровье. */
    private final Rectangle foregroundBar;

    /** Сущность персонажа, для которого отображается полоска здоровья. */
    private final Entity knight;

    /** Текстовое отображение текущего HP. */
    private final Text hpText;

    /**
     * Создаёт UI-компонент полоски здоровья для указанного персонажа.
     * <p>
     * Конструктор инициализирует красный фон, зелёную полоску здоровья с градиентом
     * и текстовое отображение текущего HP. Все элементы автоматически добавляются
     * в игровую сцену FXGL и сразу отображают текущее значение здоровья.
     *
     * @param knight сущность персонажа, для которого создаётся индикатор здоровья.
     *               Например, рыцарь или другой игровой юнит, имеющий компонент {@link HealthComponent}.
     */
    public HealthBar(Entity knight) {
        this.knight = knight;

        backgroundBar = createBackgroundBar();
        foregroundBar = createForegroundBar();
        hpText = createHpText();

        FXGL.getGameScene().addUINode(backgroundBar);
        FXGL.getGameScene().addUINode(foregroundBar);
        FXGL.getGameScene().addUINode(hpText);

        update(); // сразу обновляем текст и полоску
    }

    /** Создаёт красный фон для полоски здоровья */
    private Rectangle createBackgroundBar() {
        Rectangle rect = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        rect.setFill(Color.DARKRED);
        rect.setTranslateX(BAR_X);
        rect.setTranslateY(BAR_Y);
        return rect;
    }

    /** Создаёт зелёную полоску здоровья с градиентом */
    private Rectangle createForegroundBar() {
        Rectangle rect = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        rect.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.LIMEGREEN),
                new Stop(1, Color.DARKGREEN)
        ));
        rect.setTranslateX(BAR_X);
        rect.setTranslateY(BAR_Y);
        return rect;
    }

    /** Создаёт текстовое отображение HP */
    private Text createHpText() {
        Text text = new Text();
        text.setFont(Font.font(HP_TEXT_FONT));
        text.setFill(Color.WHITE);
        return text;
    }

    /** Обновляет полоску здоровья и текст */
    public void update() {
        if (!isKnightValid()) return;

        double hp = getHp();
        updateForegroundWidth(hp);
        updateHpText(hp);
    }

    /** Проверка валидности сущности */
    private boolean isKnightValid() {
        return knight != null && knight.getWorld() != null;
    }

    /** Получение текущего HP */
    private double getHp() {
        return knight.getComponent(HealthComponent.class).getValue();
    }

    /** Анимированное обновление ширины зелёной полоски */
    private void updateForegroundWidth(double hp) {
        double targetWidth = BAR_WIDTH * Math.max(0, hp / 100.0);
        new Timeline(new KeyFrame(Duration.seconds(0.3),
                new KeyValue(foregroundBar.widthProperty(), targetWidth)
        )).play();
    }

    /** Центрирует текст по ширине полоски */
    private void updateHpText(double hp) {
        String hpString = (int) hp + " HP";
        hpText.setText(hpString);
        double textWidth = hpText.getLayoutBounds().getWidth();
        hpText.setTranslateX(BAR_X + BAR_WIDTH / 2 - textWidth / 2);
        hpText.setTranslateY(BAR_Y + BAR_HEIGHT / 2 + HP_TEXT_OFFSET_Y);
    }
}
