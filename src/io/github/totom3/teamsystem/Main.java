package io.github.totom3.teamsystem;

import io.github.totom3.teamsystem.demo.InfectedGameCommandExecutor;
import io.github.totom3.teamsystem.demo.InfectedGameContext;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Totom3
 */
public class Main extends JavaPlugin {

    private static Main instance;

    public static Main get() {
	if (instance == null) {
	    throw new IllegalStateException("plugin not initialized yet");
	}
	return instance;
    }

    public Main() {
	instance = this;
    }

    @Override
    public void onEnable() {
	// ----------=[ Demo Infected Teams Initialization ]=----------
	// (not related to API)
	
	InfectedGameContext context = new InfectedGameContext();
	getCommand("infected").setExecutor(new InfectedGameCommandExecutor(context));
    }
    
    
}
