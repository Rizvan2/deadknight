package org.example.deadknight.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class InputActionManager {

    private static final Set<String> registeredActions = new HashSet<>();

    public static void registerActionOnce(String actionName, UserAction action, KeyCode key) {
        if (!registeredActions.contains(actionName)) {
            FXGL.getInput().addAction(action, key);
            registeredActions.add(actionName);
        }
    }

    public static void clearAll() {
        FXGL.getInput().clearAll();
        registeredActions.clear();
    }

    /** Универсальный метод для регистрации атаки для любого персонажа */
    public static void registerAttack(Supplier<Entity> entitySupplier, KeyCode key) {
        String actionName = "Attack";
        registerActionOnce(actionName, new UserAction(actionName) {
            @Override
            protected void onActionBegin() {
                Entity e = entitySupplier.get();
                if (e == null || e.getWorld() == null) return;

                Runnable playAttack = (Runnable) e.getProperties().getValueOptional("playAttack").orElse(null);
                if (playAttack != null) playAttack.run();
            }
        }, key);
    }
}
