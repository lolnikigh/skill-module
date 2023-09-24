package com.honepix.zarena.module.skill.command;

import com.honepix.userapi.data.User;
import com.honepix.userapi.provider.UserProvider;
import com.honepix.zarena.module.skill.config.Skill;
import com.honepix.zarena.module.skill.data.BoughtSkill;
import com.honepix.zarena.module.skill.data.UserSkillService;
import com.honepix.zarena.module.skill.gui.SkillGuiService;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Optional;

@Route(name = "skill")
@AllArgsConstructor
public class SkillCommand {

    private UserSkillService skillService;
    private UserProvider userProvider;
    private SkillGuiService skillGuiService;

    @Execute
    public void process(Player player) {
        Optional<User> optionalUser = userProvider.getUser(player.getUniqueId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            skillGuiService.openMainGui(user);
        }
    }

    @Route(name = "admin")
    @Permission("zarena.skill.admin")
    class Admin {

        @Execute(route = "add")
        public void add(Player player, @Arg Skill.Type type) {
            Optional<User> optionalUser = userProvider.getUser(player.getUniqueId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                skillService.saveBoughtSkill(new BoughtSkill(user, type, 1));
            }
        }
    }
}
