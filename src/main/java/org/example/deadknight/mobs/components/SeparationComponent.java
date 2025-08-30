package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import org.example.deadknight.mobs.entities.types.EntityType;

/**
 * Компонент, реализующий "разделение" (Separation) для сущностей одного типа.
 * <p>
 * Используется, чтобы гоблины (или другие мобсы) не наслаивались друг на друга.
 * <p>
 * Каждое обновление {@link #onUpdate(double)} проверяет расстояние до всех других сущностей
 * указанного типа и отталкивает их друг от друга, если они находятся ближе {@code minDistance}.
 *
 * <p>Параметры:
 * <ul>
 *     <li>{@code minDistance} — минимальное расстояние между сущностями, меньше которого происходит отталкивание.</li>
 *     <li>{@code pushFactor} — коэффициент силы отталкивания; увеличивая его, сущности будут сильнее расходиться.</li>
 * </ul>
 *
 * <p>SRP (Single Responsibility Principle):
 * Этот компонент отвечает только за логику разделения сущностей и не вмешивается в движение к игроку,
 * атаки или анимации.
 */
public class SeparationComponent extends Component {
    private final double minDistance;
    private final double pushFactor;

    /**
     * Создаёт компонент Separation.
     *
     * @param minDistance минимальное расстояние между сущностями
     * @param pushFactor  коэффициент силы отталкивания
     */
    public SeparationComponent(double minDistance, double pushFactor) {
        this.minDistance = minDistance;
        this.pushFactor = pushFactor;
    }

    /**
     * Вызывается каждый кадр.
     * <p>
     * Проверяет все сущности типа {@link EntityType#HOSTILE_MOB} и отталкивает их
     * друг от друга, если расстояние между ними меньше {@code minDistance}.
     *
     * @param tpf время между кадрами (time per frame)
     */
    @Override
    public void onUpdate(double tpf) {
        FXGL.getGameWorld().getEntitiesByType(EntityType.HOSTILE_MOB)
                .stream()
                .filter(e -> e != entity)
                .forEach(e -> {
                    Point2D diff = entity.getCenter().subtract(e.getCenter());
                    double dist = diff.magnitude();
                    if (dist < minDistance && dist > 0) {
                        entity.translate(diff.normalize().multiply((minDistance - dist) * tpf * pushFactor));
                    }
                });
    }
}
