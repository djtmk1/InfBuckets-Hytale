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
        plugin.getEventRegistry().register(UseBlockEvent.Post.class, this::onBlockUsePost);
    }

    private void onBlockUsePost(UseBlockEvent.Post event) {
        InteractionContext context = event.getContext();
        ItemStack heldItem = context.getHeldItem();
        if (heldItem == null || !isInfiniteBucket(heldItem)) return;

        Ref<EntityStore> entityRef = context.getEntity();
        Player player = entityRef.getStore().getComponent(entityRef, Player.getComponentType());
        if (player == null) return;

        try {
            BsonDocument metadata = heldItem.getMetadata();
            String bucketType = metadata.getString("infbucket_type").getValue();
            String stateName = "Filled_" + bucketType;
            String itemId = "Container_Bucket";
            ItemStack restoredBucket = new ItemStack(itemId, 1, metadata).withState(stateName);

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
                if (slotItem != null && slotItem.getItemId().contains("Container_Bucket")) {
                    return i;
                }
            } catch (Exception e) {}
        }
        return -1;
    }
}
