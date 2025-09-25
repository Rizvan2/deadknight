package org.example.deadknight.gameplay.actors.player.services;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;


/**
 * Универсальный сервис анимации для персонажей.
 * <p>
 * Позволяет управлять анимацией спрайтов персонажей:
 * движение вправо/влево, idle состояние и анимация атаки.
 * Анимация обновляется через таймер FXGL.
 */
public class AnimationService {

    @Getter
    private static final double FRAME_SIZE = 85; // размер рыцаря

    private final Entity entity;
    private final ImageView[] rightFrames;
    private final ImageView[] leftFrames;
    private final ImageView idleRight;
    private final ImageView idleLeft;
    private int frameIndex = 0;

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
     * Запускает анимацию движения персонажа с обновлением кадров каждые 0.1 секунды.
     */
    public void start() {
        //для анимации рыцаря с цепями
        FXGL.getGameTimer().runAtInterval(this::update, Duration.seconds(0.05));

//        FXGL.getGameTimer().runAtInterval(this::update, Duration.seconds(0.1));

    }

    /**
     * Обновляет кадр анимации в зависимости от направления движения и состояния персонажа.
     * Игнорирует обновление при атаке или наложенной атаке.
     */
    private void update() {
        if (!entity.isActive()) return;

        boolean attacking = entity.getProperties().getBoolean("isAttacking");
        if (attacking) return;

        boolean moving = entity.getProperties().getBoolean("moving");
        String spriteDir = entity.getProperties().getString("spriteDir"); // LEFT или RIGHT
        var view = entity.getViewComponent();

        boolean hasAttackOverlay = view.getChildren().stream()
                .anyMatch(n -> n.getUserData() != null && n.getUserData().equals("attack"));
        if (hasAttackOverlay) return;

        view.clearChildren();

        if (!moving) {
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
     * Создает ImageView для кадра спрайта с указанным размером.
     *
     * @param name имя файла изображения.
     * @param size размер стороны кадра (ширина и высота).
     * @return ImageView с установленными размерами.
     */
    public static ImageView createFrame(String name, double size) {
        ImageView iv = new ImageView(FXGL.image(name));
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    /**
     * Инициализирует анимацию персонажа: движение влево/вправо и idle.
     *
     * @param entity         сущность персонажа.
     * @param baseFrameNames массив имен файлов кадров спрайтов.
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
            right.setScaleX(-1);
            rightFrames[i] = right;
        }

        ImageView idleLeft = createFrame(baseFrameNames[0], FRAME_SIZE);
        ImageView idleRight = new ImageView(idleLeft.getImage());
        idleRight.setFitWidth(FRAME_SIZE);
        idleRight.setFitHeight(FRAME_SIZE);
        idleRight.setScaleX(-1);

        new AnimationService(entity, rightFrames, leftFrames, idleRight, idleLeft).start();
    }

    /**
     * Инициализирует анимацию персонажа из одного или нескольких спрайт-листов.
     * Каждый спрайт-лист должен содержать кадры в одной строке с равной шириной/высотой кадра.
     *
     * @param entity      сущность персонажа
     * @param sheetPaths  пути к изображениям спрайт-листов
     * @param frameWidth  ширина одного кадра в исходном спрайте
     * @param frameHeight высота одного кадра в исходном спрайте
     */
    public static void attachFromSpritesheets(Entity entity,
                                              String[] sheetPaths,
                                              int frameWidth,
                                              int frameHeight) {
        List<ImageView> baseFramesList = new ArrayList<>();

        for (String path : sheetPaths) {
            Image sheet = FXGL.image(path);
            // Если размер кадра не задан, считаем, что кадр квадратный и равен высоте листа (одна строка)
            int fw = frameWidth > 0 ? frameWidth : (int) sheet.getHeight();
            int fh = frameHeight > 0 ? frameHeight : (int) sheet.getHeight();

            int columns = (int) Math.floor(sheet.getWidth() / fw);
            int rows = (int) Math.floor(sheet.getHeight() / fh);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    int x = col * fw;
                    int y = row * fh;
                    ImageView iv = new ImageView(sheet);
                    iv.setViewport(new Rectangle2D(x, y, fw, fh));
                    iv.setFitWidth(FRAME_SIZE);
                    iv.setFitHeight(FRAME_SIZE);
                    baseFramesList.add(iv);
                }
            }
        }

        ImageView[] baseFrames = baseFramesList.toArray(new ImageView[0]);

        ImageView[] leftFrames = new ImageView[baseFrames.length];
        ImageView[] rightFrames = new ImageView[baseFrames.length];

        for (int i = 0; i < baseFrames.length; i++) {
            // Левые кадры — как есть
            leftFrames[i] = baseFrames[i];

            // Правые кадры — отзеркаленные
            ImageView right = new ImageView(baseFrames[i].getImage());
            right.setViewport(baseFrames[i].getViewport());
            right.setFitWidth(FRAME_SIZE);
            right.setFitHeight(FRAME_SIZE);
            right.setScaleX(-1);
            rightFrames[i] = right;
        }

        // Idle кадры — первый кадр
        ImageView idleLeft = new ImageView(baseFrames[0].getImage());
        idleLeft.setViewport(baseFrames[0].getViewport());
        idleLeft.setFitWidth(FRAME_SIZE);
        idleLeft.setFitHeight(FRAME_SIZE);
        ImageView idleRight = new ImageView(idleLeft.getImage());
        idleRight.setViewport(idleLeft.getViewport());
        idleRight.setFitWidth(FRAME_SIZE);
        idleRight.setFitHeight(FRAME_SIZE);
        idleRight.setScaleX(-1);

        new AnimationService(entity, rightFrames, leftFrames, idleRight, idleLeft).start();
    }

    /**
     * Запускает анимацию атаки персонажа и устанавливает задержку между атаками.
     * <p>
     * Метод проверяет, атакует ли персонаж в данный момент, чтобы предотвратить повторные атаки.
     * Меняет спрайт на указанный {@code attackImage}, запускает эффект атаки (например, волну),
     * и после {@code durationSeconds} восстанавливает возможность следующей атаки.
     *
     * @param entity          персонаж, который выполняет атаку
     * @param attackImage     путь к изображению спрайта атаки
     * @param durationSeconds длительность "кулдауна" атаки в секундах
     */
    public static void playAttack(Entity entity, String attackImage, double durationSeconds) {
        if (Boolean.TRUE.equals(entity.getProperties().getBoolean("isAttacking"))) return;
        entity.getProperties().setValue("isAttacking", true);

        String spriteDir = entity.getProperties().getString("spriteDir");

        ImageView attackSprite = getCurrentSprite(entity, 64, 64);
        attackSprite.setImage(FXGL.image(attackImage));
        setSprite(entity, attackSprite, spriteDir);

        WaveService.shoot(entity);

        setAttackCooldown(entity, durationSeconds);
    }

    /**
     * Получает текущий спрайт персонажа.
     *
     * @param entity        персонаж.
     * @param defaultWidth  ширина спрайта по умолчанию.
     * @param defaultHeight высота спрайта по умолчанию.
     * @return ImageView текущего спрайта.
     */
    public static ImageView getCurrentSprite(Entity entity, double defaultWidth, double defaultHeight) {
        double width = defaultWidth, height = defaultHeight;
        ImageView oldIv = null;
        if (!entity.getViewComponent().getChildren().isEmpty() &&
                entity.getViewComponent().getChildren().get(0) instanceof ImageView iv) {
            oldIv = iv;
            width = iv.getFitWidth();
            height = iv.getFitHeight();
        }
        ImageView sprite = (oldIv != null) ? new ImageView(oldIv.getImage()) : new ImageView();
        sprite.setFitWidth(width);
        sprite.setFitHeight(height);
        sprite.setPreserveRatio(true);
        return sprite;
    }

    /**
     * Устанавливает спрайт персонажа, учитывая направление.
     *
     * @param entity    персонаж.
     * @param sprite    спрайт.
     * @param spriteDir направление (LEFT или RIGHT).
     */
    public static void setSprite(Entity entity, ImageView sprite, String spriteDir) {
        entity.getViewComponent().clearChildren();
        if ("RIGHT".equals(spriteDir)) sprite.setScaleX(-1);
        entity.getViewComponent().addChild(sprite);
    }

    /**
     * Устанавливает задержку между атаками персонажа.
     *
     * @param entity          персонаж.
     * @param durationSeconds длительность задержки в секундах.
     */
    public static void setAttackCooldown(Entity entity, double durationSeconds) {
        FXGL.runOnce(() -> {
            entity.getProperties().setValue("isAttacking", false);
        }, Duration.seconds(durationSeconds));
    }

}
