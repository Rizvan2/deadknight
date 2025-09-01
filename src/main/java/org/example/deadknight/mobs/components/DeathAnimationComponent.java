package org.example.deadknight.mobs.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;
import java.util.List;

public class DeathAnimationComponent extends Component {

    private final List<Image> deathFrames;

    private static ImageView view;
    private static final double FRAME_TIME = 0.3; // 0.3 секунды на кадр
    private boolean facingRight;

    public DeathAnimationComponent(List<Image> deathFrames, boolean facingRight) {
        this.deathFrames = deathFrames;
        this.facingRight = facingRight;
    }

    @Override
    public void onAdded() {
        if (deathFrames == null || deathFrames.isEmpty())
            return;

        Point2D pos = entity.getPosition();
        List<Image> framesCopy = List.copyOf(deathFrames);

        // Не трогаем facingRight, оно уже передано через конструктор
        System.out.println("Death animation facingRight = " + facingRight);

        // Удаляем оригинального моба
        entity.getViewComponent().clearChildren();
        entity.removeFromWorld();

        var deathAnim = FXGL.entityBuilder()
                .at(pos.getX(), pos.getY() + 15)
                .zIndex(100)
                .buildAndAttach();

        deathAnim.addComponent(new AnimationRunner(framesCopy, facingRight));
    }




    private static class AnimationRunner extends Component {
        private final List<Image> frames;
        private final boolean facingRight; // <- добавили
        private ImageView view;
        private int frame = 0;
        private double time = 0;
        private static final double FRAME_TIME = 0.3;

        // Конструктор теперь принимает направление
        public AnimationRunner(List<Image> frames, boolean facingRight) {
            this.frames = frames;
            this.facingRight = facingRight;
        }

        @Override
        public void onAdded() {
            if (!frames.isEmpty()) {
                view = new ImageView(frames.get(0));
                double size = 70;
                view.setFitWidth(size);
                view.setFitHeight(size);
                view.setScaleX(facingRight ? 1 : -1); // используем направление
                entity.getViewComponent().addChild(view);
                System.out.println("Animation view added, scaleX = " + view.getScaleX());
            }
        }

        @Override
        public void onUpdate(double tpf) {
            if (view == null) return;
            time += tpf;
            if (time >= FRAME_TIME && frame < frames.size()) {
                view.setImage(frames.get(frame));
                frame++;
                time = 0;
            }
            if (frame >= frames.size()) {
                entity.removeFromWorld();
            }
        }
    }




}
