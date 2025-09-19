package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.KnightController;
import org.example.deadknight.gameplay.actors.player.controllers.PantherController;

import java.util.function.Supplier;

/**
 * Сервис для инициализации ввода игрока.
 * <p>
 * Делегирует настройку управления соответствующему контроллеру
 * в зависимости от выбранного типа персонажа.
 * <ul>
 *     <li>{@code knight} → {@link KnightController}</li>
 *     <li>{@code panther} → {@link PantherController}</li>
 * </ul>
 */
public class PlayerInputService {

    /**
     * Настраивает ввод для игрока указанного типа.
     *
     * @param characterType тип персонажа ("knight" или "panther")
     * @param playerSupplier ленивый поставщик сущности игрока
     */
    public static void initInput(String characterType, Supplier<Entity> playerSupplier) {
        switch (characterType) {
            case "knight" -> KnightController.initInput(playerSupplier);
            case "panther" -> PantherController.initInput(playerSupplier);
        }
    }
}
