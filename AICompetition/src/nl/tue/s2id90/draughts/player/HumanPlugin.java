package nl.tue.s2id90.draughts.player;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;

/**
 *
 * @author huub
 */
@PluginImplementation
public class HumanPlugin  extends DraughtsPlayerProvider implements DraughtsPlugin {
    public HumanPlugin() {
        super(new HumanPlayer());
    }
}
