package com.honepix.zarena.module.skill.listener;

import com.honepix.zarena.module.skill.SkillModule;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

public class LuckyPermsListener {

    private SkillModule module;
    private LuckPerms luckPerms;

    public LuckyPermsListener(SkillModule module) {
        this.module = module;
        this.luckPerms = module.getLuckPerms();
        EventBus eventBus = luckPerms.getEventBus();

        // 3. Subscribe to an event using a method reference
        eventBus.subscribe(module, NodeAddEvent.class, this::onUserDataRecalculate);
    }

    private void onUserDataRecalculate(NodeAddEvent event) {
        if (!event.isUser()) {
            return;
        }

        User target = (User) event.getTarget();
        Node node = event.getNode();
        // LuckPerms events are posted async, we want to process on the server thread!
        module.getServer().getScheduler().runTask(module, () -> {
            Player player = module.getServer().getPlayer(target.getUniqueId());
            if (player == null) {
                return;
            }


        });
    }

}
