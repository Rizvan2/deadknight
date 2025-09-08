package org.example.deadknight.infrastructure.dto;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.infrastructure.services.MapChunkService;

public class GameWorldData {
    private final Entity player;
    private final MapChunkService mapChunkService;
    private final double mapWidth;
    private final double mapHeight;

    public GameWorldData(Entity player, MapChunkService mapChunkService, double mapWidth, double mapHeight) {
        this.player = player;
        this.mapChunkService = mapChunkService;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public Entity getPlayer() {
        return player;
    }

    public MapChunkService getMapChunkService() {
        return mapChunkService;
    }

    public double getMapWidth() {
        return mapWidth;
    }

    public double getMapHeight() {
        return mapHeight;
    }
}
