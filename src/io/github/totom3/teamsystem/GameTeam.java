package io.github.totom3.teamsystem;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Totom3
 */
public class GameTeam {

    private final Team bukkitTeam;
    private final ChatColor color;
    private final GameContext context;
    private final Set<Player> players;

    private boolean valid;

    public GameTeam(GameContext context, ChatColor color) {
	this.color = GameUtils.checkColor(color);
	this.context = checkNotNull(context, "GameContext cannot be null");

	this.valid = true;
	this.players = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>());

	Team bTeam = createBukkitTeam();
	bTeam.setDisplayName(getDisplayName());
	bTeam.setAllowFriendlyFire(context.hasFriendlyFire());
	bTeam.setNameTagVisibility(context.getNameTagVisibility());
	bTeam.setCanSeeFriendlyInvisibles(context.canSeeFriendlyInvisibles());
	bukkitTeam = bTeam;
    }

    public ChatColor getColor() {
	return color;
    }

    public Team getBukkitTeam() {
	return bukkitTeam;
    }

    public GameContext getContext() {
	return context;
    }

    public int getPlayerCount() {
	return players.size();
    }

    public boolean isEmpty() {
	return players.isEmpty();
    }

    public Set<Player> getPlayers() {
	return players;
    }

    public boolean addPlayer(Player player) {
	checkValid();
	if (!players.add(GameUtils.checkPlayer(player))) {
	    return false;
	}

	GameTeam oldTeam = context.getTeamOf(player);
	if (oldTeam != null) {
	    oldTeam.removePlayer(player);
	}

	context.setTeam0(player, this);
	player.setScoreboard(bukkitTeam.getScoreboard());
	bukkitTeam.addPlayer(player);
	onJoin(player);
	return true;
    }

    public boolean removePlayer(Player player) {
	checkValid();
	if (!players.remove(player)) {
	    return false;
	}

	context.setTeam0(player, null);
	bukkitTeam.removePlayer(player);
	onQuit(player);
	return true;
    }

    public void clearPlayers() {
	Set<Player> set = new HashSet<>(players);
	players.clear();
	for (Player player : set) {
	    bukkitTeam.removePlayer(player);
	    onQuit(player);
	}

    }

    public boolean isValid() {
	return valid;
    }

    public String getName() {
	String colorName = getColor().name();
	return colorName.charAt(0) + colorName.substring(1).toLowerCase() + " Team";
    }

    public String getDisplayName() {
	return getName();
    }

    public void applySettings() {
	bukkitTeam.setAllowFriendlyFire(context.hasFriendlyFire());
	bukkitTeam.setCanSeeFriendlyInvisibles(context.canSeeFriendlyInvisibles());
	bukkitTeam.setNameTagVisibility(context.getNameTagVisibility());
	bukkitTeam.setDisplayName(getDisplayName());
    }

    // -----------------------=[ Internal ]=-----------------------
    void invalidate() {
	valid = false;
	clearPlayers();
    }

    void checkValid() {
	if (!valid) {
	    throw new IllegalStateException("Team is invalid");
	}
    }

    // -----------------------=[ - ]=-----------------------
    protected Team createBukkitTeam() {
	String name = getName();
	Scoreboard sb = context.getScoreboard();

	Team bTeam = sb.getTeam(name);
	if (bTeam == null) {
	    bTeam = sb.registerNewTeam(name);
	}

	return bTeam;
    }

    /**
     * Called when a player joins the team. When this method is called, the
     * player is already registered in the team.
     * @param player the player to join the team.
     */
    protected void onJoin(Player player) {
    }

    /**
     * Called when a player leaves the team. When this method is called, the
     * player is already unregistered.
     * @param player the player to leave the team.
     */
    protected void onQuit(Player player) {
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "{" + "color=" + color + ", players=" + players + ", valid=" + valid + '}';
    }

}
