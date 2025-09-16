package org.example.deadknight.gameplay.services;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import java.util.Random;

public class LootService {

    private final Random random = new Random();

    public void dropLoot(String mobGrade, Point2D pos) {
        switch (mobGrade) {
            case "goblin_basic" -> {
                tryDrop("healthEssence", 0.5, pos);
                tryDrop("upgradeEssence", 0.3, pos);
            }
            case "goblin_elite" -> {
                tryDrop("healthEssence", 0.7, pos);
                tryDrop("upgradeEssence", 0.6, pos);
            }
            // другие моб-грейды
        }
    }

    private void tryDrop(String entityName, double chance, Point2D pos) {
        if (random.nextDouble() < chance) {
            FXGL.spawn(entityName, pos.getX(), pos.getY());
            System.out.println("[LootService] Dropped " + entityName + " at " + pos);
        }
    }
}
