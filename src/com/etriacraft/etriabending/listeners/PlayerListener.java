package com.etriacraft.etriabending.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.etriacraft.etriabending.EtriaBending;
import com.etriacraft.etriabending.suites.MessagingSuite;
import com.etriacraft.etriabending.suites.PlayerSuite;
import com.etriacraft.etriabending.util.Utils;

public class PlayerListener implements Listener {

	EtriaBending plugin;
	
	public static String joinmessage;
	public static String welcomemessage;
	public static String quitmessage;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (String o : PlayerSuite.vanishDb) {
			if (e.getPlayer().hasPermission("ec.vanish.seehidden")) continue;
			if (Bukkit.getPlayer(o) == null) continue;
			e.getPlayer().hidePlayer(Bukkit.getPlayer(o));
		}
		if (PlayerSuite.isVanished(e.getPlayer())) {
			PlayerSuite.setVanished(e.getPlayer(), true);
			e.getPlayer().sendMessage("�aYou logged in vanished!");
		}

		MessagingSuite.showMotd(e.getPlayer());

	}
	
	@EventHandler
	public void playerquitmessage(PlayerQuitEvent e) {
		e.setQuitMessage(Utils.colorize(quitmessage).replaceAll("<name>", e.getPlayer().getName()));
	}
	@EventHandler
	public void playerjoinmessages(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			e.setJoinMessage(Utils.colorize(welcomemessage).replaceAll("<name>", e.getPlayer().getName()));
		} else {
			e.setJoinMessage(Utils.colorize(joinmessage).replaceAll("<name>", e.getPlayer().getName()));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
		if (e.getClickedBlock().getType().equals(Material.REDSTONE_ORE) && PlayerSuite.isVanished(e.getPlayer())) e.setCancelled(true);

		if (PlayerSuite.isVanished(e.getPlayer()) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST)) {
			e.setCancelled(true);
			final Chest c = (Chest) e.getClickedBlock().getState();
			final Inventory i = Bukkit.getServer().createInventory(e.getPlayer(), c.getInventory().getSize());
			i.setContents(c.getInventory().getContents());
			e.getPlayer().openInventory(i);
			PlayerSuite.silentChestOpen(e.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (PlayerSuite.isVanished(e.getPlayer())) e.setCancelled(true);
	}
}