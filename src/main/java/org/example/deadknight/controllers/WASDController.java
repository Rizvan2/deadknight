package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.example.deadknight.components.SpeedComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

/**
 * Универсальный контроллер движения по WASD.
 * Работает как старая логика KnightController.
 */
public class WASDController {

    private static final Set<String> pressedKeys = new HashSet<>();
    private static double currentTpf = 0;

    public static void setCurrentTpf(double tpf) {
        currentTpf = tpf;
    }

    /**
     * Инициализация управления движением для сущности.
     *
     * @param entitySupplier поставщик сущности
     * @param movementMap    карта клавиша → направление ("UP", "DOWN", "LEFT", "RIGHT")
     */
    public static void initInput(Supplier<Entity> entitySupplier, Map<KeyCode, String> movementMap) {
        movementMap.forEach((key, direction) -> {
            // Фиксированные смещения dx/dy как в старой логике
            int dx = 0, dy = 0;
            switch (direction) {
                case "RIGHT" -> dx = 5;
                case "LEFT" -> dx = -5;
                case "UP" -> dy = -5;
                case "DOWN" -> dy = 5;
            }
            addKeyAction(entitySupplier, key, direction, dx, dy);
        });
    }

    private static void addKeyAction(Supplier<Entity> entitySupplier,
                                     KeyCode key,
                                     String direction,
                                     int dx,
                                     int dy) {

        final int finalDx = dx;
        final int finalDy = dy;

        getInput().addAction(new UserAction("Move " + direction) {

            @Override
            protected void onActionBegin() {
                Entity e = entitySupplier.get();
                if (e == null || e.getWorld() == null) return; // проверяем, что сущность существует и находится в мире

                pressedKeys.add(direction);
                e.getProperties().setValue("moving", true);
                e.getProperties().setValue("direction", direction);

                // Горизонтальная анимация
                if ("LEFT".equals(direction) || "RIGHT".equals(direction)) {
                    e.getProperties().setValue("spriteDir", direction);
                }
            }

            @Override
            protected void onAction() {
                Entity e = entitySupplier.get();
                if (e == null || e.getWorld() == null) return; // проверяем, что сущность в мире
                if (!e.hasComponent(SpeedComponent.class)) return; // проверяем, что есть SpeedComponent

                double speed = e.getComponent(SpeedComponent.class).getSpeed();
                e.translateX(finalDx * speed * currentTpf);
                e.translateY(finalDy * speed * currentTpf);
            }


            @Override
            protected void onActionEnd() {
                Entity e = entitySupplier.get();
                if (e != null) {
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
        }, key);
    }
}
