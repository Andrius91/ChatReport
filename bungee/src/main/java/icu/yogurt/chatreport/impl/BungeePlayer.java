package icu.yogurt.chatreport.impl;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.MessageUtils;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BungeePlayer implements IPlayer {

    private final ProxiedPlayer proxiedPlayer;
    private final Audience audience;

    public BungeePlayer(BasePlugin plugin, @NotNull ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
        this.audience = plugin.getAudienceProvider().player(proxiedPlayer.getUniqueId());
    }

    @Override
    public String getUsername() {
        return proxiedPlayer.getName();
    }

    @Override
    public void sendMessage(String message) {
        MessageUtils.sendMessage(this.audience, message);
    }

    @Override
    public void sendMessage(List<String> messages) {
        MessageUtils.sendMessage(this.audience, messages);
    }
    @Override
    public boolean isConnected() {
        return proxiedPlayer.isConnected();
    }

    @Override
    public UUID getUUID() {
        return proxiedPlayer.getUniqueId();
    }

    @Override
    public String getCurrentServerName() {
        return proxiedPlayer.getServer().getInfo().getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return proxiedPlayer.hasPermission(permission);
    }

}
