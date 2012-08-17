package com.benzrf.spellcraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.benzrf.spellcraft.Runnables.ArrowshieldRunnable;
import com.benzrf.spellcraft.Runnables.FeatherfallRunnable;
import com.benzrf.spellcraft.Runnables.FissureRunnable;
import com.benzrf.spellcraft.Runnables.FreezeRunnable;
import com.benzrf.spellcraft.Runnables.HeisalreadyhereRunnable;
import com.benzrf.spellcraft.Runnables.LocatorRunnable;
import com.benzrf.spellcraft.Runnables.MeteoriteRunnable;
import com.benzrf.spellcraft.Runnables.SpellCraftRunnable;
import com.benzrf.spellcraft.Runnables.SummonwolfRunnable;
import com.benzrf.spellcraft.Runnables.VoodooRunnable;

public class Spells
{
	public static boolean fireball(PlayerInteractEvent event)
	{
		Location fromloc = getLaunchPoint(event.getPlayer(), 1);
		fromloc.getWorld().playEffect(fromloc, Effect.MOBSPAWNER_FLAMES, 1);
		Fireball f = event.getPlayer().getWorld().spawn(fromloc, Fireball.class);
		f.setShooter(event.getPlayer());
		f.setYield(1.3F);
		return true;
	}

	public static boolean snowball(PlayerInteractEvent event)
	{
		event.getPlayer().launchProjectile(Snowball.class);
		return true;
	}
	
	public static boolean confuse(PlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof Player)
		{
			Player p = (Player) event.getRightClicked();
			((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet41MobEffect(p.getEntityId(), new MobEffect(9, 300, 25)));
			event.getPlayer().sendMessage(ChatColor.RED + "Confused!");
			p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 1);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean explodecreeper(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Creeper)
		{
			boolean isPowered = ((Creeper) event.getEntity()).isPowered();
			float strength = isPowered ? 6 : 3;
			event.getEntity().remove();
			event.getDamager().getWorld().createExplosion(event.getEntity().getLocation(), strength);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean regen(PlayerInteractEvent event)
	{
		int newHealth = event.getPlayer().getHealth() + 4;
		newHealth = newHealth > 20 ? 20 : newHealth;
		event.getPlayer().setHealth(newHealth);
		return true;
	}
	
	public static boolean freeze(PlayerInteractEntityEvent event)
	{
		FreezeRunnable f = new FreezeRunnable();
		f.e = event.getRightClicked();
		f.l = event.getRightClicked().getLocation();
		f.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, f, 0, 5);
		f.p = event.getPlayer();
		event.getPlayer().sendMessage(ChatColor.BLUE + "Frozen!");
		return true;
	}
	
	public static boolean explosion(PlayerInteractEvent event)
	{
		List<Block> list = event.getPlayer().getLineOfSight(null, 100);
		Location loc = list.get(list.size() - 1).getLocation();
		event.getPlayer().getWorld().createExplosion(loc, 4);
		return true;
	}	
	public static boolean air(PlayerInteractEvent event)
	{
		Block b = event.getPlayer().getEyeLocation().getBlock();
		int t = b.getTypeId();
		if (t == 8 || t == 9)
		{
			b.setTypeId(0);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean derp(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			((Player) event.getEntity()).chat("DERP!");
		}
		else
		{
			return false;
		}
		return true;
	}

	public static boolean lightning(PlayerInteractEvent event)
	{
		List<Block> list = event.getPlayer().getLineOfSight(null, 100);
		Location loc = list.get(list.size() - 1).getLocation();
		event.getPlayer().getWorld().strikeLightning(loc);
		return true;
	}

	public static boolean food(PlayerInteractEvent event)
	{
		Location loc = event.getPlayer().getLocation().add(0, -1, 0);
		int y = 0;
		boolean exit = true;
		if (loc.add(0, 1, 0).getBlock().getTypeId() != 0)
		{
			return false;
		}
		for (int yAdd = 1; yAdd < 129 && exit; yAdd++)
		{
			y = yAdd - 1;
			if (loc.clone().add(0, yAdd, 0).getBlock().getTypeId() != 0 || yAdd >= 6)
			{
				exit = false;
			}
		}
		loc.add(0, y, 0);
		ArrayList<Material> Foods = new ArrayList<Material>();
		Foods.add(Material.COOKIE);
		Foods.add(Material.COOKED_BEEF);
		Foods.add(Material.COOKED_CHICKEN);
		Foods.add(Material.COOKED_FISH);
		Foods.add(Material.BREAD);
		Foods.add(Material.MUSHROOM_SOUP);
		Foods.add(Material.MELON);
		Foods.add(Material.GRILLED_PORK);
		Collections.shuffle(Foods);
		Random r = new Random();
		for (int i = 0; i <= 5; i++)
		{
			event.getPlayer().getWorld().dropItem(loc, new ItemStack(Foods.get(r.nextInt(7)), r.nextInt(3)));
		}
		return true;
	}
	
	public static boolean fissure(EntityDamageByEntityEvent event)
	{
		Player p = ((Player) ((Projectile) event.getDamager()).getShooter());
		if (subfissureCount < 25)
		{
			p.sendMessage(ChatColor.RED + "There is already another fissure in the world! Wait up!");
			return false;
		}
		Location loc = event.getEntity().getLocation();
		if (!(p.hasPermission(SpellCraft.instance.permsName + ".fissureplayer") || p.isOp()))
		{
			if (SpellCraft.instance.permsLightning) p.getWorld().strikeLightning(p.getLocation());
			p.sendMessage(ChatColor.DARK_RED + SpellCraft.instance.fissurePlayerMessage);
			return false;
		}
		for (int x = -2; x < 3; x++)
		{
			for (int z = -2; z < 3; z++)
			{
				subfissure(loc.clone().add(x, 0, z), p);
			}
		}
		subfissureCount = 0;
		return true;
	}
	
	private static void subfissure(Location loc, Player p)
	{
		ArrayList<Integer> blocks = new ArrayList<Integer>();
		ArrayList<Byte> blocks2 = new ArrayList<Byte>();
		ArrayList<Integer> bannedBlocks = new ArrayList<Integer>();
		bannedBlocks.add(54);
		bannedBlocks.add(61);
		bannedBlocks.add(62);
		bannedBlocks.add(52);
		bannedBlocks.add(25);
		bannedBlocks.add(63);
		bannedBlocks.add(68);
		bannedBlocks.add(23);
		bannedBlocks.add(84);
		bannedBlocks.add(117);
		bannedBlocks.add(120);
		bannedBlocks.add(68);
		bannedBlocks.add(116);
		FissureRunnable f = new FissureRunnable();
		f.p = p;
		f.loc = loc.clone();
		loc.getWorld().playEffect(loc, Effect.BLAZE_SHOOT, 1);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
		for (int y = loc.getBlockY(); y >= 0; y--)
		{
			loc.setY(y);
			if (bannedBlocks.contains(loc.getBlock().getTypeId()))
			{
				blocks.add(257);
				blocks2.add((byte) 0);
			}
			else
			{
				blocks.add(loc.getBlock().getTypeId());
				blocks2.add(loc.getBlock().getData());
				loc.getBlock().setTypeId(0);
			}
		}
		f.blocks = blocks;
		f.blocks2 = blocks2;
		SpellCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(SpellCraft.instance, f, 100L);
		return;
	}
	
	public static boolean voodoo(PlayerInteractEntityEvent event)
	{
		Player p = event.getPlayer();
		if (!(p.hasPermission(SpellCraft.instance.permsName + ".voodooplayer") || p.isOp()) && event.getRightClicked() instanceof Player)
		{
			if (SpellCraft.instance.permsLightning) p.getWorld().strikeLightning(p.getLocation());
			p.sendMessage(ChatColor.DARK_RED + SpellCraft.instance.voodooPlayerMessage);
			return false;
		}
		if (voodooMap.containsKey(p.getName()))
		{
			p.sendMessage(ChatColor.RED + "You can't voodoo-ify more than one thing at once!");
			return false;
		}
		if (voodooMap.containsValue(event.getRightClicked()))
		{
			p.sendMessage(ChatColor.RED + "Somebody else is controlling them!");
			return false;
		}
		voodooMap.put(p.getName(), event.getRightClicked());
		VoodooRunnable v = new VoodooRunnable();
		v.p = p;
		v.e = event.getRightClicked();
		v.l = event.getRightClicked().getLocation().subtract(p.getLocation());
		v.lastPLoc = p.getLocation();
		v.lastELoc = event.getRightClicked().getLocation();
		v.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, v, 0, 1);
		p.sendMessage(ChatColor.BLUE + "Voodoo'd!");
		Location loc = event.getRightClicked().getLocation();
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 0, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 1, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 2, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 3, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 4, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 5, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 6, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 7, 30);
		loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.SMOKE, 8, 30);
		return true;
	}
	
	public static boolean superjumpfeatherfall(PlayerInteractEvent event)
	{
		if (event.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getTypeId() != 0)
		{
			event.getPlayer().setVelocity(new Vector(0, 1.6, 0).add(event.getPlayer().getVelocity()));
			return true;
		}
		FeatherfallRunnable f = new FeatherfallRunnable();
		f.m = SpellCraft.instance;
		f.p = event.getPlayer();
		f.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, f, 0, 1);
		event.getPlayer().sendMessage(ChatColor.BLUE + "Feather fall engaged!");
		return true;
	}
	
	public static boolean summonwolf(EntityDamageByEntityEvent event)
	{
		Player p = ((Player) ((Projectile) event.getDamager()).getShooter());
		Location loc = p.getLocation().toVector().getMidpoint(event.getEntity().getLocation().toVector()).toLocation(p.getWorld());
		p.getWorld().createExplosion(loc, 0);
		Wolf w = (Wolf) p.getWorld().spawnCreature(loc, EntityType.WOLF);
		w.setOwner(p);
		w.setHealth(20);
		w.setTarget((LivingEntity) event.getEntity());
//		((LivingEntity) event.getEntity()).damage(1, p);
//		((LivingEntity) event.getEntity()).setHealth(((LivingEntity) event.getEntity()).getHealth() + 1);
		SummonwolfRunnable s = new SummonwolfRunnable();
		s.e = (LivingEntity) event.getEntity();
		s.w = w;
		s.p = p;
		s.id = p.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, s, 0, 5);
		return true;
	}
	
	public static boolean arrowshield(PlayerInteractEvent event)
	{
		event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation().clone().add(0, 1, 0), Effect.MOBSPAWNER_FLAMES, 1);
		event.getPlayer().sendMessage(ChatColor.BLUE + "You are now protected from arrows!");
		ArrowshieldRunnable a = new ArrowshieldRunnable();
		a.p = event.getPlayer();
		a.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, a, 0, 2);
		return true;
	}
	
	public static boolean grabitems(PlayerInteractEvent event)
	{
		List<Block> list = event.getPlayer().getLineOfSight(null, 100);
		Location loc = list.get(list.size() - 1).getLocation();
		Entity a = event.getPlayer().getWorld().spawnArrow(loc, new Vector(0, 0, 0), 0, 0);
		ArrayList<Entity> eL = (ArrayList<Entity>) a.getNearbyEntities(2, 2, 2);
		a.remove();
		if (eL.size() < 1)
		{
			return false;
		}
		for (Entity e : eL)
		{
			if (e instanceof Item)
			{
				e.teleport(event.getPlayer().getLocation().add(0, 3, 0));
			}
		}
		return true;
	}
	
	public static boolean taunt(PlayerInteractEvent event)
	{
		tauntList.add(event.getPlayer().getName());
		event.getPlayer().sendMessage(ChatColor.BLUE + "Taunt engaged!");
		return true;
	}
	
	public static boolean locator(PlayerInteractEvent event)
	{
		LocatorRunnable l = new LocatorRunnable();
		l.event = event;
		SpellCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(SpellCraft.instance, l, 60);
		event.getPlayer().sendMessage(ChatColor.BLUE + "Hold the item you get from the ore in your hand.");
		return true;
	}
	
	public static boolean fusrodah(PlayerInteractEntityEvent event)
	{
		Player p = event.getPlayer();
		if (!(p.hasPermission(SpellCraft.instance.permsName + ".fusrohdahplayer") || p.isOp()) && event.getRightClicked() instanceof Player)
		{
			if (SpellCraft.instance.permsLightning) p.getWorld().strikeLightning(p.getLocation());
			p.sendMessage(ChatColor.DARK_RED + SpellCraft.instance.fusrodahPlayerMessage);
			return false;
		}
		Entity e = event.getRightClicked();
		e.setVelocity(p.getLocation().getDirection().multiply(10));
		p.chat("FUS! RO! DAH!");
		e.getWorld().createExplosion(e.getLocation(), 0);
		return true;
	}
	
	public static boolean meteorite(PlayerInteractEvent event)
	{
		List<Block> list = event.getPlayer().getLineOfSight(null, 100);
		Location loc = list.get(list.size() - 1).getLocation();
		loc.setY(128);
		MeteoriteRunnable mr = new MeteoriteRunnable();
		mr.l = loc;
		mr.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, mr, 0, 1);
		mr.p = event.getPlayer();
		return true;
	}
	
	public static boolean barrage(final PlayerInteractEvent event)
	{
		Runnable r = new Runnable () {
			@Override
			public void run()
			{
				final Arrow a = event.getPlayer().launchProjectile(Arrow.class);
				a.setVelocity(a.getVelocity().multiply(10));
				a.setShooter(null);
				Runnable r2 = new Runnable() {

					@Override
					public void run()
					{
						a.remove();
					}
				};
				SpellCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(SpellCraft.instance, r2, 10);
			}
		};
		for (int i = 0; i <= 19; i++)
		{
			SpellCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(SpellCraft.instance, r, i);
		}
		return true;
	}
	
	public static boolean bunker(final PlayerInteractEvent event)
	{
		final Location l = event.getPlayer().getLocation();
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 2; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					if (!(x == 0 && y == 0 && z == 0) && !(x == 0 && y == 1 && z == 0))
					{
						final Block b = l.clone().add(x, y, z).getBlock();
						if (b.getTypeId() == 0 || b.getType().equals(Material.WATER) || b.getType().equals(Material.LAVA) || b.getType().equals(Material.STATIONARY_WATER) || b.getType().equals(Material.STATIONARY_LAVA))
						{
							b.setType(Material.OBSIDIAN);
							b.setData((byte) 14);
							SpellCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(SpellCraft.instance, new Runnable() {
								public void run()
								{
									if (b.getType().equals(Material.OBSIDIAN) && (b.getData() == 14))
									{
										b.setTypeId(0);
									}
								}
							}, 100);
						}
					}
				}
			}
		}
		return true;
	}
	
	public static boolean snowstorm(PlayerInteractEvent event)
	{
		Location l = event.getPlayer().getTargetBlock(null, 100).getLocation();
		Block b;
		Arrow a = l.getWorld().spawnArrow(l, new Vector(0, 0, 0), 0, 0);
		for (Entity e : a.getNearbyEntities(5, 5, 5))
		{
			if (e instanceof LivingEntity && !e.equals(event.getPlayer()))
			{
				for (int x = -1; x <= 1; x++)
				{
					for (int y = -1; y <= 2; y++)
					{
						for (int z = -1; z <= 1; z++)
						{
							b = e.getLocation().add(x, y, z).getBlock();
							if (b.getTypeId() == 0 || b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER))
							{
								b.setType(Material.ICE);
							}
						}
					}
				}
			}
		}
		a.remove();
		for (int x = -5; x <= 5; x++)
		{
			for (int y = -5; y <= 5; y++)
			{
				for (int z = -5; z <= 5; z++)
				{
					b = l.clone().add(x, y, z).getBlock();
					if (b.getType().isBlock() && b.getLocation().add(0, 1, 0).getBlock().getTypeId() == 0)
					{
						b.getLocation().add(0, 1, 0).getBlock().setType(Material.SNOW);
					}
					if (b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER))
					{
						b.setType(Material.ICE);
					}
					else if (b.getType().equals(Material.STATIONARY_LAVA))
					{
						b.setType(Material.OBSIDIAN);
					}
					else if (b.getType().equals(Material.LAVA))
					{
						b.setType(Material.COBBLESTONE);
					}
				}
			}
		}
		return true;
	}
	
	public static boolean heisalreadyhere(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof Player))
		{
			event.getPlayer().sendMessage(ChatColor.GRAY + "Non-miners are not intelligent enough to be properly terrified of " + ChatColor.DARK_GREEN + "L" + ChatColor.MAGIC + "o" + ChatColor.DARK_GREEN + "rd English" + ChatColor.GRAY + ".");
			return false;
		}
		event.getPlayer().sendMessage(ChatColor.GRAY + "You are now channeling the power of " + ChatColor.DARK_GREEN + "L" + ChatColor.MAGIC + "o" + ChatColor.DARK_GREEN + "rd English" + ChatColor.GRAY + ".");
		HeisalreadyhereRunnable a = new HeisalreadyhereRunnable();
		a.p = event.getPlayer();
		a.t = (Player) event.getRightClicked();
		a.id = SpellCraft.instance.getServer().getScheduler().scheduleSyncRepeatingTask(SpellCraft.instance, a, 0, 20);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean raise(PlayerInteractEvent event)
	{
		List<Block> list = event.getPlayer().getLineOfSight(null, 100);
		Location loc = list.get(list.size() - 1).getLocation();
		Entity a = event.getPlayer().getWorld().spawnArrow(loc, new Vector(0, 0, 0), 0, 0);
		ArrayList<Entity> eL = (ArrayList<Entity>) a.getNearbyEntities(2, 2, 2);
		a.remove();
		int counter = 10;
		for (Entity i : eL)
		{
			if (counter <= 0)
			{
				return true;
			}
			if (i instanceof Item)
			{
				Material m = ((Item) i).getItemStack().getType();
				@SuppressWarnings("rawtypes")
				Class c = null;
				if (m.equals(Material.RAW_CHICKEN))
				{
					c = Chicken.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.RAW_BEEF))
				{
					c = Cow.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.PORK))
				{
					c = Pig.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.SNOW_BALL))
				{
					c = Snowman.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.BLAZE_ROD))
				{
					c = Blaze.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.SULPHUR))
				{
					c = Creeper.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.RAW_CHICKEN))
				{
					c = Chicken.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.ENDER_PEARL))
				{
					c = Enderman.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.GHAST_TEAR) && i.getWorld().getEnvironment().equals(Environment.NETHER))
				{
					c = Ghast.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.MAGMA_CREAM))
				{
					c = MagmaCube.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.BONE))
				{
					c = Skeleton.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.SLIME_BALL))
				{
					c = Slime.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.STRING))
				{
					c = Spider.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.ROTTEN_FLESH) && i.getWorld().getEnvironment().equals(Environment.NORMAL))
				{
					c = Zombie.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.ROTTEN_FLESH) && i.getWorld().getEnvironment().equals(Environment.NETHER))
				{
					c = PigZombie.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				else if (m.equals(Material.DRAGON_EGG) && i.getWorld().getEnvironment().equals(Environment.THE_END))
				{
					c = EnderDragon.class;
					if (((Item) i).getItemStack().getAmount() > 1)
					{
						((Item) i).getItemStack().setAmount(((Item) i).getItemStack().getAmount() - 1);
					}
					else
					{
						i.remove();
					}
				}
				if (c != null)
				{
					i.getWorld().spawn(i.getLocation(), c);
					i.getWorld().playEffect(i.getLocation(), Effect.SMOKE, 4);
					i.getWorld().playEffect(i.getLocation(), Effect.SMOKE, 4);
					i.getWorld().playEffect(i.getLocation(), Effect.SMOKE, 4);
					counter--;
				}
			}
		}
		return true;
	}
	
//	private static void spawnParticles(CraftEntity e, String s)
//	{
//		Random random = new Random();
//		for (int i = 0; i < 7; i++)
//		{
//			double d0 = random.nextGaussian() * 0.02D;
//			double d1 = random.nextGaussian() * 0.02D;
//			double d2 = random.nextGaussian() * 0.02D;
//			net.minecraft.server.Entity e2 = e.getHandle();
//			e2.world.a(s, e2.locX + random.nextFloat() * e2.width * 2.0F - e2.width, e2.locY + 0.5D + random.nextFloat() * e2.length, e2.locZ + random.nextFloat() * e2.width * 2.0F - e2.width, d0, d1, d2);
//		}
//	}
	
	public static Location getLaunchPoint(Player p, int multiplier)
	{
		Location fromloc = p.getEyeLocation();
		float adjuster = p.getLocation().getPitch();
		if (adjuster > 45)
		{
			adjuster = adjuster / 30;
		}
		else
		{
			adjuster = 1;
		}
		fromloc = fromloc.add(fromloc.getDirection().multiply(adjuster * multiplier));
		return fromloc;
	}
	
	public static List<String> tauntList = new ArrayList<String>();
	public static Map<String, Entity> voodooMap = new HashMap<String, Entity>();
	public static int subfissureCount = 25;
	public static List<SpellCraftRunnable> rList = new ArrayList<SpellCraftRunnable>();
}