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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UserSelectSkillGui {

    private final OutlinePane pane;
    private final ChestGui gui;

    private final UserSkillService skillService;
    private final SkillConfig skillConfig;

    public UserSelectSkillGui(User user) {
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

    public void addAll(List<BoughtSkill> boughtSkills) {
        boughtSkills.forEach(this::add);
    }

    public void add(BoughtSkill boughtSkill) {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        int level = boughtSkill.getLevel();
        Skill skill = skillConfig.getSkill(boughtSkill.getType());
        meta.setCustomModelData(skill.getCustomModelData());
        meta.displayName(skill.getName(level, true));
        meta.lore(skill.loreWithModifier(level));
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
            item.setItemMeta(meta);
            skillService.saveBoughtSkill(boughtSkill);
            gui.update();
        }));
    }

    private void onCreate(User user) {
        List<BoughtSkill> skills = skillService.getSkills(user);
        if (skills.isEmpty()) {
            ItemStack empty = new ItemStack(Material.RED_CONCRETE);
            ItemMeta meta = empty.getItemMeta();
            meta.displayName(Component.text("У вас пока нет навыков"));
        } else {
            addAll(skills);
        }
    }


}
