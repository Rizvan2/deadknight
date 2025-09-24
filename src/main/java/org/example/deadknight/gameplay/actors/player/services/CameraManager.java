package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.services.CameraService;

/**
 * Менеджер камеры для игрока.
 * <p>
 * Отвечает за настройку камеры в игровом мире:
 * <ul>
 *     <li>Привязку камеры к игроку</li>
 *     <li>Ограничение движения камеры в пределах карты</li>
 *     <li>Настройку масштабирования (зум) с помощью {@link CameraService}</li>
 * </ul>
 * <p>
 * Использует {@link CameraService} для работы с камерой и зумом.
 */
public class CameraManager {

    /**
     * Сервис управления камерой и зумом.
     * <p>
     * Конфигурация по умолчанию:
     * <ul>
     *     <li>minZoom = 0 (максимальное приближение)</li>
     *     <li>maxZoom = 2.0 (максимальное отдаление)</li>
     *     <li>zoomFactor = 1.05 (скорость изменения зума при прокрутке колесика)</li>
     * </ul>
     */
    private final CameraService cameraService = new CameraService(0, 2.0, 1.05);

    /**
     * Привязывает камеру к игроку и ограничивает движение границами карты.
     * Также активирует обработку зума с помощью {@link CameraService#setupZoom()}.
     *
     * @param player   сущность игрока, к которой будет привязана камера
     * @param worldData данные игрового мира (ширина/высота карты и др.)
     */
    public void bindToPlayer(Entity player, GameWorldData worldData) {
        cameraService.bindToEntity(
                player,
                FXGL.getAppWidth(),
                FXGL.getAppHeight(),
                worldData.mapWidth(),
                worldData.mapHeight()
        );
        cameraService.setupZoom();
    }
}
