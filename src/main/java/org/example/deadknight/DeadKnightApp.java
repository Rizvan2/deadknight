package org.example.deadknight;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.KeyCode;
import org.example.deadknight.config.GameConfig;
import org.example.deadknight.gameplay.actors.essences.systems.EssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.systems.UpgradeEssenceCollisionInitializer;
import org.example.deadknight.gameplay.actors.essences.factory.EssenceFactory;
import org.example.deadknight.gameplay.actors.mobs.factories.GoblinFactory;
import org.example.deadknight.gameplay.actors.player.services.ui.UIService;
import org.example.deadknight.services.GameFlowService;
import org.example.deadknight.services.GameInitializerService;
import org.example.deadknight.services.GameWorldManager;
import org.example.deadknight.services.debug.DebugOverlayService;
import org.example.deadknight.services.init.SettingsInitializer;

/**
 * Главный класс приложения DeadKnight.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Настройку параметров игры через {@link SettingsInitializer}</li>
 *     <li>Инициализацию игрового мира, фабрик сущностей и менеджеров</li>
 *     <li>Выбор персонажа и запуск игрового процесса через {@link GameFlowService}</li>
 *     <li>Настройку отладочных инструментов через {@link DebugOverlayService}</li>
 *     <li>Инициализацию физических взаимодействий (коллизий) для сущностей</li>
 *     <li>Обновление состояния мира каждый кадр</li>
 * </ul>
 */
public class DeadKnightApp extends GameApplication {

    /** Менеджер игрового мира, отвечает за игрока, карту и логику */
    private GameWorldManager worldManager;

    /**
     * Инициализация настроек игры (разрешение, титул, FPS и др.).
     *
     * @param settings объект настроек игры
     */
    @Override
    protected void initSettings(GameSettings settings) {
        SettingsInitializer.initSettings(settings);
    }

    /**
     * Инициализация игры.
     * <p>
     * Вызывает вспомогательные методы для:
     * <ul>
     *     <li>Подключения фабрик сущностей</li>
     *     <li>Инициализации менеджеров и UI</li>
     *     <li>Выбора персонажа</li>
     *     <li>Подключения отладки</li>
     * </ul>
     */
    @Override
    protected void initGame() {
        initFactories();
        initWorldManagers();
        startCharacterSelection();
        initDebug();
    }

    /**
     * Подключение всех фабрик игровых сущностей.
     * <p>
     * Сюда добавляются фабрики для мобов и эссенций.
     */
    private void initFactories() {
        FXGL.getGameWorld().addEntityFactory(new EssenceFactory());

        GameInitializerService gameInitService = new GameInitializerService();
        FXGL.getGameWorld().addEntityFactory(new GoblinFactory(gameInitService.getLootService()));
    }

    /**
     * Инициализация менеджеров игрового мира и UI.
     * <p>
     * Создается {@link GameWorldManager} и {@link UIService}.
     */
    private void initWorldManagers() {
        UIService uiService = new UIService();
        worldManager = new GameWorldManager(new GameInitializerService(), uiService);
    }

    /**
     * Запуск выбора персонажа и инициализация игрового мира после выбора.
     * <p>
     * Использует {@link GameFlowService} для отображения меню выбора персонажа.
     */
    private void startCharacterSelection() {
        new GameFlowService().startCharacterSelection(worldManager::startGame);
    }

    /**
     * Инициализация отладочных инструментов.
     * <p>
     * Создается {@link DebugOverlayService} и настраиваются горячие клавиши.
     */
    private void initDebug() {
        DebugOverlayService debugService = new DebugOverlayService();
        setupDebugKeys(debugService);
        debugService.init();
    }

    /**
     * Настройка горячих клавиш для отладки.
     * <p>
     * F3 включает/отключает отображение хитбоксов.
     *
     * @param debugService сервис отладки
     */
    private void setupDebugKeys(DebugOverlayService debugService) {
        FXGL.onKeyDown(KeyCode.F3, () -> {
            GameConfig.DEBUG_HITBOXES = !GameConfig.DEBUG_HITBOXES;
            if (!GameConfig.DEBUG_HITBOXES) debugService.clear();
        });
    }

    /**
     * Инициализация физических взаимодействий.
     * <p>
     * Настраиваются коллизии для эссенций и апгрейдов.
     */
    @Override
    protected void initPhysics() {
        new EssenceCollisionInitializer().init();
        new UpgradeEssenceCollisionInitializer().init();
    }

    /**
     * Обновление игрового мира каждый кадр.
     *
     * @param tpf время с прошлого кадра (time per frame)
     */
    @Override
    protected void onUpdate(double tpf) {
        worldManager.update(tpf);
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
