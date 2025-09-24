package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.deadknight.gameplay.components.UpgradeComponent;

/**
 * Сервис для отображения UI игрока.
 * <p>
 * Отвечает за отображение количества собранных "Осколков памяти" с иконкой
 * в правом верхнем углу экрана. Обновление количества осколков производится
 * при вызове метода {@link #update()}.
 * <p>
 * Все размеры и отступы элементов UI вынесены в константы для удобной настройки интерфейса.
 */
public class PlayerUIService {

    // --- Константы интерфейса ---
    private static final double PADDING = 10;
    private static final double ICON_WIDTH = 72;
    private static final double ICON_HEIGHT = 72;
    private static final double ICON_TEXT_GAP = 10;
    private static final double TEXT_FONT_SIZE = 24;
    private static final double TOP_OFFSET = 20;

    private Text essenceText;       // Текстовое поле для отображения количества осколков
    private Rectangle background;   // Фон для иконки и текста
    private ImageView essenceIcon;  // Иконка осколка
    private Entity player;          // Сущность игрока, чьи осколки отображаются

    /**
     * Инициализирует UI для игрока.
     * <p>
     * Создает текст, иконку и фон, добавляет их на сцену и сохраняет ссылку
     * на игрока для обновления количества осколков.
     *
     * @param player сущность игрока, из которой читается компонент {@link UpgradeComponent}
     */
    public void initUI(Entity player) {
        this.player = player; // сохраняем игрока для update()

        essenceText = new Text("0");
        essenceText.setFill(Color.WHITE);
        essenceText.setFont(Font.font("Consolas", TEXT_FONT_SIZE));

        essenceIcon = new ImageView(FXGL.image("essences/upgradeEssence/eclipse_of_forgotten_souls.png"));
        essenceIcon.setFitWidth(ICON_WIDTH);
        essenceIcon.setFitHeight(ICON_HEIGHT);

        background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setHeight(Math.max(essenceText.getLayoutBounds().getHeight(), ICON_HEIGHT) + PADDING);

        FXGL.getGameScene().addUINode(background);
        FXGL.getGameScene().addUINode(essenceIcon);
        FXGL.getGameScene().addUINode(essenceText);

        Platform.runLater(this::updatePosition);
    }

    /**
     * Обновляет UI игрока.
     * <p>
     * Метод должен вызываться каждый кадр (например, в {@code onUpdate()}).
     * Обновляет текст осколков и корректирует позиции элементов.
     */
    public void update() {
        if (player != null && player.hasComponent(UpgradeComponent.class)) {
            UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
            essenceText.setText(String.valueOf(upgrade.getCount()));
            updatePosition();
        }
    }

    /**
     * Обновляет позиции текста, иконки и фона.
     * <p>
     * Текст располагается справа от иконки, фон охватывает оба элемента с отступами.
     */
    private void updatePosition() {
        double x = FXGL.getAppWidth() - PADDING;
        double y = TOP_OFFSET;

        double textWidth = essenceText.getLayoutBounds().getWidth();
        double totalWidth = ICON_WIDTH + ICON_TEXT_GAP + textWidth;

        background.setWidth(totalWidth + PADDING);
        background.setTranslateX(x - background.getWidth());
        background.setHeight(Math.max(essenceText.getLayoutBounds().getHeight(), ICON_HEIGHT) + PADDING);
        background.setTranslateY(y);

        essenceIcon.setTranslateX(background.getTranslateX() + PADDING / 2);
        essenceIcon.setTranslateY(y + (background.getHeight() - ICON_HEIGHT) / 2);

        essenceText.setTranslateX(essenceIcon.getTranslateX() + ICON_WIDTH + ICON_TEXT_GAP);
        essenceText.setTranslateY(y + background.getHeight() / 2 + TEXT_FONT_SIZE / 3);
    }
}
