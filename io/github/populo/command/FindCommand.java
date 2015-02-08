package io.github.populo.command;

import io.github.populo.FindBlock;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindCommand implements CommandExecutor {

	public FindBlock plugin;

	public FindCommand(FindBlock plugin) {
		this.plugin = plugin;
	}

	int maxRadius = FindBlock.config.getInt("maxRadius");
	public List<String> def = FindBlock.config.getStringList("blacklist");
	public Material searchedBlock;
	public String searchedName, blockName, blockLocalName;
	Material blackBlock;

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
	
	if (sender instanceof Player) {
		int found = 0, blocked = 0;
		Player player = (Player) sender;
		World world = player.getWorld();
		boolean blacklisted = false;
		
			if (args.length == 0) {
				return false;
			} else {
				int radius = 25;
				
				if (args.length >= 2 && !args[0].equalsIgnoreCase("blacklist")) {
                    try {
                    	if (Integer.parseInt(args[1]) <= maxRadius) {
                            radius = Integer.parseInt(args[1]);
                    	} else {
                    		sender.sendMessage(ChatColor.RED + args[1] + " is above the maximum radius. Maximum radius is: " + maxRadius);
                    	}
                    } catch (NumberFormatException ex) {}
                }
				
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("blacklist")) {
						if (def.isEmpty()) {
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "There are no blocks in the blacklist");
							return true;
						} else {
							for (int i = 0; i < def.size(); i++) {
								if (Material.getMaterial(def.get(i).toUpperCase()) instanceof Material && Material.getMaterial(def.get(i).toUpperCase()).isBlock()) {
									blackBlock = Material.getMaterial(def.get(i).toUpperCase());
									sender.sendMessage(ChatColor.GOLD + blackBlock.name() + "(" + blackBlock.ordinal() + ")");
									blocked++;
								} else {
									sender.sendMessage(def.get(i) + " is not a valid block name");
								}
							}
							sender.sendMessage(ChatColor.AQUA + "" + blocked + " blocks on the blacklist");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("add")) {
						if (args.length > 1) {
							if (player.isOp()) {
								if (Material.getMaterial(args[1].toUpperCase()) instanceof Material) { //&& Material.getMaterial(args[1].toUpperCase()).isBlock()) {
									for (int i = 0; i < def.size(); i++) {
										if (Material.getMaterial(def.get(i).toUpperCase()) instanceof Material) {
											if (Material.getMaterial(args[1].toUpperCase()) == Material.getMaterial(def.get(i).toUpperCase())) {
												sender.sendMessage(ChatColor.RED + args[1] + " is already on the blacklist");
												return true;
											}
										}
									}
									def.add(args[1]);
									FindBlock.config.set("blacklist", def);
									try {
										FindBlock.config.save(FindBlock.configFile);
									} catch (Exception e) {}
									sender.sendMessage(ChatColor.GREEN + args[1] + " added to blacklist.");
									return true;
								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " is not a valid block.");
									return true;
								}
							} else {
								sender.sendMessage(ChatColor.RED + "You must be an op to do this.");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please specify a block.");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("remove")) {
						if (args.length > 1) {
							if (player.isOp()) {
								if (Material.getMaterial(args[1].toUpperCase()) instanceof Material && Material.getMaterial(args[1].toUpperCase()).isBlock()) {
									for (int i = 0; i < def.size(); i++) {
										if (Material.getMaterial(def.get(i).toUpperCase()) instanceof Material) {
											if (Material.getMaterial(args[1].toUpperCase()) == Material.getMaterial(def.get(i).toUpperCase())) {
												def.remove(args[1]);
												FindBlock.config.set("blacklist", def);
												try {
													FindBlock.config.save(FindBlock.configFile);
												} catch (Exception e) {}
												sender.sendMessage(ChatColor.GREEN + args[1] + " removed.");
												return true;
											}
										}
									}
									sender.sendMessage(ChatColor.RED + args[1] + " is not on the blacklist.");
									return true;
								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " is not a valid block.");
									return true;
								}
							} else {
								sender.sendMessage(ChatColor.RED + "You must be an op to do this.");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please specify a block.");
							return true;
						}
					} else {
						searchedName = args[0].toUpperCase();
						if (Material.getMaterial(searchedName) instanceof Material && Material.getMaterial(searchedName).isBlock()) {
							searchedBlock = Material.getMaterial(searchedName);
							blockLocalName = searchedBlock.toString();
							blockName = searchedBlock.name();
							
							for(int i = 0; i < def.size(); i++) {
								if (Material.getMaterial(def.get(i).toUpperCase()) instanceof Material) {
									blackBlock = Material.getMaterial(def.get(i).toUpperCase());
									
									if(blackBlock == searchedBlock) {
										blacklisted = true;
									}
								}
							}
							
							if (!blacklisted) {
								for (int x = -(radius); x < radius; x++) {
									for (int y = -(radius); y <= radius; y ++) {
										for (int z = -(radius); z <= radius; z ++) {
											if (searchedBlock == (world.getBlockAt((player.getLocation().getBlockX() + x), player.getLocation().getBlockY() + y, player.getLocation().getBlockZ() + z)).getType()) {
												sender.sendMessage(ChatColor.GRAY + "" + (player.getLocation().getBlockX() + x) + ", " +  (player.getLocation().getBlockY() + y) + ", " + (player.getLocation().getBlockZ() + z));
												found++;
											}
										}
									}
								}
								sender.sendMessage(ChatColor.GREEN + "" + found + " " + blockLocalName + "(" + searchedBlock.ordinal() + ") blocks found in a " + radius + " radius");
								return true;
							} else {
								sender.sendMessage(ChatColor.RED + searchedBlock.toString() + "(" + searchedBlock.ordinal() + ") is blacklisted");
								return true;
							}
							
						} else {
							sender.sendMessage(ChatColor.RED + searchedName + " is not a block");
							return true;
						}
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Sender must be player");
			return true;
		}
		return false;
	}
}