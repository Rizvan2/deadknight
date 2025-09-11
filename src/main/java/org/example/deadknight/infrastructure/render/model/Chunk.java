package org.example.deadknight.infrastructure.render.model;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.example.deadknight.infrastructure.generation.BattlefieldBackgroundGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет один чанк карты игрового мира.
 * <p>
 * Чанк — это прямоугольный участок карты фиксированного размера в тайлах.
 * Он хранит:
 * <ul>
 *     <li>координаты в сетке чанков;</li>
 *     <li>список {@link Entity}, связанных с этим чанком;</li>
 *     <li>слои для визуализации, например землю и деревья.</li>
 * </ul>
 * <p>
 * Предназначен для динамической подгрузки и выгрузки участков карты
 * в зависимости от положения игрока.
 */
@Getter
@Setter
public class Chunk {

    /**
     * Координаты чанка в сетке чанков.
     * <p>
     * cx и cy определяют положение чанка по оси X и Y в "карте чанков".
     * Используется для расчета мировых координат и управления видимостью.
     */
    private final Point2D coords;

    /**
     * Размер чанка в тайлах.
     * <p>
     * Определяет, сколько тайлов помещается по ширине и высоте в одном чанке.
     * Используется при генерации визуального слоя и вычислении позиции чанка в мире.
     */
    private final int size;

    /**
     * Список игровых сущностей ({@link Entity}), привязанных к этому чанку.
     * <p>
     * Включает объекты, которые должны быть подгружены вместе с чанкoм
     * и выгружены при удалении чанка из мира.
     */
    private final List<Entity> entities = new ArrayList<>();

    /**
     * Слой земли чанка ({@link ImageView}).
     * <p>
     * Хранит визуальное представление "фона" для этого чанка.
     */
    private ImageView groundLayer;

    /**
     * Слой деревьев или объектов чанка ({@link ImageView}).
     * <p>
     * Позволяет рисовать объекты поверх земли, например деревья, кусты или строения.
     */
    private ImageView treeLayer;

    /**
     * Создаёт новый чанк.
     *
     * @param cx   координата X в сетке чанков
     * @param cy   координата Y в сетке чанков
     * @param size размер чанка в тайлах
     */
    public Chunk(int cx, int cy, int size) {
        this.coords = new Point2D(cx, cy);
        this.size = size;
    }

    /**
     * Добавляет сущность в список чанка.
     *
     * @param entity игровая сущность
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Удаляет все сущности чанка из игрового мира и очищает список.
     */
    public void unload() {
        for (Entity e : entities) {
            e.removeFromWorld();
        }
        entities.clear();
    }

    /**
     * @return мировая координата X (в пикселях) начала чанка
     */
    public int getWorldX() {
        return (int) (coords.getX() * size * BattlefieldBackgroundGenerator.tileSize);
    }

    /**
     * @return мировая координата Y (в пикселях) начала чанка
     */
    public int getWorldY() {
        return (int) (coords.getY() * size * BattlefieldBackgroundGenerator.tileSize);
    }
}
