package org.example.deadknight.controllers;

import com.almasb.fxgl.entity.Entity;

import java.util.Map;
import java.util.function.Supplier;

import javafx.scene.input.KeyCode;
import org.example.deadknight.skills.PantherSkills;

public class PantherController {

    public static void initInput(Supplier<Entity> pantherSupplier) {
        Map<KeyCode, String> movementMap = Map.of(
                KeyCode.D, "RIGHT",
                KeyCode.A, "LEFT",
                KeyCode.W, "UP",
                KeyCode.S, "DOWN"
        );

        WASDController.setCurrentTpf(1);
        WASDController.initInput(pantherSupplier, movementMap);

        PantherSkills pantherSkills = new PantherSkills(pantherSupplier);
        pantherSkills.registerAttack();

    }
}
