package net.ryanhecht.uuidconverter;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {
	boolean on=false;
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable(){
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("convertfiles")) {
			if(sender instanceof ConsoleCommandSender) {
				on=true;
				try {
					Main.main();
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
			else {
				sender.sendMessage("Please use from console.");
			}
		}
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		//e.setJoinMessage("");
		if(on)
		e.getPlayer().kickPlayer("File conversion in progress.");
	}
}
