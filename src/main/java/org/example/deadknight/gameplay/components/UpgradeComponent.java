package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import lombok.Getter;
import lombok.Setter;

/**
 * Компонент для хранения количества собранных апгрейд-эссенций.
 */
@Getter
@Setter
public class UpgradeComponent extends Component {

    private int upgradeEssenceCount = 0;

    /** Получить текущее количество собранных эссенций */
    public int getCount() {
        return upgradeEssenceCount;
    }

    /** Добавить одну эссенцию */
    public void increment() {
        upgradeEssenceCount++;
        FXGL.set("upgradeEssences", upgradeEssenceCount);
    }
    /** Сбросить счётчик */
    public void reset() {
        upgradeEssenceCount = 0;
    }
}
