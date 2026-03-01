package com.stormai.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final FireMazePlugin plugin;

    public CommandHandler(FireMazePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("FireMaze Commands:");
            player.sendMessage("/firemaze create - Create a new maze");
            player.sendMessage("/firemaze info - Show maze info");
            player.sendMessage("/firemaze setdamage <rate> - Set damage rate");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                plugin.createMaze(player);
                break;
            case "info":
                plugin.showMazeInfo(player);
                break;
            case "setdamage":
                if (args.length < 2) {
                    player.sendMessage("Usage: /firemaze setdamage <rate>");
                    return true;
                }
                try {
                    double rate = Double.parseDouble(args[1]);
                    plugin.setDamageRate(player, rate);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid damage rate. Must be a number.");
                }
                break;
            default:
                player.sendMessage("Unknown subcommand. Use /firemaze for help.");
        }

        return true;
    }
}