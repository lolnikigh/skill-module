package com.honepix.zarena.module.skill.data;

import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.Skill;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@DatabaseTable(tableName = "user_bought_skill")
@NoArgsConstructor
@Getter
@Setter
public class BoughtSkill {

    @DatabaseField(generatedId = true)
    private UUID id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;
    @DatabaseField
    private Skill.Type type;
    @DatabaseField
    private int level;
    private boolean selected;

    public BoughtSkill(@NotNull User user, Skill.Type type, int level) {
        this.user = user;
        this.type = type;
        this.level = level;
    }

    public void levelUp() {
        ++level;
    }

}
