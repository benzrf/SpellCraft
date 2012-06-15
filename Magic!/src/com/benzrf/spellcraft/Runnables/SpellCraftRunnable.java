package com.benzrf.spellcraft.Runnables;

import org.bukkit.entity.Player;

import com.benzrf.spellcraft.Spells;

public abstract class SpellCraftRunnable implements Runnable
{
	public SpellCraftRunnable()
	{
		Spells.rList.add(this);
	}
	public abstract void run();
	public abstract String getSpellName();
	public abstract boolean onStop();
	public abstract Player getCaster();
}
