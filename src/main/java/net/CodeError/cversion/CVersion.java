package net.CodeError.cversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class CVersion extends JavaPlugin implements CommandExecutor, TabCompleter {

	private static final String chatPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "CVersion" + ChatColor.DARK_GRAY + "] ";

	private List<String> subcommands = Arrays.asList("help", "reload", "set");
	private List<String> setSubcommands = Arrays.asList("version", "protocol");
	private List<String> blank = new ArrayList<>();

	private String version = this.getConfig().getString("version-string", "Epic Server");
	private int protocol = this.getConfig().getInt("protocol-number", 47);

	@Override
	public void onEnable() {
		
		this.saveDefaultConfig();

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.SERVER_INFO), ListenerOptions.ASYNC) {

			@Override
			public void onPacketSending(PacketEvent event) {

				pingPacket(event.getPacket().getServerPings().read(0));

			}

		});

		this.getCommand("cversion").setExecutor(this);
		this.getCommand("cversion").setTabCompleter(this);
		this.getLogger().info("CVersion v1.0 has been successfully enabled! Created by CodeError.");

	}

	@Override
	public void onDisable() {

		this.getLogger().info("CVersion v1.0 has been disabled.");

	}

	private void pingPacket(WrappedServerPing ping) {

		ping.setVersionName(version);
		ping.setVersionProtocol(protocol);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 0) {

			if (!(sender instanceof Player)) {

				return false;

			}

			sender.sendMessage(chatPrefix + ChatColor.GOLD + "CVersion v1.0 - Created by " + ChatColor.RED + "CodeError");
			sender.sendMessage(chatPrefix + ChatColor.GRAY + "Plugin Help: " + ChatColor.WHITE + "/cversion help");

			return true;

		}

		else {

			if (args[0].equalsIgnoreCase("help")) {

				if (!(sender instanceof Player)) {

					return false;

				}

				sender.sendMessage(chatPrefix + ChatColor.GOLD + "CVersion v1.0 - " + ChatColor.RED + "Plugin Help");
				sender.sendMessage(chatPrefix + ChatColor.GRAY + "/cversion reload - " + ChatColor.WHITE + "Reloads the plugin.");

				return true;

			}

			else if (args[0].equalsIgnoreCase("reload")) {

				if (!(sender instanceof Entity)) {

					this.reloadConfig();
					this.getConfig();
					this.getLogger().info("Plugin configuration has been successfully reloaded. All changes have been applied.");

					return true;

				}

				else if (sender instanceof Player && sender.hasPermission("cversion.reload")) {

					this.reloadConfig();
					this.getConfig();
					this.getLogger().info("Plugin configuration has been successfully reloaded. All changes have been applied.");

					sender.sendMessage(chatPrefix + ChatColor.GREEN + "Plugin configuration has been successfully reloaded. All changes have been applied.");

					return true;

				}

				sender.sendMessage(chatPrefix + ChatColor.DARK_RED + "You do not have the appropriate permissions to perform this command.");

				return true;

			}

			else if (args[0].equalsIgnoreCase("set")) {

				if (!(sender instanceof Player)) {

					return false;

				}

				if (args.length == 1) {

					sender.sendMessage(chatPrefix + ChatColor.RED + "Too few args! Please specify which value you wish to set!");

					return true;

				}

				else if (args[1].equalsIgnoreCase("version")) {

					if (!(sender instanceof Player)) {

						return false;

					}

					else if (sender instanceof Player && sender.hasPermission("cversion.set.version")) {

						if (args.length == 2) {

							sender.sendMessage(chatPrefix + ChatColor.RED + "Too few args! Please specify a non-empty value!");

							return true;

						}
						
						else if (args.length == 3 && args[2].equalsIgnoreCase("default")) {
							
							version = "Epic Server";
							sender.sendMessage(chatPrefix + ChatColor.GREEN + "Version string successfully set to: " + ChatColor.GRAY + version);

							return true;
							
						}

						StringBuilder versionBuilder = new StringBuilder();

						for (int i = 2; i < args.length; i++) {

							if (i < args.length) {

								versionBuilder.append(args[i] + " ");

							}

							else if (i == args.length - 1) {

								versionBuilder.append(args[i]);

							}

						}

						version = ChatColor.translateAlternateColorCodes('&', new String(versionBuilder));

						this.getConfig().set("version-string", version);
						this.reloadConfig();
						this.getConfig();
						sender.sendMessage(chatPrefix + ChatColor.GREEN + "Version string successfully set to: " + ChatColor.GRAY + version);

						return true;

					}

					sender.sendMessage(chatPrefix + ChatColor.DARK_RED + "You do not have the appropriate permissions to perform this command.");

					return true;

				}

				else if (args[1].equalsIgnoreCase("protocol")) {

					if (!(sender instanceof Player)) {

						return false;

					}

					else if (sender instanceof Player && sender.hasPermission("cversion.set.protocol")) {

						if (args.length == 2) {

							sender.sendMessage(chatPrefix + ChatColor.RED + "Too few args! Please specify a non-empty integer value!");

							return true;

						}
						
						else if (args.length == 3 && args[2].equalsIgnoreCase("default")) {
							
							protocol = 47;
							sender.sendMessage(chatPrefix + ChatColor.GREEN + "Version string successfully set to: " + ChatColor.GRAY + protocol);

							return true;
							
						}

						try {

							protocol = Integer.parseInt(args[2]);

						}

						catch (NumberFormatException e) {

							sender.sendMessage(chatPrefix + ChatColor.RED + "Invalid protocol number specified! Please check " + ChatColor.YELLOW + "https://wiki.vg/Protocol_version_numbers " + ChatColor.RED + "for valid protocol numbers.");

							return true;

						}

						this.getConfig().set("protocol-number", protocol);
						this.reloadConfig();
						this.getConfig();
						sender.sendMessage(chatPrefix + ChatColor.GREEN + "Protocol number successfully set to: " + ChatColor.GRAY + protocol);

						return true;

					}

					sender.sendMessage(chatPrefix + ChatColor.DARK_RED + "You do not have the appropriate permissions to perform this command.");

					return true;

				}

			}

		}

		sender.sendMessage(chatPrefix + ChatColor.RED + "Unknown subcommand. " + ChatColor.GRAY + "/cversion help");

		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length == 1) {

			return subcommands;

		}

		else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {

			return setSubcommands;

		}

		return blank;

	}

}
