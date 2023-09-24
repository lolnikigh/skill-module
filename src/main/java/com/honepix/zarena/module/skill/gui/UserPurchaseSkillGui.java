package com.honepix.zarena.module.skill.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.SkillModule;
import com.honepix.zarena.module.skill.config.Skill;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.BoughtSkill;
import com.honepix.zarena.module.skill.data.UserSkillService;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class UserPurchaseSkillGui {

    private final OutlinePane pane;
    private final ChestGui gui;

    private final UserSkillService skillService;
    private final SkillConfig skillConfig;

    public UserPurchaseSkillGui(User user) {
        SkillModule module = SkillModule.getInstance();
        this.skillService = module.getUserSkillService();
        this.skillConfig = module.getSkillConfig();

        this.pane = new OutlinePane(1, 2, 5, 2);
        this.gui = new ChestGui(6, "Выбрать навыки");
        onCreate(user);
        gui.addPane(pane);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

    }

    public void open(HumanEntity entity) {
        gui.show(entity);
    }

    public void addAll(User user) {
        EnumMap<Skill.Type, Integer> skillMap = new EnumMap<>(Skill.Type.class);
        List<BoughtSkill> boughtSkills = skillService.getSkills(user);
        boughtSkills.forEach(boughtSkill -> {
            Skill.Type type = boughtSkill.getType();
            int level = boughtSkill.getLevel();
            skillMap.put(type, level);
        });
        int maxLevelReachedCounter = 0;
        Skill.Type[] skills = Skill.Type.values();
        for (Skill.Type type : skills) {
            Skill skill = skillConfig.getSkill(type);
            ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (skillMap.containsKey(type)) {
                int level = skillMap.get(type);
                if (skill.isMaxLevel(level)) {
                    maxLevelReachedCounter++;
                } else {
                    ++level;
                    meta.displayName(skill.getName(level, false));
                    meta.lore(skill.loreForShop(level));
                    meta.setCustomModelData(skill.getCustomModelData());
                    item.setItemMeta(meta);
                }

            } else {
                meta.displayName(skill.getName(1, false));
                meta.lore(skill.loreForShop(1));
                meta.setCustomModelData(skill.getCustomModelData());
                item.setItemMeta(meta);
            }
            pane.addItem(new GuiItem(item, event -> {

                boolean success = skillService.upgradeSkill(user, type);
                if (success) {
                    ItemMeta itemMeta = item.getItemMeta();
                    Optional<BoughtSkill> optionalBoughtSkill = skillService.getSkill(user, type);
                    if (optionalBoughtSkill.isPresent()) {
                        BoughtSkill boughtSkill = optionalBoughtSkill.get();
                        int lvl = boughtSkill.getLevel();
                        if (skill.isMaxLevel(lvl)) {
                            item.setType(Material.AIR);
                            gui.update();
                            return;
                        }
                        itemMeta.displayName(skill.getName(lvl, true));
                        itemMeta.lore(skill.loreForShop(lvl));
                        item.setItemMeta(itemMeta);
                        gui.update();
                    }
                }
            }));
        }
        if (maxLevelReachedCounter == skills.length) {
            ItemStack itemStack = new ItemStack(Material.GREEN_CONCRETE);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component.text("Поздравляем, вы прокачали все свои навыки!"));
            itemStack.setItemMeta(meta);
        }
    }

    private void onCreate(User user) {
        addAll(user);
    }
}
