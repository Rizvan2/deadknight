package org.example.deadknight.infrastructure.factory;

import org.example.deadknight.gameplay.actors.player.services.PlayerService;
import org.example.deadknight.gameplay.actors.player.services.PlayerWorld;
import org.example.deadknight.gameplay.actors.player.services.WorldFactory;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.infrastructure.dto.GameWorldData;
import org.example.deadknight.infrastructure.render.services.MapChunkService;
import com.almasb.fxgl.entity.Entity;

public class GameWorldFactory {

    public static class GameWorldObjects {
        public final Entity player;
        public final PlayerService playerService;
        public final MapChunkService mapChunkService;

        public GameWorldObjects(Entity player, PlayerService playerService, MapChunkService mapChunkService) {
            this.player = player;
            this.playerService = playerService;
            this.mapChunkService = mapChunkService;
        }
    }

    public static GameWorldObjects create(GameWorldData worldData, UIService uiService) {
        // Создаём игрока через существующую фабрику
        PlayerWorld pw = WorldFactory.createPlayer(worldData);
        Entity player = pw.player();
        MapChunkService mapService = pw.mapChunkService();

        // Инициализация UI
        uiService.initUI(player);
        PlayerService playerService = new PlayerService(player, pw.movementController(), uiService.getPlayerUIService());

        return new GameWorldObjects(player, playerService, mapService);
    }
}
