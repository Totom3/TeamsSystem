package io.github.totom3.teamsystem.demo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Totom3
 */
public class InfectedGameCommandExecutor implements CommandExecutor {

    private final InfectedGameContext context;

    public InfectedGameCommandExecutor(InfectedGameContext context) {
	this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (args.length == 0) {
	    return false;
	}

	String subCommand = args[0].toLowerCase();
	switch (subCommand) {
	    case "join-s":
		return join(sender, args, true);
	    case "join-z":
		return join(sender, args, false);
	    case "leave":
		return leave(sender, args);
	    case "info":
		info(sender, false);
		break;
	    case "info->c":
		info(sender, true);
		break;
	    default:
		return false;
	}

	return true;
    }

    private boolean join(CommandSender sender, String[] args, boolean survivors) {
	Player target;
	switch (args.length) {
	    case 1:
		if (!(sender instanceof Player)) {
		    sender.sendMessage(ChatColor.RED + "You must be a player, or you must precise the name of a player, in order to use this command.");
		    return true;
		}
		target = (Player) sender;
		break;
	    case 2:
		String name = args[1];
		target = Bukkit.getPlayer(name);
		if (target == null) {
		    sender.sendMessage(ChatColor.RED + "No such player '" + name + "'");
		    return true;
		}
		break;
	    default:
		return false;
	}

	InfectedGameTeam team = (survivors) ? context.getSurvivorsTeam() : context.getZombiesTeam();
	team.addPlayer(target);
	sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " to " + ((survivors) ? "survivors" : "zombies") + " team!");
	return true;
    }

    private void info(CommandSender sender, boolean console) {
	CommandSender target = (console) ? Bukkit.getConsoleSender() : sender;
	context.printInfo(target);
    }

    private boolean leave(CommandSender sender, String[] args) {
	Player target;
	switch (args.length) {
	    case 1:
		if (!(sender instanceof Player)) {
		    sender.sendMessage(ChatColor.RED + "You must be a player, or you must precise the name of a player, in order to use this command.");
		    return true;
		}
		target = (Player) sender;
		break;
	    case 2:
		String name = args[1];
		target = Bukkit.getPlayer(name);
		if (target == null) {
		    sender.sendMessage(ChatColor.RED + "No such player '" + name + "'");
		    return true;
		}
		break;
	    default:
		return false;
	}

	if (context.setTeamOf(target, null) != null) {
	    sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " from their team!");
	}
	return true;
    }
}
