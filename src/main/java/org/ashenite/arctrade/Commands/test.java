package org.ashenite.arctrade.Commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.ashenite.arctrade.ArcTrade;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class test implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		MongoCollection<Document> coll = ArcTrade.getPlugin().getColl("trades");
		Document doc = coll.find(Filters.and(Filters.exists("aurkaxi"), Filters.exists("carrot"))).first();
		if (doc == null) {
			sender.sendMessage("null");
			return true;
		}
		sender.sendMessage(doc.toJson());

		return true;
	}
}
