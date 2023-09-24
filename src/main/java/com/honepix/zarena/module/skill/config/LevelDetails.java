package com.honepix.zarena.module.skill.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelDetails {

    private int level;
    private int cost;
    private double modifier;

    public LevelDetails(int level, int cost) {
        this.level = level;
        this.cost = cost;
        this.modifier = 1;
    }
}
