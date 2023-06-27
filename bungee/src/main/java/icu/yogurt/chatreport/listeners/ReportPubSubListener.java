package icu.yogurt.chatreport.listeners;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.listener.RedisListener;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ReportPubSubListener extends RedisListener implements Listener {

    private final String channel;
    public ReportPubSubListener(BasePlugin plugin){
        super(plugin);
        channel = "pandora:report";
    }

    @EventHandler
    public void pubSub(PubSubMessageEvent e){
        if(!e.getChannel().equals(channel)){
            return;
        }
        String json = e.getMessage();
        super.process(json);
    }
}
