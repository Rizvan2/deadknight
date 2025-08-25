package org.example.deadknight.skills;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.function.Supplier;

/**
 * Класс для управления навыками рыцаря.
 * Здесь регистрируем все действия вроде атаки, специальных умений и т.д.
 */
public class KnightSkills {

    private final Supplier<Entity> knightSupplier;

    public KnightSkills(Supplier<Entity> knightSupplier) {
        this.knightSupplier = knightSupplier;
    }

    /** Регистрирует действие атаки на пробел. */
    public void registerAttack() {
        FXGL.getInput().addAction(new UserAction("Attack") {
            @Override
            protected void onActionBegin() {
                Entity knight = knightSupplier.get();
                if (knight == null || knight.getWorld() == null) return;

                Runnable playAttack = knight.getProperties().getValue("playAttack");
                if (playAttack != null) {
                    playAttack.run();
                }
            }
        }, KeyCode.SPACE);
    }
}
