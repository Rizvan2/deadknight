package org.example.deadknight.infrastructure.dto;

import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VisibleChunks {
    private final List<ImageView> ground;
    private final List<ImageView> trees;

    public VisibleChunks(List<ImageView> ground, List<ImageView> trees) {
        this.ground = ground;
        this.trees = trees;
    }

}
