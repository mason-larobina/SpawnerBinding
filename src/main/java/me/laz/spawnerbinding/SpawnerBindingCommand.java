package me.laz.spawnerbinding;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Map;

public class SpawnerBindingCommand {
    public static void registerCommands() {
        new CommandAPICommand("spawnerbinding")
                .withAliases("sb")
                .withArguments(new LiteralArgument("give"))
                .withArguments(new PlayerArgument("target"))
                .withArguments(new EntityTypeArgument("entity"))
                .executes(SpawnerBindingCommand::give)
                .register();
    }

    private static int give(CommandSender sender, CommandArguments args) {
        if (args.get("target") instanceof Player player) {
            if (args.get("entity") instanceof EntityType entity) {
                ItemStack spawner = makePlayerSpawner(player, entity);
                giveOrDrop(player, spawner);
                return 1;
            }
        }
        return 0;
    }

    public static ItemStack makePlayerSpawner(Player player, EntityType entity) {
        ItemStack stack = new ItemStack(Material.SPAWNER);
        if (stack.getItemMeta() instanceof BlockStateMeta meta) {
            if (meta.getBlockState() instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(entity);
                meta.setBlockState(spawner);

                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                SpawnerOwner.from(player).set(pdc);

                meta.setLore(List.of(String.format("Soulbound to %s", player.getName())));

                stack.setItemMeta(meta);
                return stack;
            }
        }

        throw new IllegalStateException("Invalid block meta and or block state.");
    }

    public static void giveOrDrop(Player player, ItemStack stack) {
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(stack);
        overflow.values().forEach(x -> player.getWorld().dropItem(player.getLocation(), x));
    }
}
