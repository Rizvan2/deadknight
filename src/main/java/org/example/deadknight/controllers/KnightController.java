package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.example.deadknight.components.SpeedComponent;
import org.example.deadknight.services.WaveService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.getInput;

/**
 * Контроллер для управления рыцарем.
 * <p>
 * Отвечает за обработку ввода клавиш для движения и стрельбы.
 * Держит актуальное состояние нажатых клавиш и обновляет свойства сущности рыцаря.
 */
public class KnightController {

    /** Множество текущих нажатых направлений. */
    private static final Set<String> pressedKeys = new HashSet<>();

    /** Текущее значение tpf (time per frame), обновляется из GameApplication.onUpdate(). */
    private static double currentTpf = 0;

    /**
     * Устанавливает текущее значение tpf.
     * <p>
     * Используется для расчёта смещения при движении персонажа.
     *
     * @param tpf время кадра в секундах
     */
    public static void setCurrentTpf(double tpf) {
        currentTpf = tpf;
    }

    /**
     * Инициализирует обработку ввода для рыцаря.
     * <p>
     * Добавляет действия движения по WASD/стрелкам и стрельбы волной по пробелу.
     *
     * @param knightSupplier поставщик сущности рыцаря
     */
    public static void initInput(Supplier<Entity> knightSupplier) {
        // Подключаем универсальный W/A/S/D контроллер движения
        Map<KeyCode, String> movementMap = Map.of(
                KeyCode.D, "RIGHT",
                KeyCode.A, "LEFT",
                KeyCode.W, "UP",
                KeyCode.S, "DOWN"
        );

        WASDController.setCurrentTpf(1); // Изначально 0, потом обновляется каждый кадр
        WASDController.initInput(knightSupplier, movementMap);
        // Кастомное действие рыцаря — стрельба волной
        getInput().addAction(new UserAction("Shoot Wave") {
            @Override
            protected void onActionBegin() {
                Entity knight = knightSupplier.get();
                if (knight == null || knight.getWorld() == null) return; // проверка существования
                WaveService.shoot(knight);
            }
        }, KeyCode.SPACE);

    }

    /**
     * Вспомогательный метод для добавления действий движения.
     * <p>
     * Обновляет состояние "moving", направление и spriteDir в свойствах сущности.
     *
     * @param knightSupplier поставщик сущности рыцаря
     * @param key            клавиша для движения
     * @param direction      направление движения ("UP", "DOWN", "LEFT", "RIGHT")
     * @param dx             смещение по X
     * @param dy             смещение по Y
     */
    private static void addKeyAction(Supplier<Entity> knightSupplier,
                                     KeyCode key, String direction,
                                     int dx, int dy) {
        getInput().addAction(new UserAction("Move " + direction) {

            @Override
            protected void onActionBegin() {
                Entity k = knightSupplier.get();
                if (k != null) {
                    pressedKeys.add(direction);
                    k.getProperties().setValue("moving", true);
                    k.getProperties().setValue("direction", direction);
                    k.getProperties().setValue("shootDir", direction);

                    // Обновляем только горизонтальное направление для анимации
                    if ("LEFT".equals(direction) || "RIGHT".equals(direction)) {
                        k.getProperties().setValue("spriteDir", direction);
                    }
                }
            }

            @Override
            protected void onAction() {
                Entity k = knightSupplier.get();
                if (k != null) {
                    double speed = k.getComponent(SpeedComponent.class).getSpeed();
                    k.translateX(dx * speed * currentTpf);
                    k.translateY(dy * speed * currentTpf);
                }
            }

            @Override
            protected void onActionEnd() {
                Entity k = knightSupplier.get();
                if (k != null) {
                    pressedKeys.remove(direction);

                    if (!pressedKeys.isEmpty()) {
                        String nextDir = pressedKeys.iterator().next();
                        k.getProperties().setValue("direction", nextDir);
                        k.getProperties().setValue("shootDir", nextDir);

                        if ("LEFT".equals(nextDir) || "RIGHT".equals(nextDir)) {
                            k.getProperties().setValue("spriteDir", nextDir);
                        }

                        k.getProperties().setValue("moving", true);
                    } else {
                        k.getProperties().setValue("moving", false);
                    }
                }
            }
        }, key);
    }
}
