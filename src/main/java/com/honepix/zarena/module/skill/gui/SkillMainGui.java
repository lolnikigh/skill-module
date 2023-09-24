package com.honepix.zarena.module.skill.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.SkillModule;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.UserSkillService;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillMainGui {

    private final UserSkillService skillService;
    private final SkillConfig skillConfig;
    private final SkillShopGui skillShopGui;
    private final SkillSelectGui skillSelectGui;

    public SkillMainGui(SkillModule module) {
        this.skillService = module.getUserSkillService();
        this.skillConfig = module.getSkillConfig();
        this.skillShopGui = new SkillShopGui(skillService, skillConfig);
        this.skillSelectGui = new SkillSelectGui(skillService, skillConfig);
    }

    private final Map<UUID, ChestGui> CACHE = new HashMap<>();

    public void create(User user) {

    }

    private void fill() {

    }

    private void cancel(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
