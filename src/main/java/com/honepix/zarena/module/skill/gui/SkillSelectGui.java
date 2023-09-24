package com.honepix.zarena.module.skill.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.Skill;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.BoughtSkill;
import com.honepix.zarena.module.skill.data.UserSkillService;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkillSelectGui {

    private final UserSkillService skillService;
    private final SkillConfig skillConfig;

    private final Map<UUID, ChestGui> CACHE = new HashMap<>();
    private final Map<UUID, OutlinePane> PANES = new HashMap<>();

    public SkillSelectGui(UserSkillService skillService, SkillConfig skillConfig) {
        this.skillService = skillService;
        this.skillConfig = skillConfig;
    }

    public void create(User user) {
        List<BoughtSkill> boughtSkills = skillService.getSkills(user);
        ChestGui gui = new ChestGui(6, "Выбор навыков");
        gui.setOnGlobalClick(this::cancel);
        OutlinePane background = new OutlinePane(0, 0, 9, 6);
        gui.addPane(background);
        OutlinePane skillPane = new OutlinePane(1, 2, 5, 3);
        fill(skillPane, boughtSkills);
        CACHE.put(user.getId(), gui);
    }


    private void fill(OutlinePane pane, List<BoughtSkill> skills) {

    }

    private void createGuiItem(OutlinePane pane, BoughtSkill boughtSkill) {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        Skill skill = skillConfig.getSkill(boughtSkill.getType());
        meta.setCustomModelData(skill.getCustomModelData());
        meta.displayName(skill.getName());
        meta.lore(skill.loreWithModifier(boughtSkill.getLevel()));
        item.setItemMeta(meta);
        pane.addItem(new GuiItem(item, event -> {
            boolean selected = boughtSkill.isSelected();
            if (selected) {
                boughtSkill.setSelected(false);
                meta.addEnchant(Enchantment.LUCK, 0, false);
            } else {
                boughtSkill.setSelected(true);
                meta.removeEnchant(Enchantment.LUCK);
            }
            skillService.saveBoughtSkill(boughtSkill);
            ChestGui gui = CACHE.get(boughtSkill.getUser().getId());
            gui.update();
        }));
    }

    public void onPurchase(User user, BoughtSkill skill) {
        UUID id = user.getId();
        OutlinePane pane = PANES.get(id);
        createGuiItem(pane, skill);
        ChestGui gui = CACHE.get(id);
        gui.update();
    }

    private void cancel(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
