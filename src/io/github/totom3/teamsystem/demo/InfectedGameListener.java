package io.github.totom3.teamsystem.demo;

import io.github.totom3.teamsystem.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Totom3
 */
public class InfectedGameListener implements Listener {

    private final InfectedGameContext context;

    public InfectedGameListener(InfectedGameContext context) {
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

    @EventHandler
    void on(EntityDamageByEntityEvent event) {
	if (!(event.getEntity() instanceof Player)) {
	    return;
	}

	if (!(event.getDamager() instanceof Player)) {
	    return;
	}

	Player damaged = (Player) event.getEntity();
	Player damager = (Player) event.getDamager();

	InfectedGameTeam damagedTeam = context.getTeamOf(damaged);
	InfectedGameTeam damagerTeam = context.getTeamOf(damager);

	// if the damaged player is not a survivor
	if (damagedTeam == null || damagedTeam.isZombieTeam()) {
	    return;
	}

	// if the damager is not a zombie
	if (damagerTeam == null || damagerTeam.isSurvivorTeam()) {
	    return;
	}

	event.setDamage(10000);
	
	// add damaged player to zombies team
	damagerTeam.addPlayer(damaged);
    }

    @EventHandler
    void on(PlayerDeathEvent event) {
	Player player = event.getEntity();
	InfectedGameTeam team = context.getTeamOf(player);
	
	// if the player is not a survivor
	if (team == null || team.isZombieTeam()) {
	    return;
	}
	
	event.setDeathMessage(null);
	context.getZombiesTeam().addPlayer(player);
    }
}
