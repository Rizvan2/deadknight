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

    public DeathAnimationComponent(List<Image> deathFrames) {
        this.deathFrames = deathFrames;
    }

    @Override
    public void onAdded() {
        if (deathFrames == null || deathFrames.isEmpty())
            return;

        // Сохраняем позицию и кадры до удаления
        Point2D pos = entity.getPosition();
        List<Image> framesCopy = List.copyOf(deathFrames); // чтобы точно сохранить

        // Удаляем оригинального моба
        entity.getViewComponent().clearChildren();
        entity.removeFromWorld();
        // Создаём Entity для анимации на позиции старого моба
        var deathAnim = FXGL.entityBuilder()
                .at(pos)
                .zIndex(100)
                .buildAndAttach();

        deathAnim.addComponent(new AnimationRunner(framesCopy));
    }


    private static class AnimationRunner extends Component {
        private final List<Image> frames;
        private ImageView view;  // Ссылка на ImageView для анимации
        private int frame = 0;
        private double time = 0;
        private static final double FRAME_TIME = 0.3;

        public AnimationRunner(List<Image> frames) {
            this.frames = frames;
        }

        @Override
        public void onAdded() {
            if (!frames.isEmpty()) {
                view = new ImageView(frames.get(0));
                double size = 64;
                view.setFitWidth(size);
                view.setFitHeight(size);
                entity.getViewComponent().addChild(view);
            }
        }

        @Override
        public void onUpdate(double tpf) {
            if (view == null) return; // на всякий случай
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
