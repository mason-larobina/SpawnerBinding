package me.laz.spawnerbinding;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class SpawnerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) {
            return;
        }

        // Manually drop spawner item, if allowed.
        event.setDropItems(false);

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType entity = spawner.getSpawnedType();
        if (entity == null) {
            SpawnerBinding.LOGGER.info("No entity type, no drop.");
            return;
        }

        PersistentDataContainer pdc = spawner.getPersistentDataContainer();
        SpawnerOwner owner = SpawnerOwner.from(pdc);
        if (owner == null) {
            SpawnerBinding.LOGGER.info("No owner, no drop.");
            return;
        }

        if (!owner.uuid().equals(player.getUniqueId())) {
            SpawnerBinding.LOGGER.info("Not your spawner, no drop.");
            return;
        }

        ItemStack drop = SpawnerBindingCommand.makePlayerSpawner(player, entity);
        event.getPlayer().getWorld().dropItem(block.getLocation(), drop);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) {
            return;
        }

        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.SPAWNER) {
            return;
        }

        if (item.getItemMeta() instanceof BlockStateMeta itemMeta) {
            if (itemMeta.getBlockState() instanceof CreatureSpawner itemState) {
                EntityType type = itemState.getSpawnedType();
                if (type == null) {
                    player.sendMessage("Error: bad spawner, no creature set.");
                    event.setCancelled(true);
                    return;
                }

                PersistentDataContainer itemPdc = itemMeta.getPersistentDataContainer();
                SpawnerOwner owner = SpawnerOwner.from(itemPdc);
                if (owner == null) {
                    player.sendMessage("Error: bad spawner, no owner.");
                    event.setCancelled(true);
                    return;
                }

                if (!owner.uuid().equals(player.getUniqueId())) {
                    player.sendMessage("Error: not your spawner.");
                    event.setCancelled(true);
                    return;
                }

                if (block.getState() instanceof CreatureSpawner blockSpawner) {
                    blockSpawner.setSpawnedType(type);
                    PersistentDataContainer blockPdc = blockSpawner.getPersistentDataContainer();
                    owner.set(blockPdc);
                    blockSpawner.update(true /* force */);
                }
            }
        }
    }
}
