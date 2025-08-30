package org.example.deadknight.player.controllers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import lombok.Setter;
import org.example.deadknight.player.components.SpeedComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Универсальный контроллер движения сущности по клавишам WASD (или любым другим клавишам).
 * <p>
 * Особенности:
 * <ul>
 *     <li>Поддержка нескольких одновременных нажатий клавиш.</li>
 *     <li>Автоматическое обновление направления спрайта для LEFT/RIGHT.</li>
 *     <li>Управление свойством {@code "moving"} сущности.</li>
 * </ul>
 */
public class WASDController {

    /** Список текущих нажатых направлений */
    private static final Set<String> pressedKeys = new HashSet<>();

    /** Зарегистрированные действия для клавиш, чтобы не добавлять их повторно */
    private static final Set<String> addedActions = new HashSet<>();

    /** Текущее значение TPF (time per frame) для перемещения */
    @Setter
    private static double currentTpf = 0;

    /**
     * Инициализация управления движением для сущности.
     *
     * @param entitySupplier поставщик сущности
     * @param movementMap    карта клавиша → направление ("UP", "DOWN", "LEFT", "RIGHT")
     */
    public static void initInput(Supplier<Entity> entitySupplier, Map<KeyCode, String> movementMap) {
        FXGL.getInput().clearAll(); // очищаем старые действия
        movementMap.forEach((key, direction) -> addKeyAction(entitySupplier, key, direction));
    }

    /**
     * Добавляет обработку одной клавиши движения.
     *
     * @param entitySupplier поставщик сущности
     * @param key            клавиша управления
     * @param direction      направление движения ("UP", "DOWN", "LEFT", "RIGHT")
     */
    private static void addKeyAction(Supplier<Entity> entitySupplier, KeyCode key, String direction) {
        String actionName = "Move " + direction;

        if (addedActions.contains(actionName)) return;
        addedActions.add(actionName);

        // Получаем смещение по X и Y
        int[] deltas = getDirectionDelta(direction);
        int dx = deltas[0];
        int dy = deltas[1];

        FXGL.getInput().addAction(new UserAction(actionName) {
            @Override
            protected void onActionBegin() { handleActionBegin(entitySupplier.get(), direction); }

            @Override
            protected void onAction() { handleAction(entitySupplier.get(), dx, dy); }

            @Override
            protected void onActionEnd() { handleActionEnd(entitySupplier.get(), direction); }
        }, key);
    }

    /**
     * Возвращает смещение по X и Y для направления движения.
     *
     * @param direction направление ("UP", "DOWN", "LEFT", "RIGHT")
     * @return массив [dx, dy]
     */
    private static int[] getDirectionDelta(String direction) {
        return switch (direction) {
            case "RIGHT" -> new int[]{5, 0};
            case "LEFT"  -> new int[]{-5, 0};
            case "UP"    -> new int[]{0, -5};
            case "DOWN"  -> new int[]{0, 5};
            default -> new int[]{0, 0};
        };
    }

    /**
     * Обрабатывает начало нажатия клавиши.
     *
     * @param e         сущность
     * @param direction направление
     */
    private static void handleActionBegin(Entity e, String direction) {
        if (e == null || e.getWorld() == null) return;

        pressedKeys.add(direction);
        e.getProperties().setValue("moving", true);
        e.getProperties().setValue("direction", direction);

        if ("LEFT".equals(direction) || "RIGHT".equals(direction)) {
            e.getProperties().setValue("spriteDir", direction);
        }
    }

    /**
     * Обрабатывает удержание клавиши.
     *
     * @param e  сущность
     * @param dx смещение по X
     * @param dy смещение по Y
     */
    private static void handleAction(Entity e, int dx, int dy) {
        if (e == null || e.getWorld() == null) return;
        if (!e.hasComponent(SpeedComponent.class)) return;

        double speed = e.getComponent(SpeedComponent.class).getSpeed();
        e.translateX(dx * speed * currentTpf);
        e.translateY(dy * speed * currentTpf);
    }

    /**
     * Обрабатывает отпускание клавиши.
     *
     * @param e         сущность
     * @param direction направление
     */
    private static void handleActionEnd(Entity e, String direction) {
        if (e == null) return;

        pressedKeys.remove(direction);

        if (!pressedKeys.isEmpty()) {
            String nextDir = pressedKeys.iterator().next();
            e.getProperties().setValue("direction", nextDir);

            if ("LEFT".equals(nextDir) || "RIGHT".equals(nextDir)) {
                e.getProperties().setValue("spriteDir", nextDir);
            }

            e.getProperties().setValue("moving", true);
        } else {
            e.getProperties().setValue("moving", false);
        }
    }
}
