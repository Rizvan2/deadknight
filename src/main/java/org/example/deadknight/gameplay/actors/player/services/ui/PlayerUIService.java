package org.example.deadknight.gameplay.actors.player.services.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.deadknight.gameplay.components.UpgradeComponent;

/**
 * Сервис для отображения UI игрока.
 * <p>
 * Отвечает за показ количества собранных "Осколков памяти" с иконкой
 * в правом верхнем углу экрана.
 * <p>
 * Все размеры и отступы вынесены в константы для удобной настройки интерфейса.
 */
public class PlayerUIService {

    // --- Константы интерфейса ---
    private static final double PADDING = 10;            // отступ фона от краев текста/иконки
    private static final double ICON_WIDTH = 72;         // ширина иконки осколка
    private static final double ICON_HEIGHT = 72;        // высота иконки
    private static final double ICON_TEXT_GAP = 10;       // расстояние между иконкой и текстом
    private static final double TEXT_FONT_SIZE = 24;     // размер шрифта
    private static final double TOP_OFFSET = 20;         // отступ от верхнего края окна

    private Text essenceText;
    private Rectangle background;
    private ImageView essenceIcon;

    /**
     * Инициализирует UI игрока.
     * <p>
     * Создает текст, иконку и фон, добавляет их на сцену и запускает таймер для
     * динамического обновления количества собранных осколков памяти.
     *
     * @param player сущность игрока, из которой читается компонент {@link UpgradeComponent}
     */
    public void initUI(Entity player) {
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

        FXGL.getGameTimer().runAtInterval(() -> {
            if (player.hasComponent(UpgradeComponent.class)) {
                UpgradeComponent upgrade = player.getComponent(UpgradeComponent.class);
                essenceText.setText(String.valueOf(upgrade.getCount()));
                updatePosition();
            }
        }, Duration.seconds(0.1));
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

        // Фон
        background.setWidth(totalWidth + PADDING);
        background.setTranslateX(x - background.getWidth());
        background.setHeight(Math.max(essenceText.getLayoutBounds().getHeight(), ICON_HEIGHT) + PADDING);
        background.setTranslateY(y);

        // Иконка слева
        essenceIcon.setTranslateX(background.getTranslateX() + PADDING / 2);
        essenceIcon.setTranslateY(y + (background.getHeight() - ICON_HEIGHT) / 2);

        // Текст справа от иконки
        essenceText.setTranslateX(essenceIcon.getTranslateX() + ICON_WIDTH + ICON_TEXT_GAP);
        essenceText.setTranslateY(y + background.getHeight() / 2 + TEXT_FONT_SIZE / 3); // примерно по центру фона
    }
}
