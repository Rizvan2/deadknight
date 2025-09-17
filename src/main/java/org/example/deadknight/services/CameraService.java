package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

/**
 * Сервис для управления камерой и зумом.
 * <p>
 * Позволяет:
 * <ul>
 *     <li>Привязывать камеру к сущности (игроку)</li>
 *     <li>Ограничивать движение камеры в пределах карты</li>
 *     <li>Настраивать масштабирование сцены с помощью колесика мыши</li>
 * </ul>
 */
public class CameraService {

    private final double minZoom;
    private final double maxZoom;
    private final double zoomFactor;

    public CameraService(double minZoom, double maxZoom, double zoomFactor) {
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.zoomFactor = zoomFactor;
    }

    /**
     * Привязывает камеру к игроку и задает границы движения.
     *
     * @param player сущность игрока
     * @param viewportWidth ширина видимой области
     * @param viewportHeight высота видимой области
     * @param worldWidth ширина мира (карты)
     * @param worldHeight высота мира (карты)
     */
    public void bindToEntity(Entity player,
                             double viewportWidth,
                             double viewportHeight,
                             double worldWidth,
                             double worldHeight) {
        FXGL.getGameScene().getViewport().setZoom(1.3);
        var viewport = FXGL.getGameScene().getViewport();
        viewport.bindToEntity(player, viewportWidth / 2.0, viewportHeight / 2.0);
        viewport.setBounds(0, 0, (int) worldWidth, (int) worldHeight);
    }

    public void bindToEntity(Entity player) {
        bindToEntity(
                player,
                FXGL.getAppWidth(),
                FXGL.getAppHeight(),
                FXGL.getAppWidth(),   // если карта не задана, временно делаем равной ширине экрана
                FXGL.getAppHeight()   // то же для высоты
        );
    }

    /**
     * Настраивает масштабирование сцены с помощью колёсика мыши с ограничением.
     */
    public void setupZoom() {
        var scene = FXGL.getGameScene();
        var viewport = scene.getViewport();

        scene.getContentRoot().setOnScroll(e -> {
            double newZoom = viewport.getZoom();

            if (e.getDeltaY() > 0) {
                newZoom *= zoomFactor;
            } else {
                newZoom /= zoomFactor;
            }

            // Ограничиваем минимальный и максимальный зум
            newZoom = Math.max(minZoom, Math.min(maxZoom, newZoom));
            viewport.setZoom(newZoom);
        });
    }
}
