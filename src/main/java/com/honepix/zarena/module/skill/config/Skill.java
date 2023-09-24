package com.honepix.zarena.module.skill.config;

import com.honepix.lib.util.ComponentUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Skill {

    private final Map<Integer, LevelDetails> LEVEL_MAP = new HashMap<>();

    private int points;
    private int customModelData;
    private Component name;
    private List<Component> lore;

    public Skill(int points, int customModelData, @NotNull Component name, @NotNull List<Component> lore) {
        Validate.isTrue(points > 0, "points must be more than 0");
        this.points = points;
        this.customModelData = customModelData;
        this.name = name;
        this.lore = lore;
    }

    public List<Component> loreWithModifier(int level) {
        LevelDetails levelDetails = LEVEL_MAP.get(level);
        int percent = percentByLevel(level);
        List<Component> list = new ArrayList<>();
        lore.forEach(component -> {
            Component line = component
                    .decoration(TextDecoration.ITALIC, false)
                    .replaceText(builder -> builder
                    .matchLiteral("$modifier")
                    .replacement(Component.text(levelDetails.getModifier())
                            .color(TextColor.color(ComponentUtils.colorByPercent(100 - percent)))
                    ));
            list.add(line);
        });
        return list;
    }

    public List<Component> loreForShop(int level) {
        LevelDetails levelDetails = LEVEL_MAP.get(level);

        List<Component> lore = loreWithModifier(level);
        lore.add(Component.empty());
        lore.add(Component.text("Цена ")
                        .color(TextColor.color(0xFAFF89))
                .append(Component.text(levelDetails.getCost())
                        .color(TextColor.color(0x89FF8E)))
                .decoration(TextDecoration.ITALIC, false));
        return lore;
    }

    public void addNextLevelLine() {

    }

    public Component getName(int level, boolean purchased) {
        int percent = percentByLevel(level);
        Component max = Component.empty();
        if (purchased) {
            if (isMaxLevel(level))
                max = Component.text(" MAX!")
                        .color(TextColor.color(0xFF4242))
                        .decoration(TextDecoration.BOLD, true);
        }
        return name
                .append(Component.text(" ур. " + level).color(TextColor.color(ComponentUtils.colorByPercent(100 - percent))))
                .append(max);
    }


    public LevelDetails getLevelDetails(int level) {
        return LEVEL_MAP.get(level);
    }

    public boolean isMaxLevel(int level) {
        return level == getMaxLevel();
    }

    private int percentByLevel(int level) {
        return level * 100 / getMaxLevel();
    }

    public int getMaxLevel() {
        return LEVEL_MAP.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    void addLevel(LevelDetails levelDetails) {
        LEVEL_MAP.put(levelDetails.getLevel(), levelDetails);
    }

    public enum Type {

        RAPID,
        HUNT
    }
}
