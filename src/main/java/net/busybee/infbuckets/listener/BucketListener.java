package net.busybee.infbuckets.listener;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.busybee.infbuckets.InfBucketsPlugin;
import org.bson.BsonDocument;

/**
 * Listens for bucket usage and handles infinite bucket logic
 */
public class BucketListener {

    private final InfBucketsPlugin plugin;

    public BucketListener(InfBucketsPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getEventRegistry().register(
            UseBlockEvent.Pre.class,
            this::onBlockUse
        );

        plugin.getEventRegistry().register(
            UseBlockEvent.Post.class,
            this::onBlockUsePost
        );
    }

    /**
     * Handle block interactions before they occur
     * Prevents bucket from being consumed if it's an infinite bucket
     */
    private void onBlockUse(UseBlockEvent.Pre event) {
        InteractionContext context = event.getContext();
        ItemStack heldItem = context.getHeldItem();

        // Check if player is holding a bucket
        if (heldItem == null || !isBucket(heldItem)) {
            return;
        }

        // Get player
        Ref<EntityStore> entityRef = context.getEntity();
        Player player = entityRef.getStore().getComponent(entityRef, Player.getComponentType());

        if (player == null) {
            return;
        }

        // Check if this is an infinite bucket
        if (!isInfiniteBucket(heldItem)) {
            return;
        }

        String bucketType = getInfiniteBucketType(heldItem);

        // Check permissions
        if (bucketType.equals("water") && !player.hasPermission("infbuckets.use.water")) {
            player.sendMessage(
                Message.raw("You don't have permission to use infinite water buckets!").color("#FF5555")
            );
            event.setCancelled(true);
            return;
        }

        if (bucketType.equals("lava") && !player.hasPermission("infbuckets.use.lava")) {
            player.sendMessage(
                Message.raw("You don't have permission to use infinite lava buckets!").color("#FF5555")
            );
            event.setCancelled(true);
            return;
        }

        // Get block type
        String blockType = event.getBlockType().getId();
        InteractionType interactionType = event.getInteractionType();

        // Handle different bucket interactions
        if (isEmptyBucket(heldItem)) {
            // Picking up liquid with infinite empty bucket
            handlePickupLiquid(event, player, blockType, bucketType);
        } else if (isFilledBucket(heldItem)) {
            // Placing liquid with infinite filled bucket
            handlePlaceLiquid(event, player, blockType, bucketType);
        }
    }

    /**
     * Handle post-interaction to restore infinite bucket
     */
    private void onBlockUsePost(UseBlockEvent.Post event) {
        InteractionContext context = event.getContext();
        ItemStack heldItem = context.getHeldItem();

        if (heldItem == null || !isInfiniteBucket(heldItem)) {
            return;
        }

        Ref<EntityStore> entityRef = context.getEntity();
        Player player = entityRef.getStore().getComponent(entityRef, Player.getComponentType());

        if (player == null) {
            return;
        }

        String bucketType = getInfiniteBucketType(heldItem);

        // Check permissions again
        if (bucketType.equals("water") && !player.hasPermission("infbuckets.use.water")) {
            return;
        }

        if (bucketType.equals("lava") && !player.hasPermission("infbuckets.use.lava")) {
            return;
        }

        // After placing liquid, restore the filled bucket
        try {
            BsonDocument metadata = heldItem.getMetadata();
            String itemId = bucketType.equals("water") ? "hytale:water_bucket" : "hytale:lava_bucket";
            ItemStack restoredBucket = new ItemStack(itemId, 1, metadata);

            // Find the bucket slot and restore it
            short bucketSlot = findBucketSlot(player, heldItem);
            if (bucketSlot >= 0) {
                player.getInventory().getCombinedHotbarFirst().setItemStackForSlot(bucketSlot, restoredBucket);
            }
        } catch (Exception e) {
            plugin.getLogger().atSevere().withCause(e).log("Error restoring infinite bucket");
        }
    }

    /**
     * Handle picking up liquid with infinite bucket
     */
    private void handlePickupLiquid(UseBlockEvent.Pre event, Player player, String blockType, String bucketType) {
        // Check if picking up the correct liquid type
        if (bucketType.equals("water") && blockType.contains("water")) {
            // Allow pickup, bucket will be refilled automatically
            plugin.getLogger().atFine().log("Player " + player.getDisplayName() + " picking up water with infinite bucket");
        } else if (bucketType.equals("lava") && blockType.contains("lava")) {
            // Allow pickup, bucket will be refilled automatically
            plugin.getLogger().atFine().log("Player " + player.getDisplayName() + " picking up lava with infinite bucket");
        }
    }

    /**
     * Handle placing liquid with infinite bucket
     */
    private void handlePlaceLiquid(UseBlockEvent.Pre event, Player player, String blockType, String bucketType) {
        // Allow placement, bucket will be restored in post event
        plugin.getLogger().atFine().log("Player " + player.getDisplayName() + " placing " + bucketType + " with infinite bucket");
    }

    /**
     * Check if item is any type of bucket
     */
    private boolean isBucket(ItemStack item) {
        String itemId = item.getItemId().toLowerCase();
        return itemId.contains("bucket");
    }

    /**
     * Check if bucket is empty
     */
    private boolean isEmptyBucket(ItemStack item) {
        String itemId = item.getItemId().toLowerCase();
        return itemId.equals("hytale:bucket") || itemId.equals("hytale:empty_bucket");
    }

    /**
     * Check if bucket is filled
     */
    private boolean isFilledBucket(ItemStack item) {
        String itemId = item.getItemId().toLowerCase();
        return itemId.contains("water_bucket") || itemId.contains("lava_bucket");
    }

    /**
     * Check if this is an infinite bucket by checking metadata
     */
    private boolean isInfiniteBucket(ItemStack item) {
        BsonDocument metadata = item.getMetadata();
        if (metadata == null) {
            return false;
        }

        return metadata.containsKey("infinite") &&
               metadata.getString("infinite").getValue().equals("true");
    }

    /**
     * Get the type of infinite bucket (water or lava)
     */
    private String getInfiniteBucketType(ItemStack item) {
        BsonDocument metadata = item.getMetadata();
        if (metadata != null && metadata.containsKey("infbucket_type")) {
            return metadata.getString("infbucket_type").getValue();
        }

        // Fallback to item ID
        String itemId = item.getItemId().toLowerCase();
        if (itemId.contains("water")) {
            return "water";
        } else if (itemId.contains("lava")) {
            return "lava";
        }

        return "water"; // default
    }

    /**
     * Find the slot containing the bucket
     */
    private short findBucketSlot(Player player, ItemStack bucket) {
        var inventory = player.getInventory().getCombinedHotbarFirst();

        // Search through slots to find the bucket
        for (short i = 0; i < 40; i++) { // 9 hotbar + 36 inventory
            try {
                ItemStack slotItem = inventory.getItemStack(i);
                if (slotItem != null && slotItem.getItemId().equals(bucket.getItemId())) {
                    BsonDocument slotMeta = slotItem.getMetadata();
                    BsonDocument bucketMeta = bucket.getMetadata();

                    if (slotMeta != null && bucketMeta != null &&
                        slotMeta.containsKey("infinite") && bucketMeta.containsKey("infinite")) {
                        return i;
                    }
                }
            } catch (Exception e) {
                // Slot might be empty or invalid, continue
            }
        }

        return -1; // Not found
    }
}
