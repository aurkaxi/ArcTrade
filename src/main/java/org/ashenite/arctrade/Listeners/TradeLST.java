package org.ashenite.arctrade.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.ashenite.arctrade.ArcTrade;
import org.ashenite.arctrade.Inventories.TradeINV;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class TradeLST implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null)
			return;
		// item in cursor
		if (!(event.getClickedInventory().getHolder() instanceof TradeINV tradeINV))
			return;
		if (!event.isLeftClick()) {
			event.setCancelled(true);
		}
		String state = tradeINV.getState();
		int[] allowedOfferSlots = {19, 20, 21, 28, 29, 30, 37, 38, 39};
		int[] allowedNegotiateSlots = {23, 24, 25, 32, 33, 34, 41, 42, 43};

		ItemStack citem = event.getCursor();
		if (citem == null) {
			System.out.println("WEIRD, citem is null");
			return;
		}
		ItemStack inSlot = event.getCurrentItem();

		// System.out.println(event);
		if (citem.getType() != Material.AIR && inSlot == null || citem.getType() == Material.AIR && inSlot != null) {
			if ((!state.equals("offer") || notInArray(event.getSlot(), allowedOfferSlots)) && (!state.equals("negotiate") || notInArray(event.getSlot(), allowedNegotiateSlots))) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}

		// if click cancel button or close inventory
		// give back items to respective players
		if (event.getSlot() == 51) {
			event.getWhoClicked().closeInventory();
		}
		if (event.getSlot() == 47) {
			switch (state) {
				case "offer" -> {
					tradeINV.setState("negotiate");
					ItemStack info = event.getInventory().getItem(4);
					ItemMeta imeta = Objects.requireNonNull(info).getItemMeta();
					List<Component> ilore = imeta.lore();
					assert ilore != null;
					ilore.clear();
					ilore.add(Component.text("Now: Check items offered"));
					ilore.add(Component.text("Offer your items to trade"));
					ilore.add(Component.text("Next: partner will confirm the trade"));
					imeta.lore(ilore);
					event.getInventory().setItem(4, info);

					TextComponent p2name = (TextComponent) Objects.requireNonNull(event.getInventory().getItem(15)).getItemMeta().displayName();
					assert p2name != null;
					Player player2 = ArcTrade.getPlugin().getServer().getPlayer(p2name.content());
					assert player2 != null;
					player2.openInventory(tradeINV.getInventory());
					tradeINV.setCancellable(false);
					event.getWhoClicked().closeInventory();
					tradeINV.setCancellable(true);
				}
				case "negotiate" -> {
					tradeINV.setState("final");
					ItemStack info = event.getInventory().getItem(4);
					ItemMeta imeta = Objects.requireNonNull(info).getItemMeta();
					List<Component> ilore = imeta.lore();
					assert ilore != null;
					ilore.clear();
					ilore.add(Component.text("Now: Check items offered"));
					ilore.add(Component.text("Confirm or cancel the trade"));
					ilore.add(Component.text("Next: Trade will be cancelled or completed"));
					imeta.lore(ilore);
					event.getInventory().setItem(4, info);
					TextComponent p1name = (TextComponent) Objects.requireNonNull(event.getInventory().getItem(11)).getItemMeta().displayName();
					assert p1name != null;
					Player player1 = ArcTrade.getPlugin().getServer().getPlayer(p1name.content());
					assert player1 != null;
					player1.openInventory(tradeINV.getInventory());
					tradeINV.setCancellable(false);
					event.getWhoClicked().closeInventory();
					tradeINV.setCancellable(true);
				}
				case "final" -> {
					tradeINV.setState("success");
					event.getWhoClicked().closeInventory();
				}
			}
		}


	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getInventory().getHolder() instanceof TradeINV) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void cancelTrade(InventoryCloseEvent event) {
		if (!(event.getInventory().getHolder() instanceof TradeINV tradeINV))
			return;

		TextComponent p1name = (TextComponent) Objects.requireNonNull(event.getInventory().getItem(11)).getItemMeta().displayName();
		TextComponent p2name = (TextComponent) Objects.requireNonNull(event.getInventory().getItem(15)).getItemMeta().displayName();
		assert p1name != null;
		assert p2name != null;
		Player player1 = ArcTrade.getPlugin().getServer().getPlayer(p1name.content());
		Player player2 = ArcTrade.getPlugin().getServer().getPlayer(p2name.content());
		assert player1 != null;
		assert player2 != null;
		int[] p1slots = {19, 20, 21, 28, 29, 30, 37, 38, 39};
		int[] p2slots = {23, 24, 25, 32, 33, 34, 41, 42, 43};


		Player target1;
		Player target2;
		Component msg;
		if (tradeINV.isCancellable()) {
			target1 = player1;
			target2 = player2;
			msg = MiniMessage.miniMessage().deserialize("<#FFFF00> Trade cancelled! </#FFFF00>");
		} else
			return;


		if (tradeINV.getState().equals("success")) {
			target1 = player2;
			target2 = player1;
			msg = MiniMessage.miniMessage().deserialize("<#00FF00> Trade successful! </#00FF00>");
		}

		for (int slot : p1slots) {
			ItemStack item = event.getInventory().getItem(slot);
			if (item != null) {
				target1.getInventory().addItem(item);
			}
		}
		for (int slot : p2slots) {
			ItemStack item = event.getInventory().getItem(slot);
			if (item != null) {
				target2.getInventory().addItem(item);
			}
		}
		target1.sendMessage(msg);
		target2.sendMessage(msg);
	}


	private boolean notInArray(int slot, int[] allowedSlots) {
		for (int allowedSlot : allowedSlots) {
			if (slot == allowedSlot)
				return false;
		}
		return true;
	}


}
