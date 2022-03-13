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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

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
		Bukkit.getPluginManager().registerEvents(this, this);
		readData();
	}
	
	@Override
	public void onDisable() {
		saveData();
	}
	
	@EventHandler
	public void onPlayerDeath(EntityDeathEvent event) {
		if(event.getEntityType().equals(EntityType.PLAYER)) {
			Player p = (Player) event.getEntity();
			p.performCommand("sethome death");
		}
	}
	
	String STOP = "STOP";
	
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
				HashMap<String,Location> HOMES = new HashMap<String,Location>();
				String UUID = words.get(0);
				for(int i = 1; i < words.size(); i = i + 0) {
					if(words.get(i).equals(STOP) || words.get(i+1).equals(STOP) || words.get(i+2).equals(STOP) || words.get(i+3).equals(STOP) || words.get(i+4).equals(STOP)) {
						break;
					}
					else {
						Location loc = new Location(Bukkit.getWorld(words.get(i+1)), Integer.valueOf(words.get(i+2)), Integer.valueOf(words.get(i+3)), Integer.valueOf(words.get(i+4)));
						HOMES.put(words.get(i), loc);
						i = i + 5;
					}
				}
				homes.put(UUID, HOMES);
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
			dataFile.delete();
			dataFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
			for(Entry<String, HashMap<String, Location>> e : homes.entrySet()) {
				String toWrite = "";
				toWrite = toWrite + e.getKey() + " ";
				for(Entry<String, Location> entry : e.getValue().entrySet()) {
					Location loc = entry.getValue();
					toWrite = toWrite + entry.getKey() + " " + loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " ";
				}
				bw.write(toWrite + " " + STOP);
				bw.newLine();
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	HashMap<String, HashMap<String,Location>> homes = new HashMap<String, HashMap<String,Location>>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			  sender.sendMessage(ChatColor.RED + "Only players may use this command!");
			  return true;
		}
		Player p = (Player) sender;
		String uuid = p.getUniqueId().toString();
		Location loc = p.getLocation();
		if(homes.get(uuid)==null||homes.get(uuid).isEmpty()) {
			homes.put(uuid, new HashMap<String,Location>());
		}
		if(label.equalsIgnoreCase("homes")) {
			p.sendMessage(ChatColor.GOLD + "Home data formatting:");
			p.sendMessage(ChatColor.GOLD + "<home-name> " + ChatColor.GREEN + "<world-name> " + ChatColor.BLUE + "<x> <y> <z>");
			for(Entry<String, HashMap<String, Location>> e : homes.entrySet()) {
				if(e.getKey().equals(uuid)) {
					for(Entry<String,Location> entry : e.getValue().entrySet()) {
						p.sendMessage(ChatColor.GOLD + entry.getKey() + " " + ChatColor.GREEN + entry.getValue().getWorld().getName() + " " + 
								ChatColor.BLUE + entry.getValue().getBlockX() + " " + entry.getValue().getBlockY() + " " + entry.getValue().getBlockZ());
					}
				}
 			}
			p.sendMessage(ChatColor.GREEN + "Listed all homes successfully!");
			return true;
		}
		else if(args.length!=1) {
			  p.sendMessage(ChatColor.RED + "You need to specify one home you want to set, delete or go to! Do /homes to see a list!");
			  return true;
		}
		else if(label.equalsIgnoreCase("sethome")) {
			String homeName = args[0];
			HashMap<String,Location> HOMES = homes.get(uuid);
			if(HOMES==null) {
				HashMap<String, Location> hash = new HashMap<String,Location>();
				homes.put(uuid, hash);
				HOMES = hash;
			}
			if(containsHome(homeName, HOMES)) {
				HOMES.replace(homeName, loc);
			}
			else {
				HOMES.put(homeName, loc);
			}
			homes.replace(uuid, HOMES);
			p.sendMessage(ChatColor.GREEN + "Set home " + homeName + " to the location: " + 
					loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
			return true;
		}
		else if(label.equalsIgnoreCase("delhome")) {
			String homeName = args[0];
			HashMap<String,Location> HOMES = homes.get(uuid);
			if(HOMES==null) {
				HashMap<String, Location> hash = new HashMap<String,Location>();
				homes.put(uuid, hash);
				HOMES = hash;
			}
			HOMES.remove(homeName);
			homes.replace(uuid, HOMES);
			p.sendMessage(ChatColor.GREEN + "Deleted home " + homeName);
		}
		else if(label.equalsIgnoreCase("home")) {
			String homeName = args[0];
			p.sendMessage(ChatColor.GOLD + "Teleporting you to home " + homeName + " in 5 seconds - don't move!");
			int X = loc.getBlockX();
			int Y = loc.getBlockY();
			int Z = loc.getBlockZ();
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(ChatColor.GOLD + "Telporting...");
					Location loc2 = p.getLocation();
					int x = loc2.getBlockX();
					int y = loc2.getBlockY();
					int z = loc2.getBlockZ();
					if(X==x&&Y==y&&Z==z) {
						HashMap<String,Location> HOMES = homes.get(uuid);
						if(HOMES==null) {
							HashMap<String, Location> hash = new HashMap<String,Location>();
							homes.put(uuid, hash);
							HOMES = hash;
						}
						Location homeLoc = HOMES.get(homeName);
						if(homeLoc==null) {
							p.sendMessage(ChatColor.RED + "That home doesn't exist!");
						}
						else {
							p.teleport(homeLoc);
							p.sendMessage(ChatColor.GREEN + "Welcome to " + homeName + "!");
						}
					}
					else {
						p.sendMessage(ChatColor.RED + "You moved!");
					}
				}
			},100);
		}
		return false;
	}
	
	
	public boolean containsUUID(String string) {
		boolean value = false;
		for(Entry<String, HashMap<String, Location>> e : homes.entrySet()) {
			if(e.getKey().equals(string)) {
				value = true;
				break;
			}
		}
		return value;
	}
	public boolean containsHome(String string, HashMap<String,Location> HOMES) {
		boolean value = false;
		for(Entry<String, Location> e : HOMES.entrySet()) {
			if(e.getKey().equals(string)) {
				value = true;
				break;
			}
		}
		return value;
	}
	
	//p.sendMessage(ChatColor.GOLD + "Do /homes to see a list of your homes, /delhome to delete it, /sethome to overwrite or /home " + homeName + " to go there!");

}
