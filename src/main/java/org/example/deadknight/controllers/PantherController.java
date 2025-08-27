package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.input.KeyCode;
import org.example.deadknight.skills.PantherSkills;

/**
 * Контроллер для персонажа "Пантера".
 * <p>
 * Отвечает за настройку управления движением и атаками пантеры.
 * Использует {@link WASDController} для обработки клавиш W/A/S/D
 * и {@link PantherSkills} для регистрации атак.
 */
public class PantherController {

    /**
     * Инициализирует управление пантерой.
     * <p>
     * Настраивает клавиши движения (W/A/S/D), подключает пантерские навыки и регистрирует их.
     *
     * @param pantherSupplier поставщик сущности пантеры ({@link Entity}), для которой будет настроено управление
     */
    public static void initInput(Supplier<Entity> pantherSupplier) {
        Map<KeyCode, String> movementMap = Map.of(
                KeyCode.D, "RIGHT",
                KeyCode.A, "LEFT",
                KeyCode.W, "UP",
                KeyCode.S, "DOWN"
        );

        WASDController.setCurrentTpf(1);
        WASDController.initInput(pantherSupplier, movementMap);

        PantherSkills pantherSkills = new PantherSkills(pantherSupplier);
        pantherSkills.registerAttack();
    }
}
