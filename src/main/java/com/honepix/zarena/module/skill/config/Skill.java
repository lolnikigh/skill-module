package com.honepix.zarena.module.skill.data;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Skill {

    private final Map<Integer, LevelDetails> LEVEL_MAP = new HashMap<>();

    private Component name;
    private List<Component> lore;

    public Skill(@NotNull Component name, @NotNull List<Component> lore) {
        this.name = name;
        this.lore = lore;
    }


    public LevelDetails getLevelDetails(int level) {
        return LEVEL_MAP.get(level);
    }

    public int getMaxLevel() {
        return LEVEL_MAP.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    enum Type {

        RAPID
    }
}
