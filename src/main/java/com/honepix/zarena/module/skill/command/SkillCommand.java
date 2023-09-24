package com.honepix.zarena.module.skill.command;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

@Route(name = "skill")
@AllArgsConstructor
public class AdminSkillCommand {

    @Execute
    public void process(Player player) {

    }

    @Permission("zarena.skill.admin")

}
