package org.example.deadknight.player.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Менеджер для регистрации пользовательских действий в игре.
 * <p>
 * Позволяет:
 * <ul>
 *     <li>Регистрировать действия один раз, чтобы избежать дублирования</li>
 *     <li>Очищать все зарегистрированные действия</li>
 *     <li>Универсально регистрировать атаку для любого персонажа</li>
 * </ul>
 */
public class InputActionManager {

    private static final Set<String> registeredActions = new HashSet<>();

    /**
     * Регистрирует действие один раз по имени и клавише.
     * Если действие с таким именем уже зарегистрировано, новая регистрация игнорируется.
     *
     * @param actionName имя действия
     * @param action     действие для регистрации
     * @param key        клавиша для триггера действия
     */
    public static void registerActionOnce(String actionName, UserAction action, KeyCode key) {
        if (!registeredActions.contains(actionName)) {
            FXGL.getInput().addAction(action, key);
            registeredActions.add(actionName);
        }
    }

    /**
     * Очищает все зарегистрированные действия и сбрасывает внутренный список.
     */
    public static void clearAll() {
        FXGL.getInput().clearAll();
        registeredActions.clear();
    }

    /**
     * Универсальный метод для регистрации атаки для любого персонажа.
     * <p>
     * Действие будет выполняться при нажатии указанной клавиши, если сущность доступна в мире.
     *
     * @param entitySupplier поставщик сущности персонажа
     * @param key            клавиша для триггера атаки
     */
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
