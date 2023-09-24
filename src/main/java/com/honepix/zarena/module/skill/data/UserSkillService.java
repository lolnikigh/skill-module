package com.honepix.zarena.module.skill.data;

import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.LevelDetails;
import com.honepix.zarena.module.skill.config.Skill;

import java.util.List;
import java.util.Optional;

public interface UserSkillService {

    Optional<BoughtSkill> getSkill(User user, Skill.Type type);

    List<BoughtSkill> getSkills(User user);

    BoughtSkill saveBoughtSkill(BoughtSkill boughtSkill);

    LevelDetails getLevelDetails(Skill.Type type, int level);

    boolean upgradeSkill(User user, Skill.Type type);

}
