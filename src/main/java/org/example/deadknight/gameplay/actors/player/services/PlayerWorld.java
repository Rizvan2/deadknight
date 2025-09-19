package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.gameplay.actors.player.controllers.MovementController;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

/**
 * Контейнер, объединяющий основные элементы игрового мира,
 * относящиеся к игроку.
 *
 * @param player             сущность игрока
 * @param mapChunkService    сервис управления чанками карты
 * @param movementController контроллер движения игрока
 */
public record PlayerWorld(Entity player, MapChunkService mapChunkService, MovementController movementController) {}
