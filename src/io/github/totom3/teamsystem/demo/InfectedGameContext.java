package io.github.totom3.teamsystem.demo;

import io.github.totom3.teamsystem.GameContext;
import io.github.totom3.teamsystem.GameTeam;
import io.github.totom3.teamsystem.GameUtils;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.NameTagVisibility;

/**
 *
 * @author Totom3
 */
public class InfectedGameContext extends GameContext {

    public InfectedGameContext() {
	setFriendlyFire(false);
	setCanSeeFriendlyInvisibles(true);
	setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);

	createTeam(ChatColor.RED);
	createTeam(ChatColor.BLUE);
    }

    public ChatColor getZombiesColor() {
	return ChatColor.RED;
    }

    public ChatColor getSurvivorsColor() {
	return ChatColor.BLUE;
    }

    public InfectedGameTeam getZombiesTeam() {
	return getTeam(ChatColor.RED);
    }

    public InfectedGameTeam getSurvivorsTeam() {
	return getTeam(ChatColor.BLUE);
    }

    @Override
    public InfectedGameTeam getTeamOf(Player player) {
	return (InfectedGameTeam) super.getTeamOf(player);
    }

    @Override
    public InfectedGameTeam getTeam(ChatColor color) {
	return (InfectedGameTeam) super.getTeam(color);
    }

    @Override
    protected Listener makeListener() {
	return new InfectedGameListener(this);
    }

    @Override
    protected GameTeam makeTeam(ChatColor color) {
	return new InfectedGameTeam(this, color);
    }

    public void printInfo(CommandSender sender) {
	InfectedGameTeam zombies = getZombiesTeam();
	InfectedGameTeam survivors = getSurvivorsTeam();

	sender.sendMessage(ChatColor.DARK_RED + "-----=[ " + ChatColor.RED + "Infected Game" + ChatColor.DARK_RED + " ]=-----");
	sender.sendMessage(ChatColor.GRAY + "Zombies Team (" + ChatColor.DARK_GRAY + zombies.getPlayerCount() + ChatColor.GRAY + "): " + ChatColor.DARK_RED + formatPlayers(zombies.getPlayers()));
	sender.sendMessage(ChatColor.GRAY + "Survivors Team (" + ChatColor.DARK_GRAY + survivors.getPlayerCount() + ChatColor.GRAY + "): " + ChatColor.DARK_BLUE + formatPlayers(survivors.getPlayers()));
    }

    private String formatPlayers(Set<Player> players) {
	return GameUtils.formatPlayers(players, ChatColor.GRAY, ChatColor.DARK_GRAY);
    }
}
