package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

public class DisableAutoJoinArgument {
    public DisableAutoJoinArgument(PluginArgumentsRegistry registry) {
        registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("autojoin", registry.getPlugin().getPluginNamePrefixLong() + ".admin.autojoin", CommandArgument.ExecutorType.BOTH,
                new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " autojoin", "/" + registry.getPlugin().getCommandAdminPrefix() + " autojoin",
                        "&7Disable autojoin \n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.autojoin")) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                registry.getPlugin().getArenaManager().switchAutoJoinEnabled();
                new MessageBuilder("%plugin_prefix% Autojoin is " + (registry.getPlugin().getArenaManager().isAutoJoinEnabled() ? "enabled" : "disabled")).send(sender);
            }
        });
    }

}
