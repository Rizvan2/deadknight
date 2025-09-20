package org.example.deadknight.gameplay.actors.player.dialog;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import org.example.deadknight.gameplay.components.DialogueComponent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Компонент, управляющий последовательностью диалогов для сущности.
 * <p>
 * Позволяет запускать цепочку реплик {@link DialogueLine}, которые автоматически
 * показываются одна за другой с заданной задержкой.
 */
public class DialogueSequenceComponent extends Component {


    /**
     * Очередь реплик для отображения.
     */
    private Queue<DialogueLine> queue = new LinkedList<>();

    /**
     * Флаг, указывающий, активен ли сейчас диалог.
     */
    private boolean active = false;

    /**
     * Компонент, отвечающий за отображение текста диалога.
     */
    private DialogueComponent dialogue;

    /**
     * Инициализация: получает {@link DialogueComponent} у сущности.
     */
    @Override
    public void onAdded() {
        dialogue = entity.getComponent(DialogueComponent.class);
    }

    /**
     * Запускает диалоговую последовательность.
     *
     * @param lines массив реплик, которые должны отображаться по очереди
     */
    public void startDialogue(DialogueLine... lines) {
        if (active) return;
        queue.clear();
        Collections.addAll(queue, lines);
        active = true;
        showNext();
    }

    /**
     * Отображает следующую реплику из очереди.
     * <p>
     * После завершения текущей реплики автоматически планирует показ следующей
     * через {@link FXGL#getGameTimer()}.
     */
    private void showNext() {
        DialogueLine line = queue.poll();
        if (line == null) {
            active = false;
            return;
        }

        dialogue.showDialogue(line.getMessage(), line.getDurationSeconds());

        FXGL.getGameTimer().runOnceAfter(this::showNext,
                javafx.util.Duration.seconds(line.getDurationSeconds() + 0.2));
    }
}
