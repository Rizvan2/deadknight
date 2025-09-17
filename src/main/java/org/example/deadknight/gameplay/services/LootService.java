package org.example.deadknight.gameplay.services;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import java.util.Random;

/**
 * Сервис для управления дропом лута после смерти мобов.
 * <p>
 * Определяет, какие предметы могут выпадать у мобов разных грейдов
 * и с какой вероятностью.
 */
public class LootService {

    /** Генератор случайных чисел для расчета вероятности дропа. */
    private final Random random = new Random();

    /**
     * Пытается бросить предметы с заданного моба.
     * <p>
     * В зависимости от грейда моба, вызываются попытки дропа с различной вероятностью.
     *
     * @param mobGrade грейд моба, например "goblin_basic", "goblin_elite"
     * @param pos позиция, где должен появиться дроп
     */
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

    /**
     * Пытается создать дроп с определенной вероятностью.
     *
     * @param entityName имя сущности, которая будет заспавнена (например "healthEssence")
     * @param chance вероятность дропа (0.0 — никогда, 1.0 — всегда)
     * @param pos позиция, где будет заспавнен предмет
     */
    private void tryDrop(String entityName, double chance, Point2D pos) {
        if (random.nextDouble() < chance) {
            FXGL.spawn(entityName, pos.getX(), pos.getY());
            System.out.println("[LootService] Dropped " + entityName + " at " + pos);
        }
    }
}
