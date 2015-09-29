package io.github.totom3.teamsystem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Totom3
 */
public class DefaultGameListener implements Listener {

    private final GameContext context;

    public DefaultGameListener(GameContext context) {
	this.context = context;
    }

    @EventHandler
    void on(PlayerQuitEvent event) {
	onQuit(event.getPlayer());
    }

    @EventHandler
    void on(PlayerKickEvent event) {
	onQuit(event.getPlayer());
    }

    void onQuit(Player player) {
	GameTeam team = context.getTeamOf(player);
	if (team != null) {
	    team.removePlayer(player);
	}
    }
}
