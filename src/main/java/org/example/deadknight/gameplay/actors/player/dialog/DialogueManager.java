package org.example.deadknight.gameplay.actors.player.dialog;

import com.almasb.fxgl.dsl.FXGL;
import java.util.Queue;
import java.util.LinkedList;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.components.DialogueComponent;

/**
 * Менеджер диалогов для персонажа.
 * <p>
 * Позволяет показывать последовательность реплик над сущностью с заданной длительностью.
 * Использует {@link DialogueComponent} сущности для отображения текста.
 * <p>
 * Поддерживает очередь диалогов и автоматически показывает следующие реплики после окончания предыдущей.
 * </p>
 */
public class DialogueManager {

    /** Очередь реплик для отображения. */
    private final Queue<DialogueLine> queue = new LinkedList<>();

    /** Флаг активности диалога (чтобы не накладывались одновременно). */
    private boolean active = false;

    /**
     * Запускает диалог для сущности с указанными линиями.
     * <p>
     * Если диалог уже активен, новый вызов игнорируется.
     * </p>
     *
     * @param entity сущность, над которой будут показываться реплики
     * @param lines последовательность реплик ({@link DialogueLine})
     */
    public void startDialogue(Entity entity, DialogueLine... lines) {
        if (active) return; // чтобы не накладывалось
        queue.clear();
        for (DialogueLine line : lines) queue.add(line);
        active = true;
        showNext(entity);
    }

    /**
     * Показывает следующую реплику из очереди.
     * <p>
     * Автоматически вызывает себя после окончания текущей реплики,
     * пока очередь не опустеет.
     * </p>
     *
     * @param entity сущность, над которой показывается реплика
     */
    private void showNext(Entity entity) {
        DialogueLine line = queue.poll();
        if (line == null) {
            active = false;
            return;
        }

        entity.getComponent(DialogueComponent.class)
                .showDialogue(line.getMessage(), line.getDurationSeconds());

        FXGL.getGameTimer().runOnceAfter(() -> showNext(entity),
                javafx.util.Duration.seconds(line.getDurationSeconds() + 0.3));
    }
}
