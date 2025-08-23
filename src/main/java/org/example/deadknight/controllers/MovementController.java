package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.controllers.WASDController;

public class MovementController {

    private final Entity entity;

    public MovementController(Entity entity) {
        this.entity = entity;
    }

    public void update(double tpf) {
        WASDController.setCurrentTpf(tpf);
    }

    public Entity getEntity() {
        return entity;
    }
}
