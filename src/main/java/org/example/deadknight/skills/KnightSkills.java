package org.example.deadknight.skills;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.example.deadknight.utils.InputActionManager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

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
