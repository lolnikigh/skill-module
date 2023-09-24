package com.honepix.zarena.module.skill.data;

import com.honepix.userapi.data.User;
import com.honepix.userapi.provider.UserProvider;
import com.honepix.zarena.module.economy.api.event.UserSpendCoinsEvent;
import com.honepix.zarena.module.economy.data.UserEconomy;
import com.honepix.zarena.module.economy.data.UserEconomyService;
import com.honepix.zarena.module.skill.SkillModule;
import com.honepix.zarena.module.skill.config.LevelDetails;
import com.honepix.zarena.module.skill.config.Skill;
import com.honepix.zarena.module.skill.config.SkillConfig;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserSkillServiceImpl implements UserSkillService, Listener {

    private final SkillModule module;
    private final Logger logger;

    private final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    private final BoughtSkillDao boughtSkills;

    private UserProvider userProvider;
    private UserEconomyService userEconomyService;
    private SkillConfig skillConfig;


    public UserSkillServiceImpl(SkillModule module) {
        this.module = module;
        this.logger = module.getLogger();
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(module.getDataSource(), module.getDatabaseType())) {
            this.boughtSkills = new BoughtSkillDaoImpl(connectionSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.userProvider = module.getUserProvider();
        this.userEconomyService = module.getUserEconomyService();
        this.skillConfig = module.getSkillConfig();
        Bukkit.getPluginManager().registerEvents(this, module);
        startCacheCleaner();
    }

    @Override
    public Optional<BoughtSkill> getSkill(User user, Skill.Type type) {
        return boughtSkills.findByUserAndType(user, type);
    }

    @Override
    public List<BoughtSkill> getSkills(User user) {
        return boughtSkills.findByUser(user);
    }

    @Override
    public BoughtSkill saveBoughtSkill(BoughtSkill boughtSkill) {
        return boughtSkills.save(boughtSkill);
    }

    @Override
    public LevelDetails getLevelDetails(Skill.Type type, int level) {
        return skillConfig.getLevelDetails(type, level);
    }

    @Override
    public boolean upgradeSkill(User user, Skill.Type type) {
        Optional<BoughtSkill> optionalBoughtSkill = getSkill(user, type);
        Skill skill = skillConfig.getSkill(type);
        Optional<UserEconomy> optionalUserEconomy = userEconomyService.getUserEconomy(user);
        if (optionalUserEconomy.isEmpty()) return false;
        BoughtSkill boughtSkill;
        if (optionalBoughtSkill.isPresent()) {
            boughtSkill = optionalBoughtSkill.get();
            int level = boughtSkill.getLevel();
            if (skill.isMaxLevel(level)) return false;
            int nextLevel = level + 1;
            LevelDetails levelDetails = skill.getLevelDetails(nextLevel);
            Component component = Component.text("Прокачан навык ")
                    .append(skill.getName(level, true))
                    .color(TextColor.color(0x89FF8E));
            UserSpendCoinsEvent event = new UserSpendCoinsEvent(levelDetails.getCost(), user, component);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isSuccess()) {
                boughtSkill.setLevel(nextLevel);
                saveBoughtSkill(boughtSkill);
                return true;
            }
        } else {
            boughtSkill = new BoughtSkill(user, type, 1);
            LevelDetails levelDetails = skill.getLevelDetails(boughtSkill.getLevel());
            Component component = Component.text("Куплен новый навык, ")
                    .append(skill.getName(1, true))
                    .color(TextColor.color(0x89FF8E));
            UserSpendCoinsEvent event = new UserSpendCoinsEvent(levelDetails.getCost(), user, component);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isSuccess()) {
                saveBoughtSkill(boughtSkill);
                return true;
            }
        }
        return false;
    }

    private void startCacheCleaner() {
        final long PERIOD = 20 * 60 * 30;
        SCHEDULER.scheduleSyncRepeatingTask(module, this::clearCache, 0, 20 * 60 * 30);
        log("Cache cleaner started. Clean cache every " + PERIOD + " ticks");
    }

    private void clearCache() {
        int amount = boughtSkills.getObjectCache().size(UserEconomy.class);
        boughtSkills.clearObjectCache();
        log("Removed " + amount + " objects from cache");
    }

    private void log(String msg) {
        logger.info("[" + getClass().getSimpleName() + "] --- " + msg);
    }

}
