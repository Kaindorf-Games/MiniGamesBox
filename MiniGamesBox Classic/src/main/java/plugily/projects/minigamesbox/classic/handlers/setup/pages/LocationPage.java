/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.handlers.setup.pages;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.setup.items.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.sign.ArenaSign;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class LocationPage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public LocationPage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    setupInventory.getPlugin().getSetupUtilities().setDefaultItems(setupInventory, this, XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem(), XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_GUI), XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_VALUES));
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(1, new LocationItem(new ItemBuilder(Material.REDSTONE_BLOCK)
        .name(new MessageBuilder("&e&lSet Ending Location").build())
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported after the game)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("endlocation", setupInventory))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".endlocation", serializedLocation);
      setupInventory.getArena().setEndLocation(event.getWhoClicked().getLocation());
      new MessageBuilder("&e✔ Completed | &aEnding location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getWhoClicked());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".endlocation", serializedLocation);
          setupInventory.getArena().setEndLocation(event.getPlayer().getLocation());
          new MessageBuilder("&e✔ Completed | &aEnding location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          new MessageBuilder("&cPlease keep in mind to use blocks instead of player location for precise coordinates!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".endlocation", null);
          setupInventory.getArena().setEndLocation(null);
          setupInventory.getArena().setReady(false);
          new MessageBuilder("&e✔ Removed | &aEnding location for arena " + setupInventory.getArena().getId() + "!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".endlocation", serializedBlockLocation);
          setupInventory.getArena().setEndLocation(event.getClickedBlock().getRelative(0, 1, 0).getLocation());
          new MessageBuilder("&e✔ Completed | &aEnding location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
      }
    }, true, true, false));


    ItemStack bungeeItem;
    if(!setupInventory.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
      ItemBuilder itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial());
      itemBuilder.name(new MessageBuilder("&e&lAdd Game Sign").build());
      itemBuilder.lore(ChatColor.GRAY + "Target a sign and click this.");
      itemBuilder.lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)");
      bungeeItem = itemBuilder
          .build();
    } else {
      bungeeItem = new ItemBuilder(Material.BARRIER)
          .name(new MessageBuilder("&c&lAdd Game Sign").build())
          .lore(ChatColor.GRAY + "Option disabled with Bungee Cord module.")
          .lore(ChatColor.DARK_GRAY + "Bungee mode is meant to be one arena per server")
          .lore(ChatColor.DARK_GRAY + "If you wish to have multi arena, disable bungee in config!")
          .build();
    }
    setItem(3, new LocationItem(bungeeItem, event -> {
      if(setupInventory.getPlugin().getConfigPreferences().getOption("BUNGEEMODE")) {
        return;
      }
      event.getWhoClicked().closeInventory();

      Location location = event.getWhoClicked().getTargetBlock(null, 10).getLocation();
      Block block = location.getBlock();

      if(!(block.getState() instanceof Sign)) {
        new MessageBuilder("&c&l✘ &cPlease look at sign to add as a game sign!").prefix().send(event.getWhoClicked());
        return;
      }

      if(location.distance(event.getWhoClicked().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()
          && event.getClick() != ClickType.SHIFT_RIGHT) {
        new MessageBuilder("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
            + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so.").prefix().send(event.getWhoClicked());
        new MessageBuilder("&cYou can ignore this warning and add sign with Shift + Left Click, but for now &c&loperation is cancelled").prefix().send(event.getWhoClicked());
        return;
      }

      setupInventory.getPlugin().getSignManager().getArenaSigns().add(new ArenaSign((Sign) block.getState(), setupInventory.getArena()));
      new MessageBuilder("SIGNS_CREATED").asKey().send(event.getWhoClicked());
      List<String> locs = setupInventory.getPlugin().getSetupUtilities().getConfig().getStringList("instances." + setupInventory.getArena().getId() + ".signs");
      locs.add(location.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0,0.0");
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".signs", locs);
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_BLOCK:
          Location location = event.getClickedBlock().getLocation();
          Block block = location.getBlock();
          if(!(block.getState() instanceof Sign)) {
            new MessageBuilder("&c&l✘ &cPlease only use location where already is a sign to add it as a game sign!").prefix().send(event.getPlayer());
            return;
          }

          if(location.distance(event.getClickedBlock().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()) {
            new MessageBuilder("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
                + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so.").prefix().send(event.getPlayer());
          }

          setupInventory.getPlugin().getSignManager().getArenaSigns().add(new ArenaSign((Sign) block.getState(), setupInventory.getArena()));
          new MessageBuilder("SIGNS_CREATED").asKey().send(event.getPlayer());

          List<String> locs = setupInventory.getPlugin().getSetupUtilities().getConfig().getStringList("instances." + setupInventory.getArena().getId() + ".signs");
          locs.add(location.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0,0.0");
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".signs", locs);
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          setupInventory.getPlugin().getSignManager().updateSigns();
          refresh();
          break;
        case RIGHT_CLICK_BLOCK:
          Location locationRemoval = event.getClickedBlock().getLocation();
          Block sign = locationRemoval.getBlock();
          if(!(sign.getState() instanceof Sign)) {
            new MessageBuilder("&c&l✘ &cPlease only use location where already is a sign to add it as a game sign!").prefix().send(event.getPlayer());
            return;
          }
          setupInventory.getPlugin().getSignManager().getArenaSigns().remove(new ArenaSign((Sign) sign.getState(), setupInventory.getArena()));
          new MessageBuilder("&e✔ Removed | &c&lGame Sign got removed | You can now remove the sign!").prefix().send(event.getPlayer());

          List<String> locations = setupInventory.getPlugin().getSetupUtilities().getConfig().getStringList("instances." + setupInventory.getArena().getId() + ".signs");
          locations.remove(locationRemoval.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ() + ",0.0,0.0");
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".signs", locations);
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          setupInventory.getPlugin().getSignManager().updateSigns();
          refresh();
          break;
        case LEFT_CLICK_AIR:
        case RIGHT_CLICK_AIR:
          new MessageBuilder("&c&l✘ &cYou can't use a location that is at your player location, please select the sign!").prefix().send(event.getPlayer());
          break;
        default:
          break;
      }
    }, true, true, false));


    setItem(10, new LocationItem(new ItemBuilder(Material.LAPIS_BLOCK)
        .name(new MessageBuilder("&e&lSet Lobby Location").build())
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("lobbylocation", setupInventory))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".lobbylocation", serializedLocation);
      setupInventory.getArena().setLobbyLocation(event.getWhoClicked().getLocation());
      new MessageBuilder("&e✔ Completed | &aLobby location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getWhoClicked());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".lobbylocation", serializedLocation);
          setupInventory.getArena().setLobbyLocation(event.getPlayer().getLocation());
          new MessageBuilder("&e✔ Completed | &aLobby location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          new MessageBuilder("&cPlease keep in mind to use blocks instead of player location for precise coordinates!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".lobbylocation", null);
          setupInventory.getArena().setLobbyLocation(null);
          setupInventory.getArena().setReady(false);
          new MessageBuilder("&e✔ Removed | &aLobby location for arena " + setupInventory.getArena().getId() + "!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".lobbylocation", serializedBlockLocation);
          setupInventory.getArena().setLobbyLocation(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()));
          new MessageBuilder("&e✔ Completed | &aLobby location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
      }
    }, true, true, false));


    setItem(19, new LocationItem(new ItemBuilder(Material.EMERALD_BLOCK)
        .name(new MessageBuilder("&e&lSet Starting Location").build())
        .lore(ChatColor.GRAY + "Click to SET the starting location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(locations where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported when game starts)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("startlocation", setupInventory))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".startlocation", serializedLocation);
      setupInventory.getArena().setStartLocation(event.getWhoClicked().getLocation());
      new MessageBuilder("&e✔ Completed | &aStarting location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getWhoClicked());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".startlocation", serializedLocation);
          setupInventory.getArena().setStartLocation(event.getPlayer().getLocation());
          new MessageBuilder("&e✔ Completed | &aStarting location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          new MessageBuilder("&cPlease keep in mind to use blocks instead of player location for precise coordinates!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".startlocation", null);
          setupInventory.getArena().setStartLocation(null);
          setupInventory.getArena().setReady(false);
          new MessageBuilder("&e✔ Removed | &aStarting location for arena " + setupInventory.getArena().getId() + "!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".startlocation", serializedBlockLocation);
          setupInventory.getArena().setStartLocation(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()));
          new MessageBuilder("&e✔ Completed | &aStarting location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
      }
    }, true, true, false));

    setItem(28, new LocationItem(new ItemBuilder(Material.BAMBOO)
        .name(new MessageBuilder("&e&lSet Spectator Location").build())
        .lore(ChatColor.GRAY + "Click to SET the spectator location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(locations where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported when they get into spectator)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("spectatorlocation", setupInventory))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".spectatorlocation", serializedLocation);
      setupInventory.getArena().setSpectatorLocation(event.getWhoClicked().getLocation());
      new MessageBuilder("&e✔ Completed | &aSpectator location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getWhoClicked());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".spectatorlocation", serializedLocation);
          setupInventory.getArena().setSpectatorLocation(event.getPlayer().getLocation());
          new MessageBuilder("&e✔ Completed | &aSpectator location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          new MessageBuilder("&cPlease keep in mind to use blocks instead of player location for precise coordinates!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".spectatorlocation", null);
          setupInventory.getArena().setSpectatorLocation(null);
          setupInventory.getArena().setReady(false);
          new MessageBuilder("&e✔ Removed | &aSpectator location for arena " + setupInventory.getArena().getId() + "!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".spectatorlocation", serializedBlockLocation);
          setupInventory.getArena().setSpectatorLocation(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()));
          new MessageBuilder("&e✔ Completed | &aSpectator location for arena " + setupInventory.getArena().getId() + " set at your location!").prefix().send(event.getPlayer());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          refresh();
          break;
      }
    }, true, true, false));

  }
}
