package org.example.deadknight.gameplay.actors.player.skills;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import org.example.deadknight.gameplay.actors.player.utils.InputActionManager;

import java.util.function.Supplier;

/**
 * Класс для регистрации и управления навыками рыцаря.
 */
public class KnightSkills {

    private final Supplier<Entity> knightSupplier;

    public KnightSkills(Supplier<Entity> knightSupplier) {
        this.knightSupplier = knightSupplier;
    }

    /** Регистрирует действие атаки на пробел только один раз. */
    public void registerAttack() {
        InputActionManager.registerAttack(knightSupplier, KeyCode.SPACE);
    }
}
