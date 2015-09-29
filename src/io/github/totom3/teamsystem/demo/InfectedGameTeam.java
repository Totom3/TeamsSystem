package io.github.totom3.teamsystem.demo;

import io.github.totom3.teamsystem.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Totom3
 */
public class InfectedGameTeam extends GameTeam {

    private final boolean isZombieTeam;

    public InfectedGameTeam(InfectedGameContext context, ChatColor color) {
	super(context, color);
	
	switch (color) {
	    case RED:
		isZombieTeam = true;
		break;
	    case BLUE:
		isZombieTeam = false;
		break;
	    default:
		throw new IllegalArgumentException("invalid color: " + color);
	}
    }

    public boolean isZombieTeam() {
	return isZombieTeam;
    }

    public boolean isSurvivorTeam() {
	return !isZombieTeam;
    }

    @Override
    public String getName() {
	return (isZombieTeam) ? "Zombies Team" : "Survivors Team";
    }

    @Override
    protected void onJoin(Player player) {
	if (isZombieTeam) {
	    String msg = ChatColor.DARK_RED + player.getName() + ChatColor.RED + " is now infected!";
	    for (Player p : getContext().players().keySet()) {
		p.sendMessage(msg);
	    }
	}
    }

    @Override
    public String toString() {
	return ((isZombieTeam) ? "Zombies" : "Survivors") + " Team";
    }

}
