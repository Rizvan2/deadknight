package org.example.deadknight.services.init;

import javafx.geometry.Point2D;
import org.example.deadknight.infrastructure.services.MapService;

public class MapInitializer {

    // Размеры карты для разных типов миров
    public static final int SMALL_WORLD_WIDTH = 16;
    public static final int SMALL_WORLD_HEIGHT = 16;

    public static final int MEDIUM_WORLD_WIDTH = 32;
    public static final int MEDIUM_WORLD_HEIGHT = 32;

    public static final int LARGE_WORLD_WIDTH = 64;
    public static final int LARGE_WORLD_HEIGHT = 64;

    /**
     * Генерация маленького мира.
     *
     * @param mapName имя карты
     * @return размеры карты (ширина, высота)
     */
    public static Point2D generateSmallWorld(String mapName) {
        return MapService.generateBattlefieldLayers(mapName, SMALL_WORLD_WIDTH, SMALL_WORLD_HEIGHT);
    }

    /**
     * Генерация среднего мира.
     *
     * @param mapName имя карты
     * @return размеры карты (ширина, высота)
     */
    public static Point2D generateMediumWorld(String mapName) {
        return MapService.generateBattlefieldLayers(mapName, MEDIUM_WORLD_WIDTH, MEDIUM_WORLD_HEIGHT);
    }

    /**
     * Генерация большого мира.
     *
     * @param mapName имя карты
     * @return размеры карты (ширина, высота)
     * вот этот вот большой мир нам рано еще создавать у нас нет рендеринга и вся карта одновременна отображается
     * мы добавим прогрузку чанков чтобы грузилась карта только в зоне нашей видемости
     */
    public static Point2D generateLargeWorld(String mapName) {
        return MapService.generateBattlefieldLayers(mapName, LARGE_WORLD_WIDTH, LARGE_WORLD_HEIGHT);
    }
}
