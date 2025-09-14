package org.example.deadknight.gameplay.actors.player.dialog;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import org.example.deadknight.gameplay.components.DialogueComponent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class DialogueSequenceComponent extends Component {

    private Queue<DialogueLine> queue = new LinkedList<>();
    private boolean active = false;
    private DialogueComponent dialogue;

    @Override
    public void onAdded() {
        dialogue = entity.getComponent(DialogueComponent.class);
    }

    public void startDialogue(DialogueLine... lines) {
        if (active) return;
        queue.clear();
        Collections.addAll(queue, lines);
        active = true;
        showNext();
    }

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
