package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
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

    /**
     * Текстовый элемент, отображаемый над сущностью.
     */
    private final Text dialogueText;

    /**
     * Создаёт компонент диалогов и добавляет текстовый узел в сцену FXGL.
     */
    public DialogueComponent() {
        dialogueText = new Text();
        dialogueText.setFont(Font.font("Verdana", 16));
        dialogueText.setFill(Color.WHITE);
        dialogueText.setStroke(Color.BLACK);
        dialogueText.setStrokeWidth(0.5);
        dialogueText.setVisible(false);

        FXGL.getGameScene().addUINode(dialogueText);
    }

    /**
     * Показывает сообщение над сущностью на указанное время.
     * <p>
     * Текст автоматически исчезает после истечения времени {@code durationSeconds}.
     *
     * @param message текст сообщения
     * @param durationSeconds время отображения в секундах
     */
    public void showDialogue(String message, double durationSeconds) {
        dialogueText.setText(message);
        dialogueText.setVisible(true);

        FXGL.getGameTimer().runOnceAfter(() -> dialogueText.setVisible(false),
                Duration.seconds(durationSeconds));
    }

    /**
     * Обновляет позицию текста каждый кадр.
     * <p>
     * Текст следует за сущностью, компенсируя положение камеры,
     * и располагается немного выше сущности.
     *
     * @param tpf время, прошедшее с последнего кадра (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        if (!dialogueText.isVisible()) return;

        double camX = FXGL.getGameScene().getViewport().getX();
        double camY = FXGL.getGameScene().getViewport().getY();

        dialogueText.setTranslateX(entity.getX() - camX);
        dialogueText.setTranslateY(entity.getY() - camY - 50);
    }
}
