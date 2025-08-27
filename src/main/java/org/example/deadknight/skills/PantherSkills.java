package org.example.deadknight.skills;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.function.Supplier;

public class PantherSkills {

    private final Supplier<Entity> pantherSupplier;

    public PantherSkills(Supplier<Entity> pantherSupplier) {
        this.pantherSupplier = pantherSupplier;
    }

    /** Регистрирует действие атаки на пробел */
    public void registerAttack() {
        FXGL.getInput().addAction(new UserAction("PantherAttack") {
            @Override
            protected void onActionBegin() {
                Entity panther = pantherSupplier.get();
                if (panther == null || panther.getWorld() == null) return;

                Runnable playAttack = (Runnable) panther.getProperties()
                        .getValueOptional("playAttack")
                        .orElse(null);

                if (playAttack != null) {
                    Boolean isAttacking = panther.getProperties().getBoolean("isAttacking");
                    if (isAttacking == null || !isAttacking) {
                        playAttack.run();
                    }
                }
            }
        }, KeyCode.SPACE);
    }
}
