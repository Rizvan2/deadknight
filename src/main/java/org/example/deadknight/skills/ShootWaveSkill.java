package org.example.deadknight.skills;

import com.almasb.fxgl.entity.Entity;
import org.example.deadknight.services.WaveService;

public class ShootWaveSkill implements Skill {
    @Override
    public void use(Entity user) {
        if (user == null || user.getWorld() == null) return;
        WaveService.shoot(user);
    }
}
