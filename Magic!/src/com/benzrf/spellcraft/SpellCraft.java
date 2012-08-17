package com.benzrf.spellcraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.benzrf.spellcraft.Runnables.SpellCraftRunnable;

public class SpellCraft extends JavaPlugin
{
	
	void addSpells()
	{
		addSpell("fireball", "[NETHERRACK]", 0);
		addSpell("snowball", "[SNOW_BLOCK]", 0);
		addSpell("confuse", "[RED_MUSHROOM, RED_MUSHROOM]", 2);
		addSpell("explodecreeper", "[LEAVES]", 3);
		addSpell("regen", "[BOOKSHELF, VINE]", 4);
		addSpell("freeze", "[GLASS]", 2);
		addSpell("explosion", "[TNT]", 1);
		addSpell("air", "[PUMPKIN]", 4);
		addSpell("derp", "[CAKE_BLOCK]", 3);
		addSpell("lightning", "[REDSTONE_TORCH_ON, REDSTONE_TORCH_ON, REDSTONE_WIRE]", 1);
		addSpell("food", "[MELON_BLOCK, PUMPKIN, SOIL]", 4);
		addSpell("fissure", "[GOLD_ORE, GOLD_ORE, IRON_ORE]", 3);
		addSpell("voodoo", "[NETHERRACK, OBSIDIAN, SOUL_SAND]", 2);
		addSpell("superjump/featherfall", "[LADDER, WOOL]", 4);
		addSpell("summonwolf", "[WOOL]", 3);
		addSpell("arrowshield", "[IRON_ORE]", 4);
		addSpell("grabitems", "[OBSIDIAN]", 0);
		addSpell("taunt", "[DOUBLE_STEP]", 4);
		addSpell("locator", "[IRON_ORE, REDSTONE_WIRE]", 4);
		addSpell("fusrodah", "[BOOKSHELF, SOUL_SAND]", 2);
		addSpell("meteorite", "[FIRE, FIRE, OBSIDIAN]", 1);
		addSpell("barrage", "[GRAVEL, LOG]", 1);
		addSpell("bunker", "[IRON_BLOCK]", 4);
		addSpell("snowstorm", "[SNOW_BLOCK, SNOW_BLOCK, SNOW_BLOCK]", 1);
		addSpell("heisalreadyhere", "[WOOD, WOOD]", 2);
		addSpell("raise", "[SOUL_SAND]", 1);
	}

	@Override
	public void onEnable()
	{
		addSpells();
		generateConfig();
		setFieldsAndGetConfigValues();
		loadPlayerData();
		
		getServer().getPluginManager().registerEvents(thelistener, this);
		getServer().getPluginManager().registerEvents(theotherlistener, this);
		
		if (getServer().getPluginManager().getPlugin("HawkEye") != null)
		{
			HEEnabled = true;
		}
	}
	
	@Override
	public void onDisable()
	{
		cancelRunningSpells();
		thelogger.info(prefix + "Shutting down...");
		try {
			File testificate = new File("plugins/" + pluginName + "/data");
			if (!testificate.isDirectory())
			{
				testificate.mkdirs();
			}
			theyaml.dump(playerMap, new FileWriter("plugins/" + pluginName + "/data/spells1"));
			theyaml.dump(playerMap2, new FileWriter("plugins/" + pluginName + "/data/spells2"));
			theyaml.dump(playerMap3, new FileWriter("plugins/" + pluginName + "/data/spells3"));
			theyaml.dump(playerMap4, new FileWriter("plugins/" + pluginName + "/data/spells4"));
			theyaml.dump(playerManaMap, new FileWriter("plugins/" + pluginName + "/data/mana"));
		}
		catch (IOException e)
		{
			thelogger.warning(prefix + "Could not save info! Get worried.");
		}
	}

	void cancelRunningSpells()
	{
		for (SpellCraftRunnable r : Spells.rList)
		{
			Player p = r.getCaster();
			if (!r.getSpellName().equals("fissure"))
			{
				p.sendMessage(ChatColor.GREEN + "Spell " + ChatColor.GOLD + r.getSpellName() + ChatColor.GREEN + " cancelled due to server restart!");
			}
			if (r.onStop() && p.getGameMode().equals(GameMode.SURVIVAL))
			{
				thelistener.refundMana(r.getSpellName(), p, true);
			}
		}
	}

	void generateConfig()
	{
		FileConfiguration c = getConfig();
		for (String s : spellCostMap.keySet())
		{
			if (c.getInt("manaCosts." + s, 0) == 0)
			{
				getConfig().set("manaCosts." + s, spellCostMap.get(s));
			}
		}
		
		String sacrifice = "";
		for (String s : spellBlockMap.keySet())
		{
			if (c.getString("sacrifices." + spellBlockMap.get(s)) == null)
			{
				for (String s2 : s.substring(1, s.length() - 1).split(", "))
				{
					sacrifice += " " + s2;
				}
				getConfig().set("sacrifices." + spellBlockMap.get(s), sacrifice.substring(1));
				sacrifice = "";
			}
		}
		
		if (c.getString("sacrificePhrase") == null) c.set("sacrificePhrase", sacrificePhrase);
		if (c.getString("permsLightning") == null) c.set("permsLightning", permsLightning);
		if (c.getString("fissurePlayerMessage") == null) c.set("fissurePlayerMessage", fissurePlayerMessage.replaceAll("§", "&"));
		if (c.getString("voodooPlayerMessage") == null) c.set("voodooPlayerMessage", voodooPlayerMessage.replaceAll("§", "&"));
		if (c.getString("spellDeniedMessage") == null) c.set("spellDeniedMessage", spellDeniedMessage.replaceAll("§", "&"));
		if (c.getString("fusrodahPlayerMessage") == null) c.set("fusrohdahPlayerMessage", fusrodahPlayerMessage.replaceAll("§", "&"));
		if (c.getString("sacrificeMessage") == null) c.set("sacrificeMessage", sacrificeMessage.replaceAll("§", "&"));
		saveConfig();
	}
	
	@SuppressWarnings("unchecked")
	void loadPlayerData()
	{
		try
		{
			playerMap = theyaml.loadAs(new FileReader("plugins/" + pluginName + "/data/spells1"), HashMap.class);
			playerMap2 = theyaml.loadAs(new FileReader("plugins/" + pluginName + "/data/spells2"), HashMap.class);
			playerMap3 = theyaml.loadAs(new FileReader("plugins/" + pluginName + "/data/spells3"), HashMap.class);
			playerMap4 = theyaml.loadAs(new FileReader("plugins/" + pluginName + "/data/spells4"), HashMap.class);
			playerManaMap = theyaml.loadAs(new FileReader("plugins/" + pluginName + "/data/mana"), HashMap.class);
		}
		catch (FileNotFoundException e)
		{
			thelogger.warning(prefix + "Could not load saved info! If this is your first time running " + pluginName + ", please ignore this message. Otherwise, get worried.");
		}
	}
	
	void setFieldsAndGetConfigValues()
	{
		pluginName = this.getDescription().getName();
		permsName = pluginName.toLowerCase().replaceAll(" ", "");
		prefix = "[" + pluginName + "] ";
		sacrificePhrase = getConfig().getString("sacrificePhrase", sacrificePhrase);
		permsLightning = getConfig().getBoolean("permsLightning", permsLightning);
		instance = this;
		
		fissurePlayerMessage = getConfig().getString("fissurePlayerMessage", fissurePlayerMessage).replaceAll("&", "§");
		voodooPlayerMessage = getConfig().getString("voodooPlayerMessage", voodooPlayerMessage).replaceAll("&", "§");
		fusrodahPlayerMessage = getConfig().getString("fusrodahPlayerMessage", fusrodahPlayerMessage).replaceAll("&", "§");
		spellDeniedMessage = getConfig().getString("spellDeniedMessage", spellDeniedMessage).replaceAll("&", "§");
		sacrificeMessage = getConfig().getString("sacrificeMessage", sacrificeMessage).replaceAll("&", "§");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (commandLabel.equalsIgnoreCase("listspells"))
		{
			if (!(sender.isOp() || sender.hasPermission(permsName + ".admin")))
			{
				return false;
			}
			String toSend = "";
			for (String s : spellCostMap.keySet())
			{
				toSend += s + ", ";
			}
			toSend = toSend.substring(0, toSend.length() - 2);
			sender.sendMessage(ChatColor.LIGHT_PURPLE + toSend);
			return true;
		}
		if (!(sender.hasPermission(permsName + ".command") || sender.isOp()))
		{
			return false;
		}
		if (args.length == 0)
		{
			if (sender.getName().equalsIgnoreCase("CONSOLE"))
			{
				return false;
			}
			String mana = Integer.toString(getMana((Player) sender));
			sender.sendMessage(ChatColor.GREEN + "You have " + ChatColor.GOLD + mana + ChatColor.GREEN + " mana.");
			sender.sendMessage(ChatColor.GREEN + "Your current block-targeting spell is " + ChatColor.GOLD + (playerMap.get(sender.getName()) == null ? "none" : playerMap.get(sender.getName())) + ChatColor.GREEN + ".");
			sender.sendMessage(ChatColor.GREEN + "Your current self-targeting spell is " + ChatColor.GOLD + (playerMap4.get(sender.getName()) == null ? "none" : playerMap4.get(sender.getName())) + ChatColor.GREEN + ".");
			sender.sendMessage(ChatColor.GREEN + "Your current short-range entity-targeting spell is " + ChatColor.GOLD + (playerMap2.get(sender.getName()) == null ? "none" : playerMap2.get(sender.getName())) + ChatColor.GREEN + ".");
			sender.sendMessage(ChatColor.GREEN + "Your current long-range entity-targeting spell is " + ChatColor.GOLD + (playerMap3.get(sender.getName()) == null ? "none" : playerMap3.get(sender.getName())) + ChatColor.GREEN + ".");
		}
		else if (args.length == 3 && (sender.isOp() || sender.hasPermission(permsName + ".admin")))
		{
			if (args[0].equalsIgnoreCase("setspell"))
			{
				setSpell(sender, args);
			}
			else if (args[0].equalsIgnoreCase("givemana"))
			{
				if (sender.getServer().matchPlayer(args[1]).size() == 0)
				{
					sender.sendMessage(ChatColor.RED + pluginName + " couldn't find anybody named " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.RED + ".");
				}
				else
				{
					try
					{
						Integer.parseInt(args[2]);
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.RED + " is not a number!");
						return true;
					}
					
					Player p = sender.getServer().matchPlayer(args[1]).get(0);
					int add = Integer.parseInt(args[2]);
					int current;
					try
					{
						current = Integer.parseInt(playerManaMap.get(p.getName()));
					}
					catch (NumberFormatException e)
					{
						current = 0;
					}
					playerManaMap.put(p.getName(), Integer.toString(current + add));
					sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + " now has " + ChatColor.GOLD + Integer.toString(current + add) + ChatColor.GREEN + " mana!");
				}
			}
			else if (args[0].equalsIgnoreCase("setmana"))
			{
				if (sender.getServer().matchPlayer(args[1]).size() == 0)
				{
					sender.sendMessage(ChatColor.RED + pluginName + " couldn't find anybody named " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.RED + ".");
				}
				else
				{
					try
					{
						Integer.parseInt(args[2]);
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.RED + " is not a number!");
						return true;
					}
					Player p = sender.getServer().matchPlayer(args[1]).get(0);
					int newMana = Integer.parseInt(args[2]);
					playerManaMap.put(p.getName(), Integer.toString(newMana));
					sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + " now has " + ChatColor.GOLD + Integer.toString(newMana) + ChatColor.GREEN + " mana!");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "/" + permsName + " {info|setspell|givemana|setmana}");
			}
		}
		else if (args.length == 2 && (sender.isOp() || sender.hasPermission(permsName + ".admin")))
		{
			if (args[0].equalsIgnoreCase("info"))
			{
				Player p;
				if (sender.getServer().matchPlayer(args[1]).size() == 0)
				{
					sender.sendMessage(ChatColor.RED + pluginName + " couldn't find anybody named " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.RED + ".");
					return true;
				}
				else
				{
					p = sender.getServer().matchPlayer(args[1]).get(0);
				}
				String mana = Integer.toString(getMana(p));
				sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + " has " + ChatColor.GOLD + mana + ChatColor.GREEN + " mana.");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + "'s current block-targeting spell is " + ChatColor.GOLD + (playerMap.get(p.getName()) == null ? "none" : playerMap.get(p.getName())) + ChatColor.GREEN + ".");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + "'s current self-targeting spell is " + ChatColor.GOLD + (playerMap4.get(p.getName()) == null ? "none" : playerMap4.get(p.getName())) + ChatColor.GREEN + ".");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + "'s current short-range entity-targeting spell is " + ChatColor.GOLD + (playerMap2.get(p.getName()) == null ? "none" : playerMap2.get(p.getName())) + ChatColor.GREEN + ".");
				sender.sendMessage(ChatColor.LIGHT_PURPLE + p.getName() + ChatColor.GREEN + "'s current long-range entity-targeting spell is " + ChatColor.GOLD + (playerMap3.get(p.getName()) == null ? "none" : playerMap3.get(p.getName())) + ChatColor.GREEN + ".");
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "/" + permsName + " {info|setspell|givemana|setmana}");
			}
		}
		else
		{
			if (!(sender.isOp() || sender.hasPermission(permsName + ".admin")))
			{
				sender.sendMessage(ChatColor.RED + "/" + permsName);
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "/" + permsName + " {info|setspell|givemana|setmana}");
			}
		}
		return true;
	}
	
	public void setSpell(CommandSender sender, String[] args)
	{
		args[2] = args[2].toLowerCase();
		if (sender.getServer().matchPlayer(args[1]).size() == 0)
		{
			sender.sendMessage(ChatColor.RED + pluginName + " couldn't find anybody named " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.RED + ".");
		}
		else if (args[2].equalsIgnoreCase("none"))
		{
			playerMap.put(this.getServer().matchPlayer(args[1]).get(0).getName(), "none");
			playerMap2.put(this.getServer().matchPlayer(args[1]).get(0).getName(), "none");
			playerMap3.put(this.getServer().matchPlayer(args[1]).get(0).getName(), "none");
			playerMap4.put(this.getServer().matchPlayer(args[1]).get(0).getName(), "none");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + sender.getServer().matchPlayer(args[1]).get(0).getName() + ChatColor.GREEN + "'s current spells have been set to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + ".");
		}
		else if (!spellCostMap.containsKey(args[2]))
		{
			sender.sendMessage(ChatColor.RED + pluginName + " couldn't find a spell called " + ChatColor.GOLD + args[2] + ChatColor.RED + ".");
		}
		else
		{
			byte type = spellTypeMap.get(args[2].toLowerCase());
			switch (type)
			{
			case 2:
				playerMap2.put(this.getServer().matchPlayer(args[1]).get(0).getName(), args[2]);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + sender.getServer().matchPlayer(args[1]).get(0).getName() + ChatColor.GREEN + "'s current short-range entity-targeting spell has been set to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + ".");
				break;
			case 3:
				playerMap3.put(this.getServer().matchPlayer(args[1]).get(0).getName(), args[2]);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + sender.getServer().matchPlayer(args[1]).get(0).getName() + ChatColor.GREEN + "'s current long-range entity-targeting spell has been set to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + ".");
				break;
			case 4:
				playerMap4.put(this.getServer().matchPlayer(args[1]).get(0).getName(), args[2]);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + sender.getServer().matchPlayer(args[1]).get(0).getName() + ChatColor.GREEN + "'s current self-targeting spell has been set to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + ".");
				break;
			default:
				playerMap.put(this.getServer().matchPlayer(args[1]).get(0).getName(), args[2]);
				sender.sendMessage(ChatColor.LIGHT_PURPLE + sender.getServer().matchPlayer(args[1]).get(0).getName() + ChatColor.GREEN + "'s current block-targeting spell has been set to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + ".");
				break;
			}
		}
	}
	
	public void setSpell(String[] args)
	{
		args[1] = args[1].toLowerCase();
		if (this.getServer().matchPlayer(args[0]).size() == 0)
		{
			return;
		}
		else if (!spellCostMap.containsKey(args[1]))
		{
			return;
		}
		else
		{
			byte type = spellTypeMap.get(args[1]);
			switch (type)
			{
			case 2:
				playerMap2.put(this.getServer().matchPlayer(args[0]).get(0).getName(), args[1]);
				break;
			case 3:
				playerMap3.put(this.getServer().matchPlayer(args[0]).get(0).getName(), args[1]);
				break;
			case 4:
				playerMap4.put(this.getServer().matchPlayer(args[0]).get(0).getName(), args[1]);
				break;
			default:
				playerMap.put(this.getServer().matchPlayer(args[0]).get(0).getName(), args[1]);
				break;
			}
		}
	}
	
	public void addSpell(String name, String blockCost, int type)
	{
		spellCostMap.put(name, getConfig().getInt("manaCosts." + name, 0));
		spellBlockMap.put(getBlockCost(name, blockCost), name);
		spellTypeMap.put(name, (byte) type);
	}

	String getBlockCost(String name, String blockCost)
	{
		if (getConfig().getString("sacrifices." + name) != null)
		{
			List<String> alphabetizer = new ArrayList<String>();
			for (String s : getConfig().getString("sacrifices." + name).split(" "))
			{
				alphabetizer.add(s.toUpperCase());
			}
			Collections.sort(alphabetizer);
			blockCost = alphabetizer.toString();
		}
		return blockCost;
	}

	public void addMana(Player p, int add)
	{
		playerManaMap.put(p.getName(), Integer.toString(getMana(p) + add));
	}
	
	public int getMana(Player p)
	{
		int parsedMana;
		if (playerManaMap.containsKey(p.getName()))
		{
			String unparsedMana = playerManaMap.get(p.getName());
			try
			{
				parsedMana = Integer.parseInt(unparsedMana);
			}
			catch (NumberFormatException e)
			{
				parsedMana = 0;
			}
		}
		else
		{
			parsedMana = 0;
		}
		return parsedMana;
	}
	
	public String pluginName;
	public String permsName;
	public String prefix;
	public boolean HEEnabled = false;
	Map<String, String> playerMap = new HashMap<String, String>();
	Map<String, String> playerMap2 = new HashMap<String, String>();
	Map<String, String> playerMap3 = new HashMap<String, String>();
	Map<String, String> playerMap4 = new HashMap<String, String>();
	Map<String, String> playerManaMap = new HashMap<String, String>();
	final Map<String, Integer> spellCostMap = new HashMap<String, Integer>();
	final Map<String, Byte> spellTypeMap = new HashMap<String, Byte>();
	final Map<String, String> spellBlockMap = new HashMap<String, String>();
	SpellCraftPlayerListener thelistener = new SpellCraftPlayerListener(this);
	SpellCraftEntityListener theotherlistener = new SpellCraftEntityListener(this);
	Logger thelogger = Logger.getLogger("Minecraft");
	Yaml theyaml = new Yaml();
	public String sacrificePhrase = "sacrifice";
	public boolean permsLightning = true;
	public String fissurePlayerMessage = "&4THOU ART NOT WORTHY OF THE POWER TO USE FISSURE UPON THY FELLOW MINERS.";
	public String voodooPlayerMessage = "&4THOU ART NOT WORTHY OF USING THE MIGHT OF VOODOO UPON THY FELLOW MINERS.";
	public String spellDeniedMessage = "&4THOU ART NOT WORTHY OF THE POWER OF %SPELL.";
	public String fusrodahPlayerMessage = "&4THOU ART NOT WORTHY OF USING THE FORCE OF FUS RO DAH UPON THY FELLOW MINERS.";
	public String sacrificeMessage = "&4I ACCEPT THY SACRIFICE.";
	
	public static SpellCraft instance; 
}