package net.busybee.infbuckets.listener;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.busybee.infbuckets.InfBucketsPlugin;
import org.bson.BsonDocument;

public class BucketListener {

    private final InfBucketsPlugin plugin;

    public BucketListener(InfBucketsPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        // We use Post because we want to refill the bucket AFTER the fluid is placed in the world
        plugin.getEventRegistry().register(UseBlockEvent.Post.class, this::onBlockUsePost);
    }

    private void onBlockUsePost(UseBlockEvent.Post event) {
        InteractionContext context = event.getContext();
        ItemStack heldItem = context.getHeldItem();

        // Check for our custom metadata
        if (heldItem == null || !isInfiniteBucket(heldItem)) return;

        Ref<EntityStore> entityRef = context.getEntity();
        Player player = entityRef.getStore().getComponent(entityRef, Player.getComponentType());
        if (player == null) return;

        try {
            BsonDocument metadata = heldItem.getMetadata();
            String bucketType = metadata.getString("infbucket_type").getValue();

            // Map the fluid type back to the state name
            String stateName;
            switch (bucketType) {
                case "water": stateName = "Filled_Water"; break;
                // Add more types here as valid state names are discovered
                default: stateName = "Filled_Water"; break;
            }

            // Re-create the item with the correct Base ID, metadata, and state
            String itemId = "Container_Bucket";
            ItemStack restoredBucket = new ItemStack(itemId, 1, metadata).withState(stateName);

            // Re-apply the bucket to the slot to prevent it from turning into an empty version
            short bucketSlot = findBucketSlot(player);
            if (bucketSlot >= 0) {
                player.getInventory().getCombinedHotbarFirst().setItemStackForSlot(bucketSlot, restoredBucket);
            }
        } catch (Exception e) {
            plugin.getLogger().atSevere().withCause(e).log("Error restoring infinite bucket");
        }
    }

    private boolean isInfiniteBucket(ItemStack item) {
        BsonDocument metadata = item.getMetadata();
        return metadata != null && metadata.containsKey("infinite");
    }

    private short findBucketSlot(Player player) {
        var inventory = player.getInventory().getCombinedHotbarFirst();
        for (short i = 0; i < 40; i++) {
            try {
                ItemStack slotItem = inventory.getItemStack(i);
                // Search for the ID that the engine just switched to
                if (slotItem != null && slotItem.getItemId().contains("Container_Bucket")) {
                    return i;
                }
            } catch (Exception e) {}
        }
        return -1;
    }
}
