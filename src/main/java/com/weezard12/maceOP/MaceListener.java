package com.weezard12.maceOP;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.event.player.PlayerShieldDisableEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static com.weezard12.maceOP.MaceOP.plugin;


public class MaceListener implements Listener {

    private static UUID boostUUID = UUID.fromString("e945d5c5-b8c9-4d13-80f2-9d4d5f8b5d72");

    @EventHandler
    public void onHoldMace(PlayerInventorySlotChangeEvent event){
        Inventory inventory = event.getPlayer().getInventory();
        // The hotbar is represented by slots 0 to 8.

        boolean hasMace = false;
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getItem(i);

            if(isMace(item)){
                hasMace = true;
                break;
            }


        }
        if (hasMace) {
            applyHealthBoost(event.getPlayer());
        }
        else
            removeHealthBoost(event.getPlayer());
    }

    private boolean isMace(ItemStack item) {
        if (item == null) return false;
        return item.getType() == Material.MACE;
    }
    private boolean isShield(ItemStack item) {
        if (item == null) return false;
        return item.getType() == Material.SHIELD;
    }

    private void applyHealthBoost(Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            // Check if the modifier is already applied
            if (healthAttribute.getModifiers().stream()
                    .noneMatch(modifier -> boostUUID.toString().equals(modifier.getName()))) {

                AttributeModifier modifier = new AttributeModifier(
                        boostUUID, // Use a fixed UUID for consistency
                        "MaceHealthBoost",
                        plugin.getHealthBoost(),
                        AttributeModifier.Operation.ADD_NUMBER
                );

                boolean isPlayerFullHealth = false;
                if(player.getHealth() == healthAttribute.getValue())
                    isPlayerFullHealth = true;
                healthAttribute.addModifier(modifier);

                // Adjust current health to ensure it's within the new max range
                if(isPlayerFullHealth)
                    player.setHealth(player.getHealth() + plugin.getHealthBoost());
            }
        }

    }

    private void removeHealthBoost(Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            // Find and remove the modifier
            healthAttribute.getModifiers().stream()
                    .filter(modifier -> boostUUID.toString().equals(modifier.getName()))
                    .forEach(healthAttribute::removeModifier);

            // Adjust current health if it exceeds the new max health
            if (player.getHealth() > healthAttribute.getValue()) {
                player.setHealth(healthAttribute.getValue());
            }
        }

    }

    @EventHandler
    public void onShieldBlocked(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player attackedPlayer){
            if(event.getDamager() instanceof Player player){
                if(!attackedPlayer.isBlocking())
                    return;
                if(isMace(player.getInventory().getItemInMainHand())){
                    attackedPlayer.setCooldown(Material.SHIELD, plugin.getShieldDisableTicks());
                    attackedPlayer.clearActiveItem();
                }
            }
        }


    }
}
