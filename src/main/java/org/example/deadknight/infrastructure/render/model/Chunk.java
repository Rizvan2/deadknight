package org.example.deadknight.infrastructure.render.model;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Класс, представляющий чанк карты с Canvas для рендеринга.
 *
 * <p>Чанк хранит свои координаты на карте, размер в тайлах,
 * канвас для отрисовки тайлов и FXGL Entity для отображения в мире.
 *
 * <p>Позволяет:
 * <ul>
 *     <li>Присоединять и отсоединять чанк из FXGL мира</li>
 *     <li>Хранить канвас и графический контекст для рендеринга тайлов</li>
 *     <li>Полностью уничтожать чанк при выгрузке</li>
 * </ul>
 */
@Getter
@Setter
public class Chunk {

    /** Координаты чанка на карте (в чанках, а не в пикселях) */
    private final Point2D coords;

    /** Размер чанка в тайлах */
    private final int size;

    /** Canvas для отрисовки тайлов */
    private final Canvas canvas;

    /** Графический контекст Canvas */
    private final GraphicsContext gc;

    /** FXGL-сущность, представляющая чанк в мире */
    private Entity entity;

    /**
     * Создает чанк с заданными координатами и размером.
     * Canvas создается с размером size * tileSize пикселей.
     *
     * @param cx координата X чанка
     * @param cy координата Y чанка
     * @param size размер чанка в тайлах
     */
    public Chunk(int cx, int cy, int size) {
        this.coords = new Point2D(cx, cy);
        this.size = size;

        int pixelSize = size * BattlefieldBackgroundGenerator.tileSize;
        this.canvas = new Canvas(pixelSize, pixelSize);
        this.gc = canvas.getGraphicsContext2D();
    }

    /**
     * Устанавливает FXGL Entity для чанка.
     *
     * @param entity FXGL-сущность, привязанная к чанку
     */
    public void addEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Возвращает координату X чанка в мировых пикселях.
     */
    public int getWorldX() {
        return (int) (coords.getX() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    /**
     * Возвращает координату Y чанка в мировых пикселях.
     */
    public int getWorldY() {
        return (int) (coords.getY() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    /**
     * Прикрепляет Canvas как Entity в FXGL-мир.
     * Если Entity уже активна, повторно не создается.
     */
    public void attach() {
        if (entity == null || !entity.isActive()) {
            entity = com.almasb.fxgl.dsl.FXGL.entityBuilder()
                    .at(getWorldX(), getWorldY())
                    .view(canvas)
                    .zIndex(-100)
                    .buildAndAttach();
        }
    }

    /**
     * Убирает чанк с карты (FXGL мира), но не уничтожает его.
     * Canvas и графический контекст остаются доступными для кеша.
     */
    public void detach() {
        if (entity != null && entity.isActive()) {
            entity.removeFromWorld();
        }
    }

    /**
     * Полностью уничтожает чанк:
     * <ul>
     *     <li>Удаляет FXGL Entity из мира</li>
     *     <li>Сбрасывает ссылку на сущность</li>
     *     <li>Canvas и GC остаются доступными, но могут быть собраны сборщиком мусора</li>
     * </ul>
     */
    public void unload() {
        detach();
        entity = null;
    }
}
