package nl.tue.s2id90.group27;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;


/**
 *
 * @author huub
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make one or more players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(//new AlphaBetaGroup27(30),
                new BasicAlphaBetaGroup27(30),
                new AlphaBetaGroup27(30, 0.08134482f, 0.40828696f, 3f, 0.001115791f, 0.05383503f, 0.1396768f, 0.003636082f),
                new AlphaBetaGroup27(30, 0.6973688f, 0.20452403f, 3f, 0.000856f, 0.7066673f, 0.08885682f, 0.006674359f),
                new AlphaBetaGroup27(30, 0.4148461f, 0.712731f, 3f, 0.003206621f, 0.16236654f, 0.12995185f, 0.0000119f),
                new AlphaBetaGroup27(30, 0.37071338f, 0.008277425f, 3f, 0.001454319f, 0.14640248f, 0.038532697f, 0.000572f),
                new AlphaBetaGroup27(30, 0.38953158f, 0.49439526f, 3f, 0.003448247f, 0.011743657f, 0.0000000143f, 0.06374896f),
                new AlphaBetaGroup27(30, 0.028864006f, 0.000182f, 3f, 0.02470608f, 0.43854302f, 0.0000304f, 0.00961012f),
                new AlphaBetaGroup27(30, 0.29510808f, 0.05107948f, 3f, 0.017872872f, 0.26313502f, 0.12029633f, 0.010930816f),
                new AlphaBetaGroup27(30, 0.6979089f, 0.31211582f, 3f, 0.003623714f, 0.33468616f, 0.049585227f, 0.079528876f),
                new AlphaBetaGroup27(30, 0.2636121f, 0.615074f, 3f, 0.04580523f, 0.024873257f, 0.06838366f, 0.018477857f),
                new AlphaBetaGroup27(30, 0.0000516f, 0.117356785f, 3f, 0.013174546f, 0.024916839f, 0.011442239f, 0.019218715f),
                new AlphaBetaGroup27(30, 0.5788817f, 0.002500019f, 3f, 0.102333285f, 0.007910816f, 0.15276182f, 0.034970284f),
                new AlphaBetaGroup27(30, 0.29752862f, 0.27161768f, 3f, 0.020347988f, 0.07396747f, 0.043234427f, 0.023081876f)
        );
    }
}
