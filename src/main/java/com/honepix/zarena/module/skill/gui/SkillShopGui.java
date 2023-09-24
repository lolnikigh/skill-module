package com.honepix.zarena.module.skill.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.UserSkillService;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillShopGui {


    private final UserSkillService skillService;
    private final SkillConfig skillConfig;

    private final Map<UUID, ChestGui> CACHE = new HashMap<>();

    public SkillShopGui(UserSkillService skillService, SkillConfig skillConfig) {
        this.skillService = skillService;
        this.skillConfig = skillConfig;
    }

    public void create(User user) {

    }

    private void fill() {

    }

    private void cancel(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
