package org.example.deadknight.ui;

import com.almasb.fxgl.entity.Entity;

public class UIController {

    private final HealthBar healthBar;

    public UIController(Entity knight) {
        this.healthBar = new HealthBar(knight);
    }

    public void update() {
        healthBar.update();
    }
}
