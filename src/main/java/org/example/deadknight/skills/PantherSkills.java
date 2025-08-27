package org.example.deadknight.skills;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.example.deadknight.utils.InputActionManager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Класс для регистрации и управления навыками пантеры.
 */
public class PantherSkills {

    private final Supplier<Entity> pantherSupplier;

    public PantherSkills(Supplier<Entity> pantherSupplier) {
        this.pantherSupplier = pantherSupplier;
    }

    /**
     * Регистрирует действие атаки на SPACE только один раз.
     */
    public void registerAttack() {
        InputActionManager.registerAttack(pantherSupplier, KeyCode.SPACE);
    }
}
