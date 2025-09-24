package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.gameplay.components.SpeedComponent;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

/**
 * Фабрика для создания объектов игрового мира, связанных с игроком.
 * <p>
 * Отвечает за сборку {@link PlayerWorld}, объединяя сущность игрока,
 * сервис управления чанками карты и контроллер движения.
 */
public class WorldFactory {

    /**
     * Создаёт структуру {@link PlayerWorld} из данных игрового мира.
     *
     * @param worldData данные игрового мира, включая игрока и карту
     * @return контейнер {@link PlayerWorld} с игроком, картой и контроллером движения
     */
    public static PlayerWorld createPlayer(GameWorldData worldData) {
        Entity player = worldData.player();
        MapChunkService mapService = worldData.mapChunkService();
        MovementController movement = new MovementController(
            (HasSpeed) player.getComponent(SpeedComponent.class),
            player
        );
        return new PlayerWorld(player, mapService, movement);
    }
}
