package org.ashenite.arctrade.Commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.ashenite.arctrade.ArcTrade;
import org.ashenite.arctrade.Inventories.TradeINV;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TradeCMD implements TabExecutor {

	private final ArcTrade plugin = ArcTrade.getPlugin();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}
		if (args.length != 1) {
			sender.sendMessage("Usage: /trade <player>");
			return true;
		}
		Player target = plugin.getServer().getPlayer(args[0]);
		TradeINV tradeINV = new TradeINV(plugin, player, target);
		assert target != null;

		player.sendMessage("Creating trade request with " + target.getName() + "...");
		player.sendMessage(MiniMessage.miniMessage().deserialize("<#FF0000>REMEMBER, ONLY WAY TO CANCEL THE TRADE IS TO CLICK THE CANCEL BUTTON! IF YOU CLOSE THE INVENTORY IN ANY OTHER WAY, BOTH PLAYERS WILL LOSE THEIR ITEMS!</#FF0000>"));
		player.openInventory(tradeINV.getInventory());
		target.sendMessage(player.getName() + " is creating a trade request with you!");
		target.sendMessage(MiniMessage.miniMessage().deserialize("<#FF0000>REMEMBER, ONLY WAY TO CANCEL THE TRADE IS TO CLICK THE CANCEL BUTTON! IF YOU CLOSE THE INVENTORY IN ANY OTHER WAY, BOTH PLAYERS WILL LOSE THEIR ITEMS!</#FF0000>"));
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player))
			return null;
		if (args.length == 1) {
			// list of players name
			List<String> players = new ArrayList<>();
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (player.equals(sender))
					continue;
				players.add(player.getName());
			}
			return players;
		}
		return null;
	}
}