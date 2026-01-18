package net.busybee.infbuckets.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.busybee.infbuckets.InfBucketsPlugin;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class InfBucketCommand extends AbstractPlayerCommand {

    private final InfBucketsPlugin plugin;

    public InfBucketCommand(InfBucketsPlugin plugin) {
        super("infb", "Infinite bucket command");
        this.plugin = plugin;

        // Require permission
        requirePermission("infbuckets.give");

        // Add subcommand for 'give'
        addSubCommand(new GiveSubCommand());
    }

    @Override
    protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> playerRef,
                          PlayerRef playerRefComponent, World world) {
        // Show help message if no subcommand
        context.sendMessage(
            Message.raw("InfBuckets Commands:\n").color("#FFD700")
                .insert(Message.raw("/infb give <player> <water|lava> ").color("#FFFF55"))
                .insert(Message.raw("- Give infinite bucket to player").color("#AAAAAA"))
        );
    }

    private class GiveSubCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> targetPlayerArg;
        private final RequiredArg<String> bucketTypeArg;

        public GiveSubCommand() {
            super("give", "Give an infinite bucket to a player");
            requirePermission("infbuckets.give");

            this.targetPlayerArg = withRequiredArg("player", "Target player name", ArgTypes.STRING);
            this.bucketTypeArg = withRequiredArg("bucket_type", "Bucket type (water or lava)", ArgTypes.STRING);
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> senderRef,
                              PlayerRef senderPlayerRef, World world) {
            String targetPlayerName = context.get(targetPlayerArg);
            String bucketType = context.get(bucketTypeArg).toLowerCase();

            // Validate bucket type
            if (!bucketType.equals("water") && !bucketType.equals("lava")) {
                context.sendMessage(
                    Message.raw("Invalid bucket type! Use 'water' or 'lava'.").color("#FF5555")
                );
                return;
            }

            // Find target player
            Player targetPlayer = findPlayerByName(store, targetPlayerName);

            if (targetPlayer == null) {
                context.sendMessage(
                    Message.raw("Player '" + targetPlayerName + "' not found!").color("#FF5555")
                );
                return;
            }

            // Create infinite bucket with metadata
            BsonDocument metadata = new BsonDocument();
            metadata.put("infinite", new BsonString("true"));
            metadata.put("infbucket_type", new BsonString(bucketType));

            // Determine item ID based on bucket type
            String itemId = bucketType.equals("water") ? "hytale:water_bucket_filled" : "hytale:lava_bucket_filled";

            ItemStack infiniteBucket = new ItemStack(itemId, 1, metadata);

            // Give bucket to player
            try {
                targetPlayer.getInventory().getCombinedHotbarFirst().addItemStack(infiniteBucket);

                context.sendMessage(
                    Message.raw("Gave infinite " + bucketType + " bucket to " + targetPlayer.getDisplayName() + "!").color("#55FF55")
                );

                targetPlayer.sendMessage(
                    Message.raw("You received an infinite " + bucketType + " bucket!").color("#55FF55")
                );

                plugin.getLogger().atInfo().log("Gave infinite " + bucketType + " bucket to " + targetPlayer.getDisplayName());
            } catch (Exception e) {
                context.sendMessage(
                    Message.raw("Failed to give bucket: " + e.getMessage()).color("#FF5555")
                );
                plugin.getLogger().atSevere().withCause(e).log("Error giving infinite bucket");
            }
        }

        private Player findPlayerByName(Store<EntityStore> store, String name) {
            // Search through all entities to find player by display name
            Player[] foundPlayer = new Player[1];

            store.forEachChunk((chunk, buffer) -> {
                for (int i = 0; i < chunk.size(); i++) {
                    Player player = chunk.getComponent(i, Player.getComponentType());
                    if (player != null && player.getDisplayName().equalsIgnoreCase(name)) {
                        foundPlayer[0] = player;
                        return;
                    }
                }
            });

            return foundPlayer[0];
        }
    }
}
