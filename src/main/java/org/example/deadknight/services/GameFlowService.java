package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import org.example.deadknight.services.init.LoadingScreenSubScene;
import org.example.deadknight.services.ui.CharacterSelectScreen;

import java.util.function.Consumer;

/**
 * Сервис для управления игровым потоком на уровне выбора персонажа и загрузочного экрана.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Показ экрана выбора персонажа</li>
 *     <li>Очистку старых UI-нодов и сущностей перед началом игры</li>
 *     <li>Показ и скрытие загрузочного экрана с прогрузкой ресурсов</li>
 * </ul>
 */
public class GameFlowService {

    /**
     * Запускает процесс выбора персонажа.
     * <p>
     * После выбора персонажа:
     * <ol>
     *     <li>Очищается старый UI и сущности игрового мира</li>
     *     <li>Добавляется загрузочный экран {@link LoadingScreenSubScene}</li>
     *     <li>Прогружаются ресурсы, после чего загрузочный экран убирается</li>
     *     <li>Вызывается коллбек {@code onCharacterSelected} с выбранным типом персонажа</li>
     * </ol>
     *
     * @param onCharacterSelected коллбек, который принимает выбранный тип персонажа
     */
    public void startCharacterSelection(Consumer<String> onCharacterSelected) {
        CharacterSelectScreen.show(characterType -> {

            // Чистим старый UI и сущности до загрузки нового контента
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameWorld().removeEntities(FXGL.getGameWorld().getEntitiesCopy());

            // Добавляем лоадер
            LoadingScreenSubScene loadingScreen = new LoadingScreenSubScene(
                    FXGL.getAppWidth(),
                    FXGL.getAppHeight()
            );
            FXGL.getGameScene().addUINode(loadingScreen);

            loadingScreen.loadTextures(() -> {
                // Убираем лоадер после загрузки
                FXGL.getGameScene().removeUINode(loadingScreen);
                onCharacterSelected.accept(characterType);
            });
        });
    }

}
