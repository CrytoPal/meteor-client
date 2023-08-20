/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;

public class Sprint extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum Mode {
        Strict,
        Rage,
        Omni
    }

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("speed-mode")
        .description("What mode of sprinting.")
        .defaultValue(Mode.Strict)
        .build()
    );

    // Removed whenStationary as it was just Rage sprint
    private final Setting<Boolean> hunger = sgGeneral.add(new BoolSetting.Builder()
        .name("check-for-hunger")
        .description("Checks if you have enough hunger to sprint")
        .defaultValue(false)
        .build()
    );


    public Sprint() {
        super(Categories.Movement, "sprint", "Automatically sprints.");
    }
    
    @Override
    public void onDeactivate() {
        mc.player.setSprinting(false);
    }
    
    private static void sprint() {
        if ((hunger.get() && mc.player.getHungerManager().getFoodLevel() > 6) || !hunger.get()) {
            mc.player.setSprinting(true)
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Mode currentmode = mode.get();
        if (currentmode == Mode.Omni && PlayerUtils.isMoving()) {
            sprint()
        } else if (currentmode == Mode.Strict && mc.player.forwardSpeed > 0) {
            sprint()
        } else if (currentmode == Mode.Rage) {
            sprint()
        }
    }
}
