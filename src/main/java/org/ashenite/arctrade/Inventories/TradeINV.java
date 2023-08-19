package org.ashenite.arctrade.Inventories;

import net.kyori.adventure.text.Component;
import org.ashenite.arctrade.ArcTrade;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TradeINV implements InventoryHolder {
	private final Inventory inv;
	public String state;
	public boolean cancellable = true;

	public boolean isCancellable() {
		return cancellable;
	}

	public void setCancellable(boolean cancellable) {
		this.cancellable = cancellable;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public TradeINV(@NotNull ArcTrade plugin, Player player, Player target) {
		this.state = "offer";
		this.inv = plugin.getServer().createInventory(this, 54, Component.text("Trade Menu"));
		// set black stained-glass pane to the borders
		int[] borderSlots = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53};
		for (int i : borderSlots) {
			this.inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
		}
		// set brown stained-glass pane to the middle
		for (int i = 13; i <= 40; i += 9) {
			this.inv.setItem(i, new ItemStack(Material.BROWN_STAINED_GLASS_PANE, 1));
		}
		// set info block
		ItemStack info = new ItemStack(Material.ENCHANTED_BOOK, 1);
		ItemMeta imeta = info.getItemMeta();
		imeta.displayName(Component.text("Info"));
		List<Component> ilore = new ArrayList<>();
		ilore.add(Component.text("Now: Offer items to trade"));
		ilore.add(Component.text("Next: Partner will offer items"));
		imeta.lore(ilore);
		info.setItemMeta(imeta);
		this.inv.setItem(4, info);


		ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemStack targetHead = new ItemStack(Material.PLAYER_HEAD, 1);
		// set name and lore
		String pname = player.getName();
		String tname = target.getName();
		playerHead.getItemMeta().displayName(Component.text(pname));
		targetHead.getItemMeta().displayName(Component.text(tname));
		// set lore
		ItemMeta pmeta = playerHead.getItemMeta();
		List<Component> plore = new ArrayList<>();
		plore.add(Component.text("Items " + tname + " will receive"));
		pmeta.lore(plore);
		pmeta.displayName(Component.text(pname));
		playerHead.setItemMeta(pmeta);

		ItemMeta tmeta = targetHead.getItemMeta();
		List<Component> tlore = new ArrayList<>();
		tlore.add(Component.text("Items " + pname + " will receive"));
		tmeta.lore(tlore);
		tmeta.displayName(Component.text(tname));
		targetHead.setItemMeta(tmeta);

		this.inv.setItem(11, playerHead);
		this.inv.setItem(15, targetHead);

		// set confirm button
		ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS, 1);
		ItemMeta cmeta = confirm.getItemMeta();
		cmeta.displayName(Component.text("Confirm"));
		cmeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "confirm"), PersistentDataType.BYTE);
		confirm.setItemMeta(cmeta);
		this.inv.setItem(47, confirm);

		// set cancel button
		ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS, 1);
		ItemMeta cameta = cancel.getItemMeta();
		cameta.displayName(Component.text("Cancel"));
		cameta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cancel"), PersistentDataType.BYTE);
		cancel.setItemMeta(cameta);
		this.inv.setItem(51, cancel);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inv;
	}
}
