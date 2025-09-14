package org.example.deadknight.gameplay.actors.player.dialog;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import org.example.deadknight.gameplay.actors.player.dialog.DialogueLine;
import org.example.deadknight.gameplay.actors.player.dialog.DialogueScripts;

public class PlayerDialogueComponent extends Component {

    private DialogueSequenceComponent sequence;

    @Override
    public void onAdded() {
        sequence = entity.getComponent(DialogueSequenceComponent.class);

        // пример стартового диалога
        FXGL.getGameTimer().runOnceAfter(() -> sequence.startDialogue(DialogueScripts.START_GAME()),
                javafx.util.Duration.seconds(2));
    }

    public void onFirstKill() {
        sequence.startDialogue(DialogueScripts.FIRST_KILL());
    }

    // можно добавлять методы для других событий
}
