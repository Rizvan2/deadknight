package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.components.DialogueComponent;
import org.example.deadknight.gameplay.components.HealthComponent;

public class DialogueService {

    public void tryShowDialogue(Entity entity, String message, double duration) {
        DialogueComponent dialogue = entity.getComponent(DialogueComponent.class);
        if (dialogue != null) {
            dialogue.showDialogue(message, duration);
        }
    }

    public void checkConditions(Entity player) {
        // Пример: показать сообщение, если здоровье меньше 50%
        HealthComponent health = player.getComponent(HealthComponent.class);
        if (health.getValue() < 50) {
            tryShowDialogue(player, "Нужна помощь!", 2.0);
        }
    }
}
