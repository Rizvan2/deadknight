package org.example.deadknight.gameplay.actors.essences.services;

import com.almasb.fxgl.entity.Entity;

/**
 * Сервис для обработки стандартной логики подбора эссенций.
 */
public class EssenceService {

    /**
     * Применяет эффект к игроку и удаляет эссенцию из мира.
     *
     * @param player  игрок
     * @param essence эссенция
     * @param effect  действие, которое применяется к игроку
     */
    public void handleEssence(Entity player, Entity essence, Runnable effect) {
        effect.run();          // применяем эффект
        essence.removeFromWorld(); // удаляем эссенцию
    }
}
