package com.minepokemine.mhs.sidebar;

import com.minepokemine.mhs.PluginMHS;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerSidebar {
    public static ComponentSidebarLayout Generate(Player player) {
        SidebarComponent title = SidebarComponent.staticLine(Component.text(PluginMHS.instance.getConfig().getString("scoreboard.title")));
        SidebarComponent lines = SidebarComponent.builder()
                .addBlankLine()
                .addStaticLine(Component.text("MH$: ", TextColor.color(0,0,0), TextDecoration.BOLD))
                .addDynamicLine(() -> {
                    PluginMHS.instance.logger.info("Updating scoreboard MH$ for player " + player);
                    return Component.text(
                            PluginMHS.instance.economyModern.balance(PluginMHS.instance.getName(), player.getUniqueId()).intValue(),
                            TextColor.color(94,94,94), TextDecoration.ITALIC);
                })
                .build();

        return new ComponentSidebarLayout(title, lines);
    }
}
