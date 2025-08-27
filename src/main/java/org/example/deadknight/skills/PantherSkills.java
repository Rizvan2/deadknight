package org.example.deadknight.skills;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

import java.util.function.Supplier;

/**
 * Класс для регистрации и управления навыками пантеры.
 * <p>
 * Основная задача — связывать действия игрока с функционалом персонажа (например, атаку).
 */
public class PantherSkills {

    private final Supplier<Entity> pantherSupplier;

    /**
     * Конструктор класса навыков пантеры.
     *
     * @param pantherSupplier поставщик сущности пантеры (например, через лямбду или метод GameInitializer)
     */
    public PantherSkills(Supplier<Entity> pantherSupplier) {
        this.pantherSupplier = pantherSupplier;
    }

    /**
     * Регистрирует действие атаки на нажатие клавиши SPACE.
     * <p>
     * При начале действия проверяется, существует ли пантерa,
     * и не находится ли она уже в состоянии атаки.
     * Если всё в порядке, вызывается сохранённый Runnable "playAttack" из свойств сущности.
     */
    public void registerAttack() {
        FXGL.getInput().addAction(new UserAction("PantherAttack") {
            @Override
            protected void onActionBegin() {
                Entity panther = pantherSupplier.get();
                if (panther == null || panther.getWorld() == null) return;

                Runnable playAttack = (Runnable) panther.getProperties()
                        .getValueOptional("playAttack")
                        .orElse(null);

                if (playAttack != null) {
                    Boolean isAttacking = panther.getProperties().getBoolean("isAttacking");
                    if (isAttacking == null || !isAttacking) {
                        playAttack.run();
                    }
                }
            }
        }, KeyCode.SPACE);
    }
}
