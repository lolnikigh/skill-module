package com.honepix.zarena.module.skill.data;

import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.Skill;
import com.j256.ormlite.dao.Dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoughtSkillDao extends Dao<BoughtSkill, UUID> {

    List<BoughtSkill> findByUser(User user);

    Optional<BoughtSkill> findByUserAndType(User user, Skill.Type type);

    BoughtSkill save(BoughtSkill boughtSkill);
}
