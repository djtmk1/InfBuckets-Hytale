package net.busybee.infbuckets;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import net.busybee.infbuckets.commands.InfBucketCommand;
import net.busybee.infbuckets.listener.BucketListener;

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

        getCommandRegistry().registerCommand(new InfBucketCommand(this));

        bucketListener = new BucketListener(this);
        bucketListener.register();

        getLogger().atInfo().log("InfBuckets plugin loaded! Using ID: Container_Bucket");
    }

    public static InfBucketsPlugin getInstance() {
        return instance;
    }
}
