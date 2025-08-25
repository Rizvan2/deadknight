package org.example.deadknight.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

/**
 * Универсальный сервис анимации для персонажей.
 * <p>
 * Позволяет управлять анимацией спрайтов персонажей:
 * движение вправо/влево и idle состояние.
 * Анимация обновляется через таймер FXGL.
 */
public class AnimationService {

    private static final double FRAME_SIZE = 64;
    private static final String ORIGINAL_NODE_KEY = "originalNode";

    private final Entity entity;
    private final ImageView[] rightFrames;
    private final ImageView[] leftFrames;
    private final ImageView idleRight;
    private final ImageView idleLeft;
    private int frameIndex = 0;
    private static final Map<Entity, Texture> originalTextures = new HashMap<>();


    /**
     * Конструктор сервиса анимации.
     *
     * @param entity      Сущность персонажа для анимации.
     * @param rightFrames Массив спрайтов для движения вправо.
     * @param leftFrames  Массив спрайтов для движения влево.
     * @param idleRight   Спрайт для стояния направо.
     * @param idleLeft    Спрайт для стояния налево.
     */
    public AnimationService(Entity entity,
                            ImageView[] rightFrames,
                            ImageView[] leftFrames,
                            ImageView idleRight,
                            ImageView idleLeft) {
        this.entity = entity;
        this.rightFrames = rightFrames;
        this.leftFrames = leftFrames;
        this.idleRight = idleRight;
        this.idleLeft = idleLeft;
    }

    /**
     * Запускает анимацию, обновляя кадры каждые 0.1 секунды.
     */
    public void start() {
        FXGL.getGameTimer().runAtInterval(this::update, Duration.seconds(0.1));
    }

    /**
     * Обновляет текущий кадр анимации в зависимости от направления
     * и состояния движения персонажа.
     */
    private void update() {
        if (!entity.isActive()) return;

        // Проверка, идёт ли атака — тогда анимация движения не обновляется
        boolean attacking = entity.getProperties().getBoolean("isAttacking");
        if (attacking) return;

        boolean moving = entity.getProperties().getBoolean("moving");
        String spriteDir = entity.getProperties().getString("spriteDir"); // LEFT или RIGHT
        var view = entity.getViewComponent();

        // Если уже есть overlay атаки — не трогаем базовый спрайт
        boolean hasAttackOverlay = view.getChildren().stream()
                .anyMatch(n -> n.getUserData() != null && n.getUserData().equals("attack"));
        if (hasAttackOverlay) return;

        // Очищаем текущие кадры
        view.clearChildren();

        // Выбираем спрайт в зависимости от направления движения
        if (!moving) {
            // Статический спрайт (idle)
            view.addChild("RIGHT".equals(spriteDir) ? idleRight : idleLeft);
        } else if ("RIGHT".equals(spriteDir)) {
            view.addChild(rightFrames[frameIndex]);
            frameIndex = (frameIndex + 1) % rightFrames.length;
        } else if ("LEFT".equals(spriteDir)) {
            view.addChild(leftFrames[frameIndex]);
            frameIndex = (frameIndex + 1) % leftFrames.length;
        }
    }




    /**
     * Создает ImageView для одного кадра спрайта с заданным размером.
     *
     * @param name Имя файла изображения в ресурсах.
     * @param size Размер стороны кадра (ширина и высота).
     * @return ImageView с установленными размерами.
     */
    public static ImageView createFrame(String name, double size) {
        ImageView iv = new ImageView(FXGL.image(name));
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    /**
     * Универсальный метод attach для любого персонажа.
     * <p>
     * Создает массивы кадров для движения влево и вправо,
     * а также idle спрайты, и запускает анимацию.
     *
     * @param entity         Сущность персонажа для анимации.
     * @param baseFrameNames Массив имен файлов кадров спрайтов.
     */
    public static void attach(Entity entity, String[] baseFrameNames) {
        ImageView[] baseFrames = new ImageView[baseFrameNames.length];
        for (int i = 0; i < baseFrameNames.length; i++)
            baseFrames[i] = createFrame(baseFrameNames[i], FRAME_SIZE);

        ImageView[] leftFrames = new ImageView[baseFrames.length];
        ImageView[] rightFrames = new ImageView[baseFrames.length];

        for (int i = 0; i < baseFrames.length; i++) {
            leftFrames[i] = baseFrames[i];

            ImageView right = new ImageView(baseFrames[i].getImage());
            right.setFitWidth(FRAME_SIZE);
            right.setFitHeight(FRAME_SIZE);
            right.setScaleX(-1); // зеркально для движения вправо
            rightFrames[i] = right;
        }

        ImageView idleLeft = createFrame(baseFrameNames[0], FRAME_SIZE);
        ImageView idleRight = new ImageView(idleLeft.getImage());
        idleRight.setFitWidth(FRAME_SIZE);
        idleRight.setFitHeight(FRAME_SIZE);
        idleRight.setScaleX(-1);

        new AnimationService(entity, rightFrames, leftFrames, idleRight, idleLeft).start();
    }

    public static void playAttack(Entity knight, String attackImage, double durationSeconds) {
        Boolean isAttacking = knight.getProperties().getBoolean("isAttacking");
        if (isAttacking != null && isAttacking) return;

        knight.getProperties().setValue("isAttacking", true);

        // Используем spriteDir для горизонтального зеркалирования
        String spriteDir = knight.getProperties().getString("spriteDir"); // LEFT или RIGHT

        // Размер старой текстуры
        double width = 64, height = 64;
        if (!knight.getViewComponent().getChildren().isEmpty() &&
                knight.getViewComponent().getChildren().get(0) instanceof ImageView oldIv) {
            width = oldIv.getFitWidth();
            height = oldIv.getFitHeight();
        }

        // Чистим и добавляем кадр атаки
        knight.getViewComponent().clearChildren();
        ImageView attackIv = new ImageView(FXGL.image(attackImage));
        attackIv.setFitWidth(width);
        attackIv.setFitHeight(height);
        if ("RIGHT".equals(spriteDir)) attackIv.setScaleX(-1); // зеркалим только при движении вправо
        knight.getViewComponent().addChild(attackIv);

        // Волна
        WaveService.shoot(knight);

        // Возвращаем анимацию ходьбы
        FXGL.runOnce(() -> {
            knight.getProperties().setValue("isAttacking", false);
            knight.getViewComponent().clearChildren();
            startWalkAnimation(knight, 64, 64, spriteDir); // тоже используем spriteDir
        }, Duration.seconds(durationSeconds));
    }

    public static void startWalkAnimation(Entity knight, double width, double height, String spriteDir) {
        knight.getProperties().setValue("walkFrameIndex", 0);

        ImageView iv = new ImageView(FXGL.image("knight_left-1.png"));
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        if ("RIGHT".equals(spriteDir)) iv.setScaleX(-1);
        knight.getViewComponent().addChild(iv);
    }
}
