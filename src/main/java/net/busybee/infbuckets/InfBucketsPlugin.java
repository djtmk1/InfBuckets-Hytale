package net.busybee.infbuckets;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import net.busybee.infbuckets.commands.InfBucketCommand;
import net.busybee.infbuckets.listener.BucketListener;

/**
 * InfBuckets Plugin - Adds infinite water and lava buckets
 *
 * Permissions:
 * - infbuckets.give - Allows use of /infb give command
 * - infbuckets.use.water - Allows use of infinite water buckets
 * - infbuckets.use.lava - Allows use of infinite lava buckets
 */
public class InfBucketsPlugin extends JavaPlugin {

    private static InfBucketsPlugin instance;
    private BucketListener bucketListener;

    public InfBucketsPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        getLogger().atInfo().log("InfBuckets plugin loading...");

        // Register command
        getCommandRegistry().registerCommand(new InfBucketCommand(this));
        getLogger().atInfo().log("Registered /infb command");

        // Register event listener
        bucketListener = new BucketListener(this);
        bucketListener.register();
        getLogger().atInfo().log("Registered bucket event listener");

        getLogger().atInfo().log("InfBuckets plugin loaded successfully!");
        getLogger().atInfo().log("Commands: /infb give <player> <water|lava>");
        getLogger().atInfo().log("Permissions: infbuckets.give, infbuckets.use.water, infbuckets.use.lava");
    }

    public static InfBucketsPlugin getInstance() {
        return instance;
    }
}
