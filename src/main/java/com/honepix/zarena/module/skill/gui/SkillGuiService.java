package com.honepix.zarena.module.skill.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.Skill;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.BoughtSkill;
import com.honepix.zarena.module.skill.data.UserSkillService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SkillGuiService {

    private final Map<UUID, UserSelectSkillGui> SELECT_SKILL_GUI = new HashMap<>();
    private final Map<UUID, UserPurchaseSkillGui> PURCHASE_SKILL_GUI = new HashMap<>();
    private final Map<UUID, UserSkillMainGui> SKILL_MAIN_GUI = new HashMap<>();

    private final SkillConfig skillConfig;
    private final UserSkillService skillService;

    public SkillGuiService(SkillConfig skillConfig, UserSkillService skillService) {
        this.skillConfig = skillConfig;
        this.skillService = skillService;
    }

    public void createSelectSkillGui(User user) {
        SELECT_SKILL_GUI.put(user.getId(), new UserSelectSkillGui(user));
    }

    public void createPurchaseSkillGui(User user) {
        PURCHASE_SKILL_GUI.put(user.getId(), new UserPurchaseSkillGui(user));
    }

    public void openSelectSkillGui(User user) {
        Player player = Bukkit.getPlayer(user.getId());
        UserSelectSkillGui gui = new UserSelectSkillGui(user);
        gui.open(player);
    }

    public void openPurchaseSkillGui(User user) {
        Player player = Bukkit.getPlayer(user.getId());
        UserPurchaseSkillGui gui = new UserPurchaseSkillGui(user);
        gui.open(player);
    }

    public void openMainGui(User user) {
        createMainGui(user);
    }


    public void createMainGui(User user) {
        List<BoughtSkill> boughtSkills = skillService.getSkills(user);
        ChestGui gui = new ChestGui(6, "Меню навыков");
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        OutlinePane navigatePane = new OutlinePane(0, 0, 3, 1);
        gui.addPane(navigatePane);
        ItemStack shopItem = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta shopItemItemMeta = shopItem.getItemMeta();
        shopItemItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        shopItemItemMeta.displayName(Component.text("Покупка и улучшение навыков").decoration(TextDecoration.ITALIC, false));
        shopItem.setItemMeta(shopItemItemMeta);
        navigatePane.addItem(new GuiItem(shopItem, event -> openPurchaseSkillGui(user)));

        Player player = Bukkit.getPlayer(user.getId());
        if (player == null) {
            System.out.println("not online");
            return;
        }
        ItemStack selectItem = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta selectItemMeta = selectItem.getItemMeta();
        selectItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        selectItemMeta.displayName(Component.text("Выбор навыков"));
        List<Component> lore = new ArrayList<>();
        int points = boughtSkills.stream().filter(BoughtSkill::isSelected)
                .map(boughtSkill -> skillConfig.getSkill(boughtSkill.getType()))
                .mapToInt(Skill::getPoints)
                .sum();
        int maxPoints = skillConfig.getGroupPoints(player);
        lore.add(Component.text("Всего очков ")
                .append(Component.text(points))
                .append(Component.text("/" + maxPoints)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        selectItemMeta.lore(lore);
        selectItem.setItemMeta(selectItemMeta);
        navigatePane.addItem(new GuiItem(selectItem, event -> openSelectSkillGui(user)));
        gui.show(player);
    }
}
