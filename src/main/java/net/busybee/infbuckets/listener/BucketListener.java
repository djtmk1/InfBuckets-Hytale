package net.busybee.infbuckets.listener;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
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
        plugin.getEventRegistry().register(UseBlockEvent.Pre.class, this::onBlockUse);
        plugin.getEventRegistry().register(UseBlockEvent.Post.class, this::onBlockUsePost);
    }

    private void onBlockUse(UseBlockEvent.Pre event) {
        InteractionContext context = event.getContext();
        ItemStack heldItem = context.getHeldItem();

        if (heldItem == null || !isBucket(heldItem)) return;

        Ref<EntityStore> entityRef = context.getEntity();
        Player player = entityRef.getStore().getComponent(entityRef, Player.getComponentType());

        if (player == null || !isInfiniteBucket(heldItem)) return;

        // Strictly water permission check
        if (!player.hasPermission("infbuckets.use.water")) {
            player.sendMessage(Message.raw("You don't have permission to use infinite water buckets!").color("#FF5555"));
            event.setCancelled(true);
        }
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

            // FIXED: Using the Base ID + State format to resolve the "?" icon
            // Format: BaseID#StateName
            String itemId = "Container_Bucket#Filled_Water";
            ItemStack restoredBucket = new ItemStack(itemId, 1, metadata);

            short bucketSlot = findBucketSlot(player, restoredBucket);
            if (bucketSlot >= 0) {
                player.getInventory().getCombinedHotbarFirst().setItemStackForSlot(bucketSlot, restoredBucket);
            }
        } catch (Exception e) {
            plugin.getLogger().atSevere().withCause(e).log("Error restoring infinite water bucket");
        }
    }

    private boolean isBucket(ItemStack item) {
        String itemId = item.getItemId();
        return itemId.contains("Container_Bucket") || itemId.contains("bucket");
    }

    private boolean isInfiniteBucket(ItemStack item) {
        BsonDocument metadata = item.getMetadata();
        return metadata != null &&
                metadata.containsKey("infinite") &&
                metadata.getString("infinite").getValue().equals("true");
    }

    private short findBucketSlot(Player player, ItemStack targetItem) {
        var inventory = player.getInventory().getCombinedHotbarFirst();
        for (short i = 0; i < 40; i++) {
            try {
                ItemStack slotItem = inventory.getItemStack(i);
                if (slotItem != null && slotItem.getItemId().equals(targetItem.getItemId())) {
                    BsonDocument slotMeta = slotItem.getMetadata();
                    if (slotMeta != null && slotMeta.containsKey("infinite")) return i;
                }
            } catch (Exception e) {}
        }
        return -1;
    }
}