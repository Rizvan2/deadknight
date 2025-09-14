package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Компонент диалогов для сущности.
 * <p>
 * Позволяет отображать текст над сущностью на короткое время.
 * Текст автоматически следует за сущностью и учитывает положение камеры.
 * <p>
 * Пример использования:
 * <pre>{@code
 * Entity knight = FXGL.entityBuilder()
 *     .with(new DialogueComponent())
 *     .build();
 *
 * knight.getComponent(DialogueComponent.class).showDialogue("Привет!", 2.0);
 * }</pre>
 */
public class DialogueComponent extends Component {

    private final StackPane dialoguePane;
    private final Text dialogueText;
    private final Rectangle background;

    public DialogueComponent() {
        dialogueText = new Text();
        dialogueText.setFont(Font.font("Verdana", 16));
        dialogueText.setFill(Color.WHITE); // белый текст

        background = new Rectangle();
        background.setArcWidth(10);   // скругленные углы
        background.setArcHeight(10);
        background.setFill(Color.color(0, 0, 0, 0.7)); // черный с прозрачностью

        dialoguePane = new StackPane(background, dialogueText);
        dialoguePane.setVisible(false);

        FXGL.getGameScene().addUINode(dialoguePane);
    }

    public void showDialogue(String message, double durationSeconds) {
        dialogueText.setText(message);

        // авто-подгонка фона под текст
        background.setWidth(dialogueText.getLayoutBounds().getWidth() + 10);
        background.setHeight(dialogueText.getLayoutBounds().getHeight() + 6);

        dialoguePane.setVisible(true);

        FXGL.getGameTimer().runOnceAfter(() -> dialoguePane.setVisible(false),
                Duration.seconds(durationSeconds));
    }

    @Override
    public void onUpdate(double tpf) {
        if (!dialoguePane.isVisible()) return;

        var viewport = FXGL.getGameScene().getViewport();

        double zoom = viewport.getZoom();

        // камера учитывает смещение и зум
        double screenX = (entity.getX() - viewport.getX()) * zoom;
        double screenY = (entity.getY() - viewport.getY()) * zoom;

        dialoguePane.setTranslateX(screenX);
        dialoguePane.setTranslateY(screenY - 50 * zoom); // чуть выше сущности
    }


}
