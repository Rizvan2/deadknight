package org.example.deadknight.controllers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import org.example.deadknight.components.SpeedComponent;
import org.example.deadknight.skills.KnightSkills;

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

    /**
     * Инициализация обработки ввода для рыцаря.
     * <p>
     * Добавляет действия движения по клавишам W, A, S, D и атаку по пробелу.
     * Движение делегируется {@link WASDController}.
     * Атака выполняется через {@link Runnable} из свойства "playAttack" сущности рыцаря.
     *
     * @param knightSupplier поставщик сущности рыцаря
     */
    public static void initInput(Supplier<Entity> knightSupplier) {
        Map<KeyCode, String> movementMap = Map.of(
                KeyCode.D, "RIGHT",
                KeyCode.A, "LEFT",
                KeyCode.W, "UP",
                KeyCode.S, "DOWN"
        );

        WASDController.setCurrentTpf(1);
        WASDController.initInput(knightSupplier, movementMap);

        // Подключаем навыки рыцаря
        KnightSkills skills = new KnightSkills(knightSupplier);
        skills.registerAttack();
    }
}
