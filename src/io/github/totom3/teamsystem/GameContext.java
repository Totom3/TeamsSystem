package io.github.totom3.teamsystem;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Totom3
 */
public class GameContext {

    private final Scoreboard scoreboard;
    private final Map<Player, GameTeam> players;
    private final Map<ChatColor, GameTeam> teams;

    // Global settings
    private boolean allowFriendlyFire = true;
    private boolean seeFriendlyInvisibles = false;
    private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;

    public GameContext() {
	this(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public GameContext(Scoreboard scoreboard) {
	this.scoreboard = checkNotNull(scoreboard, "Bukkit Scoreboard cannot be null");
	this.players = new WeakHashMap<>();
	this.teams = new EnumMap<>(ChatColor.class);

	Listener listener = makeListener();
	if (listener != null) {
	    Bukkit.getPluginManager().registerEvents(listener, Main.get());
	}
    }

    public Map<ChatColor, GameTeam> teams() {
	return Collections.unmodifiableMap(teams);
    }

    public Map<Player, GameTeam> players() {
	return Collections.unmodifiableMap(players);
    }

    public Scoreboard getScoreboard() {
	return scoreboard;
    }

    public GameTeam getTeam(ChatColor color) {
	return teams.get(GameUtils.checkColor(color));
    }

    public boolean hasTeam(ChatColor color) {
	/*
	 * using Map#get(K) instead of Map#containsKey() in case extending
	 * classes have a bug where keys are assigned to null values (in which
	 * case containsKey() will return true)
	 */
	return teams.get(GameUtils.checkColor(color)) != null;
    }

    // -------------------=[ Team Editing ]=-------------------
    // Only subclasses may create, remove, or clear teams.
    protected GameTeam createTeam(ChatColor color) {
	GameTeam newTeam = makeTeam(GameUtils.checkColor(color));
	GameTeam oldTeam = teams.put(color, newTeam);

	if (oldTeam != null) {
	    oldTeam.invalidate();
	}
	return oldTeam;
    }

    protected void clearTeams() {
	for (GameTeam team : teams.values()) {
	    team.invalidate();
	}
	teams.clear();
	players.clear();
    }

    protected GameTeam removeTeam(ChatColor color) {
	GameTeam team = getTeam(color);
	if (team == null) {
	    return null;
	}

	for (Player player : team.getPlayers()) {
	    players.remove(player);
	}
	team.invalidate();
	teams.remove(color);
	return team;
    }

    // -------------------=[ Players' Team ]=-------------------
    /**
     * Returns the current team of a player.
     * @param player the player to get the team of.
     * @return the team the specified player currently is in, or {@code null} if
     *         the player isn't in any team.
     */
    public GameTeam getTeamOf(Player player) {
	return players.get(GameUtils.checkPlayer(player));
    }

    /**
     * Sets the team of a player.
     * @param player    the player to set the team of. Must not be {@code null}.
     * @param teamColor the color of the team to set. May be {@code null}, in
     *                  which case the player will be removed from it's current
     *                  team. If not {@code null}, must be a non-format color
     *                  and must not equal {@link ChatColor#RESET}.
     * @return the team the player was in previously, or {@code null} if the
     *         player wasn't in any team.
     * @throws NullPointerException     if {@code player} is {@code null}.
     * @throws IllegalArgumentException if {@code teamColor} is a format, or if
     *                                  it equals {@link ChatColor#RESET}.
     */
    public GameTeam setTeamOf(Player player, ChatColor teamColor) {
	/*
	 * Internal note: this method will first fetch the team with the
	 * specified color, and will then call GameTeam#addPlayer(Player)
	 * or GameTeam#removePlayer(Player). These two methods will in turn
	 * invoke GameContext#setTeam0(Player, GameTeam), which will complete
	 * the transition internally.
	 */
	GameTeam oldTeam = getTeamOf(player);

	if (teamColor == null) {
	    oldTeam.removePlayer(player);
	    return oldTeam;
	}

	GameTeam newTeam = getTeam(teamColor);
	if (newTeam == null) {
	    throw new IllegalArgumentException("No such team " + teamColor);
	}

	newTeam.addPlayer(player);
	return oldTeam;
    }

    /**
     * Only called by
     * {@link GameTeam#addPlayer(Player)}, {@link GameTeam#removePlayer(Player)}
     * and {@link GameTeam#clearPlayers()}. No other code should call this
     * method, to ensure that no infinite loops are created.
     * @param player the player to change the team of.
     * @param team   the team to set, or null
     */
    GameTeam setTeam0(Player player, GameTeam team) {
	if (team == null) {
	    return players.remove(player);
	} else {
	    return players.put(player, team);
	}
    }

    /**
     * Creates a team appropriate for this {@code GameContext}. Instantiates by
     * default a {@code GameTeam} with this {@code GameContext} and the
     * specified color. Implementations may override this method to instantiate
     * a specialized subclass of {@code GameTeam}.
     * @param color the color of the team to create. Must not be a format and
     *              must not be equal to {@link ChatColor#RESET}.
     * @return a newly created {@code GameTeam} with the specified color and
     *         this {@code GameContext}.
     */
    protected GameTeam makeTeam(ChatColor color) {
	return new GameTeam(this, color);
    }

    /**
     * Creates a {@code Listener} that will remove players from their teams when
     * they disconnect and optionally other tasks, or {@code null} if no
     * listener should be registered. Returns by default a
     * {@code DefaultGameListener}.
     * @return a {@code Listener} that will remove players from their teams, or
     *         {@code null} if a listener is provided externally, or if no
     *         listener should be created.
     */
    protected Listener makeListener() {
	return new DefaultGameListener(this);
    }

    // -----------------------=[ Settings ]=-----------------------
    /**
     * Returns whether or not players may hurt other teammates.
     * @return {@code true} if friendly fire is allowed, {@code false}
     *         otherwise.
     */
    public boolean hasFriendlyFire() {
	return allowFriendlyFire;
    }

    /**
     * Sets whether or not player may hurt other teammates. The setting will
     * apply to all teams in this {@code GameContext}.
     * @param allowFriendlyFire {@code true} to allow friendly fire,
     *                          {@code false} to disable.
     */
    public void setFriendlyFire(boolean allowFriendlyFire) {
	if (this.allowFriendlyFire == allowFriendlyFire) {
	    return;
	}
	this.allowFriendlyFire = allowFriendlyFire;
	for (GameTeam team : teams.values()) {
	    team.getBukkitTeam().setAllowFriendlyFire(allowFriendlyFire);
	}
    }

    /**
     * Returns whether or not players may see other invisible teammates (will
     * appear transluscent).
     * @return {@code true} if the players can see invisible teammates,
     *         {@code false} otherwise.
     */
    public boolean canSeeFriendlyInvisibles() {
	return seeFriendlyInvisibles;
    }

    /**
     * Sets wether or not players will be able to see other invisible teammates.
     * The setting will apply to all teams in this {@code GameContext}.
     * @param seeFriendlyInvisibles {@code true} to allow players to see
     *                              invisible teammates, {@code false}
     *                              otherwise.
     */
    public void setCanSeeFriendlyInvisibles(boolean seeFriendlyInvisibles) {
	if (this.seeFriendlyInvisibles == seeFriendlyInvisibles) {
	    return;
	}

	this.seeFriendlyInvisibles = seeFriendlyInvisibles;
	for (GameTeam team : teams.values()) {
	    team.getBukkitTeam().setCanSeeFriendlyInvisibles(seeFriendlyInvisibles);
	}
    }

    /**
     * Returns the current {@code NameTagVisibility}, which defines whether or
     * not players will be able to see other players' nametags.
     * @return the {@code NameTagVisibility} of this {@code GameContext}.
     */
    public NameTagVisibility getNameTagVisibility() {
	return nameTagVisibility;
    }

    /**
     * Sets the {@code NameTagVisibility} for this {@code GameContext}.
     * @param nameTagVisibility the new {@code NameTagVisibility} to set. Must
     *                          not be {@code null}.
     * @throws NullPointerException if {@code nameTagVisibility} is
     *                              {@code null}.
     */
    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) {
	if (this.nameTagVisibility == nameTagVisibility) {
	    return;
	}

	this.nameTagVisibility = checkNotNull(nameTagVisibility);
	for (GameTeam team : teams.values()) {
	    team.getBukkitTeam().setNameTagVisibility(nameTagVisibility);
	}
    }

    /**
     * Applies the friendly fire, can see friendly invisibles and nametag
     * visibility settings, and also updates the display names for each team.
     * This method is not required to call {@link GameTeam#applySettings()} and
     * doesn't, by default.
     * @see GameTeam#applySettings()
     */
    public void applySettings() {
	boolean friendlyFire = allowFriendlyFire;
	boolean seeInvisibles = seeFriendlyInvisibles;
	NameTagVisibility nametag = nameTagVisibility;

	for (GameTeam team : teams.values()) {
	    Team bukkitTeam = team.getBukkitTeam();
	    bukkitTeam.setNameTagVisibility(nametag);
	    bukkitTeam.setAllowFriendlyFire(friendlyFire);
	    bukkitTeam.setCanSeeFriendlyInvisibles(seeInvisibles);
	    bukkitTeam.setDisplayName(team.getDisplayName());
	}
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "{" + "teams=" + teams + ", players=" + GameUtils.formatPlayers(players.keySet(), true) + '}';
    }

}
