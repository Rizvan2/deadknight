package org.example.deadknight.infrastructure.dto;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.infrastructure.render.services.MapChunkService;

/**
 * Data transfer object (DTO), содержащий основные данные игрового мира.
 * <p>
 * Используется для хранения ссылки на игрока, сервис управления чанками карты,
 * а также размеры игровой карты.
 * </p>
 *
 * @param player         сущность игрока в игровом мире
 * @param mapChunkService сервис, отвечающий за подгрузку и управление чанками карты
 * @param mapWidth        ширина карты в игровых единицах
 * @param mapHeight       высота карты в игровых единицах
 */
public record GameWorldData(Entity player, MapChunkService mapChunkService, double mapWidth, double mapHeight) {
}
