package com.honepix.zarena.module.skill.data;

import com.honepix.userapi.data.User;
import com.honepix.zarena.module.skill.config.Skill;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BoughtSkillDaoImpl extends BaseDaoImpl<BoughtSkill, UUID> implements BoughtSkillDao {

    public BoughtSkillDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, BoughtSkill.class);
        TableUtils.createTableIfNotExists(connectionSource, BoughtSkill.class);
        setObjectCache(ReferenceObjectCache.makeSoftCache());
    }

    @Override
    public List<BoughtSkill> findByUser(User user) {
        List<BoughtSkill> skills;
        try {
            skills = super.queryBuilder().where()
                    .eq("user_id", user).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return skills;
    }

    @Override
    public Optional<BoughtSkill> findByUserAndType(User user, Skill.Type type) {
        List<BoughtSkill> skill;
        try {
            skill = super.queryBuilder().where()
                    .eq("user_id", user)
                    .and()
                    .eq("type", type).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (skill.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(skill.get(0));
    }

    @Override
    public BoughtSkill save(BoughtSkill boughtSkill) {
        try {
            UUID id = super.extractId(boughtSkill);
            boolean exists = super.idExists(id);
            if (exists) super.update(boughtSkill);
            else boughtSkill = super.createIfNotExists(boughtSkill);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return boughtSkill;
    }
}
