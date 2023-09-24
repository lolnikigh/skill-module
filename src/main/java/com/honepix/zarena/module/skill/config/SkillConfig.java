package com.honepix.zarena.module.skill.config;

import com.honepix.lib.util.ComponentUtils;
import com.honepix.zarena.module.skill.SkillModule;
import com.honepix.zarena.module.skill.data.BoughtSkill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

import static com.honepix.lib.util.ComponentUtils.fromLegacy;

public class SkillConfig {

    private final SkillModule module;
    private final Logger logger;

    private final EnumMap<Skill.Type, Skill> SKILL_MAP = new EnumMap<>(Skill.Type.class);
    private final Map<String, Integer> GROUP_POINTS_MAP = new HashMap<>();

    private final String PERMISSION_PREFIX = "zarena.skill.points.";

    public SkillConfig(SkillModule module) {
        this.module = module;
        this.logger = module.getLogger();
        Configuration configuration = module.getConfig();
        ConfigurationSection permSection = configuration.getConfigurationSection("group");
        ConfigurationSection skillSection = configuration.getConfigurationSection("skill");
        Validate.notNull(permSection);
        Validate.notNull(skillSection);
        loadPermissions(permSection);
        loadSkills(skillSection);
    }

    @Nullable
    public LevelDetails getLevelDetails(Skill.Type type, int level) {
        Validate.isTrue(level > 0, "level must be more than 0");
        Skill skill = getSkill(type);
        return skill.getLevelDetails(level);
    }

    public int getGroupPoints(Player player) {
        String permission = GROUP_POINTS_MAP.keySet().stream()
                .filter(player::hasPermission)
                .findFirst().orElse(PERMISSION_PREFIX + "default");
        return GROUP_POINTS_MAP.get(permission);
    }

    public Skill getSkill(BoughtSkill boughtSkill) {
        return SKILL_MAP.get(boughtSkill.getType());
    }

    public Skill getSkill(Skill.Type type) {
        return SKILL_MAP.get(type);
    }

    private void loadPermissions(ConfigurationSection section) {
        ConfigurationSection pointsSection = section.getConfigurationSection("points");
        Validate.notNull(pointsSection);
        Set<String> pointsKeys = pointsSection.getKeys(false);
        pointsKeys.forEach(groupName -> {
            String permission = PERMISSION_PREFIX + groupName;
            int points = pointsSection.getInt(groupName, 10);
            GROUP_POINTS_MAP.put(permission, points);
        });
        log("Loaded " + GROUP_POINTS_MAP.size() + " group points " + GROUP_POINTS_MAP);
    }

    private void loadSkills(ConfigurationSection section) {
        Set<String> skillKeys = section.getKeys(false);
        skillKeys.forEach(typeName -> {
            ConfigurationSection skillSection = section.getConfigurationSection(typeName);
            Validate.notNull(skillSection);
            Skill.Type type = Skill.Type.valueOf(typeName);
            int points = skillSection.getInt("points");
            int customModelData = skillSection.getInt("custom-model-data");
            Component name = fromLegacy(skillSection.getString("name")).decoration(TextDecoration.ITALIC, false);
            List<Component> lore = skillSection.getStringList("lore").stream()
                    .map(ComponentUtils::fromLegacy)
                    .toList();
            List<String> rawLevelDetails = skillSection.getStringList("level");
            Validate.notEmpty(rawLevelDetails, "level is empty in " + typeName);
            Skill skill = new Skill(points, customModelData, name, lore);
            for (String rawLevel : rawLevelDetails) {
                LevelDetails levelDetails = parseLevelDetails(rawLevel);
                Validate.notNull(levelDetails);
                skill.addLevel(levelDetails);
            }
            SKILL_MAP.put(type, skill);
        });
        log("Loaded " + SKILL_MAP.size() + " skills " + SKILL_MAP);
    }

    private LevelDetails parseLevelDetails(String value) {
        String[] args = value.split(":");
        Validate.isTrue(!(args.length > 3), "format: <level>:<cost>:(modifier), your value " + value);
        LevelDetails levelDetails = null;
        if (args.length >= 2) {
            int level = Integer.parseInt(args[0]);
            Validate.isTrue(level > 0, "level must be more than 0");
            int cost = Integer.parseInt(args[1]);
            levelDetails = new LevelDetails(level, cost);
            if (args.length == 3) {
                double modifier = Double.parseDouble(args[2]);
                levelDetails.setModifier(modifier);
            }
        }
        return levelDetails;
    }

    private void log(String msg) {
        logger.info("[" + getClass().getSimpleName() + "] --- " + msg);
    }
}
