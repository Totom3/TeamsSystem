package io.github.totom3.teamsystem;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Totom3
 */
public class GameUtils {

    /**
     * Checks that a {@code Player} is valid for joining teams. Currently only
     * insures {@code player} is not {@code null}.
     * @param player the player to check.
     * @return the player passed as argument.
     * @throws NullPointerException if {@code player} is {@code null}.
     */
    public static Player checkPlayer(Player player) {
	return checkNotNull(player, "Player cannot be null");
    }

    /**
     * Checks that a {@code ChatColor} is valid as team colors. Currently
     * insures that it is not {@code null}, is not a format, and is not equal to
     * {@link ChatColor#RESET}.
     * @param color the {@code ChatColor} to check.
     * @return the color passed as argument.
     */
    public static ChatColor checkColor(ChatColor color) {
	if (color == null) {
	    throw new NullPointerException("Color cannot be null");
	}
	if (color.isFormat()) {
	    throw new IllegalArgumentException("Expected color, got format instead");
	}
	if (color == ChatColor.RESET) {
	    throw new IllegalArgumentException("Color cannot be RESET (use WHITE instead)");
	}
	return color;
    }

    /**
     * Prints the contents of a {@code Set} of {@code Player}s using each
     * player's name. The returned string is formatted as following:
     * <pre>(color2) player-name(color1), (color2) player-name(color1), (color2) player-name(color1)]</pre>
     * @param players the {@code Set} to format. Must not be {@code null} and
     *                must not contain any {@code null} elements.
     * @param color1  the color to use for the commas. Must not be {@code null}.
     * @param color2  the color to use for the names. Must not be {@code null}.
     * @return a formatted string containing the names of the players containing
     *         in a set.
     * @throws NullPointerException if either {@code color1}, {@code color2} or
     *                              {@code players} is {@code null}, or if
     *                              {@code players} contains one or more
     *                              {@code null} elements.
     */
    public static String formatPlayers(Set<Player> players, ChatColor color1, ChatColor color2) {
	String strColor1 = color1.toString();
	String strColor2 = color2.toString();

	StringBuilder builder = new StringBuilder();

	Iterator<Player> it = players.iterator();
	if (!it.hasNext()) {
	    return "";
	}

	builder.append(strColor2).append(it.next().getName());
	for (;;) {
	    if (!it.hasNext()) {
		return builder.toString();
	    }

	    builder.append(strColor1).append(", ").append(strColor2).append(it.next().getName());
	}
    }

    /**
     * Prints the contents of a {@code Set} of {@code Player}s using each
     * player's name. The returned string is formatted as following:
     * <pre>With brackets: [player-name, player-name, player-name]<br/>Without brackets: player-name, player-name, player-name</pre>
     * @param players  the {@code Set} to format. Must not be {@code null} and
     *                 must not contain any {@code null} elements.
     * @param brackets whether or not to use square brackets.
     * @return
     */
    public static String formatPlayers(Set<Player> players, boolean brackets) {
	StringBuilder builder = new StringBuilder();

	Iterator<Player> it = players.iterator();
	if (!it.hasNext()) {
	    return (brackets) ? "[]" : "";
	}

	if (brackets) {
	    builder.append('[');
	}

	builder.append(it.next().getName());
	for (;;) {
	    if (!it.hasNext()) {
		if (brackets) {
		    builder.append(']');
		}
		return builder.toString();
	    }

	    builder.append(", ").append(it.next().getName());
	}
    }

    private GameUtils() {
	throw new AssertionError("no GameUtils instance for you! :P");
    }

}
