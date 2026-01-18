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
import com.hypixel.hytale.server.core.universe.world.World; // Verified Import
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.busybee.infbuckets.InfBucketsPlugin;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class InfBucketCommand extends AbstractPlayerCommand {

    private final InfBucketsPlugin plugin;

    public InfBucketCommand(InfBucketsPlugin plugin) {
        super("infb", "Infinite water bucket command");
        this.plugin = plugin;
        requirePermission("infbuckets.give");
        addSubCommand(new GiveSubCommand());
    }

    @Override
    protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> playerRef,
                           PlayerRef playerRefComponent, World world) {
        context.sendMessage(Message.raw("InfBuckets Commands:\n").color("#FFD700")
                .insert(Message.raw("/infb give <player> water ").color("#FFFF55")));
    }

    private class GiveSubCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> targetPlayerArg;

        public GiveSubCommand() {
            super("give", "Give an infinite water bucket");
            this.targetPlayerArg = withRequiredArg("player", "Target player name", ArgTypes.STRING);
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> senderRef,
                               PlayerRef senderPlayerRef, World world) {
            String targetPlayerName = context.get(targetPlayerArg);
            Player targetPlayer = findPlayerByName(store, targetPlayerName);

            if (targetPlayer == null) {
                context.sendMessage(Message.raw("Player '" + targetPlayerName + "' not found!").color("#FF5555"));
                return;
            }

            BsonDocument metadata = new BsonDocument();
            metadata.put("infinite", new BsonString("true"));
            metadata.put("infbucket_type", new BsonString("water"));

            // Use the base ID and set the state via a dedicated ItemStack property or variant ID
            // Based on Deco_Bucket.json and Container_Bucket.json:
            String itemId = "Container_Bucket";
            ItemStack infiniteBucket = new ItemStack(itemId, 1, metadata);

            // Note: If your API requires the state in the string, use: "Container_Bucket.Filled_Water"

            try {
                targetPlayer.getInventory().getCombinedHotbarFirst().addItemStack(infiniteBucket);
                context.sendMessage(Message.raw("Gave infinite water bucket to " + targetPlayer.getDisplayName() + "!").color("#55FF55"));
            } catch (Exception e) {
                context.sendMessage(Message.raw("Failed to give bucket: " + e.getMessage()).color("#FF5555"));
            }
        }

        private Player findPlayerByName(Store<EntityStore> store, String name) {
            Player[] foundPlayer = new Player[1];
            store.forEachChunk((chunk, buffer) -> {
                for (int i = 0; i < chunk.size(); i++) {
                    Player player = chunk.getComponent(i, Player.getComponentType());
                    if (player != null && player.getDisplayName().equalsIgnoreCase(name)) {
                        foundPlayer[0] = player;
                    }
                }
            });
            return foundPlayer[0];
        }
    }
}