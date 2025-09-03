package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Сервис для управления атакой пантеры.
 * <p>
 * Отвечает за анимацию атаки, рывок и последующее возвращение к обычному спрайту.
 */
public class PantherAttackService {

    /**
     * Выполняет атаку пантеры с анимацией и рывком.
     *
     * @param panther         объект пантеры
     * @param attackImage     путь к спрайту атаки
     * @param durationSeconds длительность атаки в секундах
     * @param dashDistance    дистанция рывка (пиксели)
     */
    public static void playAttack(Entity panther, String attackImage, double durationSeconds, double dashDistance) {
        if (Boolean.TRUE.equals(panther.getProperties().getBoolean("isAttacking"))) return;
        panther.getProperties().setValue("isAttacking", true);

        String spriteDir = panther.getProperties().getString("spriteDir");

        // Получаем текущий спрайт и устанавливаем его как кадр атаки
        ImageView attackSprite = AnimationService.getCurrentSprite(panther, 64, 64);
        attackSprite.setImage(FXGL.image(attackImage));
        AnimationService.setSprite(panther, attackSprite, spriteDir);

        // Рывок: перемещаем пантеру по X
        double dx = "RIGHT".equals(spriteDir) ? dashDistance : -dashDistance;
        FXGL.getGameTimer().runAtInterval(
                () -> panther.setX(panther.getX() + dx),
                Duration.seconds(0.02),
                (int) (10 * durationSeconds)
        );

        // Устанавливает задержку между атаками персонажа.
        AnimationService.setAttackCooldown(panther, durationSeconds);
    }
}
