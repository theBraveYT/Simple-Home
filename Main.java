package home;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		this.getDataFolder().mkdirs();
		try {
			File dataFile = new File(this.getDataFolder().getPath()+"/data.txt");
			if(dataFile.exists()==false) {
				dataFile.createNewFile();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		readData();
	}
	
	@Override
	public void onDisable() {
		saveData();
	}
	
	public void readData() {
		File dataFile = new File(this.getDataFolder().getPath()+"/data.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String line = "a";
			ArrayList<String> data = new ArrayList<String>();
			while(line!=null) {
				line = br.readLine();
				if(line!=null) {
					data.add(line);
				}
			}
			for(String s : data) {
				String[] words1 = s.split(" ");
				ArrayList<String> words = new ArrayList<String>();
				for(String s1 : words1) {
					words.add(s1);
				}
				Location loc = new Location(Bukkit.getWorld(words.get(1)), Integer.valueOf(words.get(2)), Integer.valueOf(words.get(3)), Integer.valueOf(words.get(4)));
				homes.put(words.get(0), loc);
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveData() {
		File dataFile = new File(this.getDataFolder().getPath()+"/data.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
			for(Entry<String, Location> e : homes.entrySet()) {
				Location loc = e.getValue();
				bw.write(e.getKey() + " " + loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
				bw.newLine();
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	HashMap<String, Location> homes = new HashMap<String, Location>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			  sender.sendMessage(ChatColor.RED + "Only players may use this command!");
			  return true;
		}
		Player p = (Player) sender;
		String uuid = p.getUniqueId().toString();
		Location loc = p.getLocation();
		if(label.equalsIgnoreCase("sethome")) {
			if(!containsUUID(uuid)) {
				homes.put(uuid, loc);
			}
			else {
				homes.replace(uuid, loc);
			}
			p.sendMessage(ChatColor.GREEN + "Set your home location to " + loc.getBlockX() + " " + loc.getBlockY()
			+ " " + loc.getBlockZ() + " in world " + loc.getWorld().getName());
		}
		if(label.equalsIgnoreCase("home")) {
			p.sendMessage(ChatColor.GOLD + "Teleporting you to your home in 5 seconds, don't move!");
			int blockX = loc.getBlockX();
			int blockY = loc.getBlockY();
			int blockZ = loc.getBlockZ();
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					Location loc2 = p.getLocation();
					int x = loc2.getBlockX();
					int y = loc2.getBlockY();
					int z = loc2.getBlockZ();
					boolean shouldContinue = false;
					if(x==blockX) {
						if(y == blockY) {
							if(z == blockZ) {
								shouldContinue = true;
							}
						}
					}
					if(shouldContinue) {
						p.sendMessage(ChatColor.GREEN + "Teleporting...");
						if(containsUUID(uuid)) {
							Location homeLoc = homes.get(uuid);
							p.teleport(homeLoc);
							p.sendMessage(ChatColor.GOLD + "Welcome to your home!");
						}
						else {
							p.sendMessage(ChatColor.RED + "You don't have a home!");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + "You moved! Teleportation cancelled!");
					}
				}
			},100);
		}
		return false;
	}
	
	
	public boolean containsUUID(String uuid) {
		boolean value = false;
		for(Entry<String, Location> e : homes.entrySet()) {
			if(e.getKey().equals(uuid)) {
				value = true;
				break;
			}
		}
		return value;
	}
}
