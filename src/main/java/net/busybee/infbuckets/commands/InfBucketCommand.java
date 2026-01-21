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

import java.util.Arrays;
import java.util.List;

public class InfBucketCommand extends AbstractPlayerCommand {

    public InfBucketCommand(InfBucketsPlugin plugin) {
        super("infb", "Infinite bucket command");
        requirePermission("infbuckets.give");
        addSubCommand(new GiveSubCommand());
        addSubCommand(new HelpSubCommand());
    }

    @Override
    protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> playerRef,
                           PlayerRef playerRefComponent, World world) {
        context.sendMessage(Message.raw("=== InfiniteBuckets Help ===").color("#FFD700"));
        context.sendMessage(Message.raw("/infb give <player> <type> - Give an infinite bucket").color("#AAAAAA"));
        context.sendMessage(Message.raw("/infb help - Show available bucket types").color("#AAAAAA"));
    }

    private class GiveSubCommand extends AbstractPlayerCommand {
        private final RequiredArg<String> targetPlayerArg;
        private final RequiredArg<String> bucketTypeArg;
        private final List<String> allowedTypes = Arrays.asList("water", "lava", "poison", "slime", "red_slime", "tar");

        public GiveSubCommand() {
            super("give", "Give an infinite bucket");
            this.targetPlayerArg = withRequiredArg("player", "Target player name", ArgTypes.STRING);
            this.bucketTypeArg = withRequiredArg("type", "Fluid type", ArgTypes.STRING);
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> senderRef,
                               PlayerRef senderPlayerRef, World world) {
            String targetPlayerName = context.get(targetPlayerArg);
            String type = context.get(bucketTypeArg).toLowerCase();

            Player targetPlayer = findPlayerByName(store, targetPlayerName);
            if (targetPlayer == null) {
                context.sendMessage(Message.raw("Player not found: " + targetPlayerName).color("#FF5555"));
                return;
            }

            if (!allowedTypes.contains(type)) {
                context.sendMessage(Message.raw("Unknown bucket type: " + type + ". Allowed: " + String.join(", ", allowedTypes)).color("#FF5555"));
                return;
            }

            String stateName = "Filled_" + capitalize(type);

            BsonDocument metadata = new BsonDocument();
            metadata.put("infinite", new BsonString("true"));
            metadata.put("infbucket_type", new BsonString(capitalize(type)));
            String itemId = "Container_Bucket";
            ItemStack infiniteBucket = new ItemStack(itemId, 1, metadata).withState(stateName);

            try {
                targetPlayer.getInventory().getCombinedHotbarFirst().addItemStack(infiniteBucket);
                context.sendMessage(Message.raw("Gave infinite " + type + " bucket to " + targetPlayer.getDisplayName()).color("#55FF55"));
            } catch (Exception e) {
                context.sendMessage(Message.raw("Error: " + e.getMessage()).color("#FF5555"));
            }
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            if (str.contains("_")) {
                String[] parts = str.split("_");
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append("_");
                }
                return sb.substring(0, sb.length() - 1);
            }
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }

        private Player findPlayerByName(Store<EntityStore> store, String name) {
            Player[] foundPlayer = new Player[1];
            store.forEachChunk((chunk, buffer) -> {
                for (int i = 0; i < chunk.size(); i++) {
                    Player p = chunk.getComponent(i, Player.getComponentType());
                    if (p != null && p.getDisplayName().equalsIgnoreCase(name)) foundPlayer[0] = p;
                }
            });
            return foundPlayer[0];
        }
    }

    private class HelpSubCommand extends AbstractPlayerCommand {
        private final List<String> bucketTypes = Arrays.asList("water", "lava", "poison", "slime", "red_slime", "tar");

        public HelpSubCommand() {
            super("help", "Show help and available bucket types");
        }

        @Override
        protected void execute(CommandContext context, Store<EntityStore> store, Ref<EntityStore> senderRef,
                               PlayerRef senderPlayerRef, World world) {
            context.sendMessage(Message.raw("=== InfiniteBuckets Help ===").color("#FFD700"));
            context.sendMessage(Message.raw("Usage: /infb give <player> <type>").color("#AAAAAA"));
            context.sendMessage(Message.raw("").color("#FFFFFF"));
            context.sendMessage(Message.raw("Available Bucket Types:").color("#55FF55"));
            for (String type : bucketTypes) {
                context.sendMessage(Message.raw("  - " + type).color("#FFFFFF"));
            }
        }
    }
}
