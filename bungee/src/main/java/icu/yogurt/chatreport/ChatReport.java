package icu.yogurt.chatreport;

import icu.yogurt.chatreport.common.LibraryLoader;
import net.md_5.bungee.api.plugin.Plugin;

public final class ChatReport extends Plugin{

    private BungeePlugin bungeePlugin;

    @Override
    public void onLoad(){
        LibraryLoader.loadLibraries(getDataFolder());
        this.bungeePlugin = new BungeePlugin(this);
        bungeePlugin.onLoad();
    }

    @Override
    public void onEnable(){
        bungeePlugin.onEnable();
    }

    @Override
    public void onDisable(){
        bungeePlugin.onDisable();
    }
}