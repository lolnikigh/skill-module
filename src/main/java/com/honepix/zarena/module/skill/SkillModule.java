package com.honepix.zarena.module.skill;

import com.honepix.lib.HonepixLib;
import com.honepix.userapi.UserAPI;
import com.honepix.userapi.provider.UserProvider;
import com.honepix.zarena.module.economy.EconomyModule;
import com.honepix.zarena.module.economy.data.UserEconomyService;
import com.honepix.zarena.module.skill.command.InvalidUsageHandlerImpl;
import com.honepix.zarena.module.skill.command.PermissionHandlerImpl;
import com.honepix.zarena.module.skill.command.SkillCommand;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.honepix.zarena.module.skill.data.UserSkillService;
import com.honepix.zarena.module.skill.data.UserSkillServiceImpl;
import com.honepix.zarena.module.skill.gui.SkillGuiService;
import com.j256.ormlite.jdbc.db.MariaDbDatabaseType;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

@Getter
public final class SkillModule extends JavaPlugin {


    private LiteCommands<CommandSender> liteCommands;
    private final MariaDbDatabaseType databaseType = new MariaDbDatabaseType();
    private DataSource dataSource;
    private UserProvider userProvider;
    private UserEconomyService userEconomyService;
    private UserSkillService userSkillService;
    private SkillConfig skillConfig;
    private SkillGuiService skillGuiService;
    private LuckPerms luckPerms;

    private static SkillModule instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        luckPerms = LuckPermsProvider.get();
        dataSource = HonepixLib.getInstance().getDataSource();
        skillConfig = new SkillConfig(this);
        userProvider = UserAPI.getInstance().getUserProvider();
        userEconomyService = EconomyModule.getInstance().getEconomyService();
        userSkillService = new UserSkillServiceImpl(this);
        skillGuiService = new SkillGuiService(skillConfig, userSkillService);
        liteCommands = LitePaperAdventureFactory.builder(getServer(), "economy-module")
                .argument(Player.class, new BukkitPlayerArgument<>(getServer(), "&cИгрока нет на сервере"))

                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Только игроки могут использовать эту команду"))

                //Commands
                .commandInstance(new SkillCommand(userSkillService, userProvider, skillGuiService))

                // Handlers
                .invalidUsageHandler(new InvalidUsageHandlerImpl())
                .permissionHandler(new PermissionHandlerImpl())
                .register();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SkillModule getInstance() {
        return instance;
    }
}
