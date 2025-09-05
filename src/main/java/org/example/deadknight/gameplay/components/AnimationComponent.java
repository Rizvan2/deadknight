package org.example.deadknight.gameplay.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.example.deadknight.gameplay.actors.mobs.entities.GoblinEntity;

public class AnimationComponent extends Component {

    @Getter
    private final GoblinEntity goblinData;

    private ImageView goblinView;

    @Getter
    private boolean attacking = false;

    private int walkIndex = 0;
    private int attackIndex = 0;

    private double walkElapsed = 0;
    private double attackElapsed = 0;

    private static final double WALK_FRAME_TIME = 0.1;
    private static final double ATTACK_FRAME_TIME = 0.04;

    private boolean facingRight = true;

    private final ImageView[] walkRight;
    private final ImageView[] walkLeft;
    private final ImageView[] attackRight;
    private final ImageView[] attackLeft;

    public AnimationComponent(GoblinEntity goblinData) {
        this.goblinData = goblinData;

        this.walkRight = goblinData.getWalkRight();
        this.attackRight = goblinData.getAttackRight();

        // Левые кадры создаются зеркально
        this.walkLeft = createLeftFrames(goblinData.getWalkRight());
        this.attackLeft = createLeftFrames(goblinData.getAttackRight());
    }

    @Override
    public void onAdded() {
        // Инициализируем текущий спрайт сразу
        currentSprite = walkRight[0];
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(currentSprite);
        ((ImageView) currentSprite).setSmooth(true);
        ((ImageView) currentSprite).setCache(true);
        ((ImageView) currentSprite).setCacheHint(CacheHint.SPEED);

    }

    public void setFacingRight(boolean facingRight) {
        if (this.facingRight != facingRight) {
            this.facingRight = facingRight;

            // Убираем текущий кадр и ставим первый кадр нового направления
            entity.getViewComponent().removeChild(currentSprite);
            currentSprite = facingRight ? walkRight[0] : walkLeft[0];
            entity.getViewComponent().addChild(currentSprite);

            walkIndex = 0;
            attackIndex = 0;
            walkElapsed = 0;
            attackElapsed = 0;
        }
    }


    @Override
    public void onUpdate(double tpf) {
        if (attacking) {
            updateAttackAnimation(tpf);
        } else {
            updateWalkAnimation(tpf);
        }
    }

    private void updateAttackAnimation(double tpf) {
        attackElapsed += tpf;
        if (attackElapsed >= ATTACK_FRAME_TIME) {
            ImageView[] frames = facingRight ? attackRight : attackLeft;

            // Убираем предыдущий кадр атаки
            if (currentSprite != null) {
                entity.getViewComponent().removeChild(currentSprite);
            }

            if (attackIndex < frames.length) {
                currentSprite = frames[attackIndex];
                entity.getViewComponent().addChild(currentSprite);
                attackIndex++;
                attackElapsed = 0;
            } else {
                // Анимация атаки закончена — возвращаемся к ходьбе
                attacking = false;
                attackIndex = 0;
                walkElapsed = 0;

                // Сразу ставим кадр ходьбы
                currentSprite = facingRight ? walkRight[walkIndex] : walkLeft[walkIndex];
                entity.getViewComponent().addChild(currentSprite);
            }
        }
    }



    private Node currentSprite;

    private void updateWalkAnimation(double tpf) {
        walkElapsed += tpf;
        if (walkElapsed >= WALK_FRAME_TIME) {
            ImageView[] frames = facingRight ? walkRight : walkLeft;

            if (currentSprite != null) {
                entity.getViewComponent().removeChild(currentSprite);
            }

            currentSprite = frames[walkIndex];
            entity.getViewComponent().addChild(currentSprite);

            walkIndex = (walkIndex + 1) % frames.length;
            walkElapsed = 0;
        }
    }





    private ImageView[] createLeftFrames(ImageView[] rightFrames) {
        ImageView[] left = new ImageView[rightFrames.length];
        for (int i = 0; i < rightFrames.length; i++) {
            ImageView iv = new ImageView(rightFrames[i].getImage());
            iv.setFitWidth(rightFrames[i].getFitWidth());
            iv.setFitHeight(rightFrames[i].getFitHeight());
            iv.setScaleX(-1); // зеркалирование
            left[i] = iv;
        }
        return left;
    }

    public void playAttack() {
        if (attacking) return;
        attacking = true;
        attackIndex = 0;
        attackElapsed = 0;
    }

    public boolean isFacingRight() {
        return facingRight;
    }





}
