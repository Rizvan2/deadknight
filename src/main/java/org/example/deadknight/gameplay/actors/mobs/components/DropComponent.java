package org.example.deadknight.gameplay.actors.mobs.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import lombok.Getter;
import org.example.deadknight.gameplay.services.LootService;

/**
 * Компонент для дропа лута при смерти моба.
 */
@Getter
public class DropComponent extends Component {

    private final String mobGrade;
    private final LootService lootService;

    public DropComponent(String mobGrade, LootService lootService) {
        this.mobGrade = mobGrade;
        this.lootService = lootService;
    }

    /** Вызывает дроп лута в позиции сущности */
    public void dropLoot(Point2D pos) {
        lootService.dropLoot(mobGrade, pos);
    }
}
