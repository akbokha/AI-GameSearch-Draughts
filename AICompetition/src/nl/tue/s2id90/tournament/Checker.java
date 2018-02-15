package nl.tue.s2id90.tournament;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;
import nl.tue.s2id90.contest.PlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;
import nl.tue.s2id90.game.GameState;
import nl.tue.s2id90.game.Player;

/**
 * checks whether or not the give plugins fullfill the requirements for the
 * tournament.
 * <ol>
 * <li> have precisely one player per plugin
 * <li> have q unique package name: nl.tue.s2id90.groupNN
 * </ol>
 *
 * @author huub
 * @param <Competitor>
 * @param <P> Player type
 * @param <M> Move type
 * @param <S> GameState type
 */
public class Checker<Competitor extends Player<M,S>, P extends PlayerProvider<Competitor>, M, S extends GameState<M>> {

    public static void main(String[] args) {
        System.err.println("args="+Arrays.asList(args));
        Checker checker = new Checker();
        checker.go(args);
    }
    void go(String[] pluginFolders) {
        List<P> plugins = getPlugins(
                pluginFolders,
                p->   (p instanceof DraughtsPlugin)
                        && (p instanceof DraughtsPlayerProvider)
        );
        
        List<String> groups = plugins.stream()
            .map(p->p.getName())
            .map(p->className(p))
            .collect(Collectors.toList());
        Collections.sort(groups);
        System.out.format("checked plugins: \n %s\n", groups);
        
        System.out.println("\nCheck on multiple players in one jar file:");
        plugins.stream()
            .filter(p -> p.getPlayers().size()>1).forEach(p ->
                System.out.format("plugin %s has %d Players: %s\n",
                    p.getName(),
                    p.getPlayers().size(),
                    p.getPlayers().stream()
                        .map(player->className(player.getName()))
                        .collect(Collectors.toList())
                ));
        
        System.out.println("\nCheck on duplicate package names in the list of player classes:");
        List<String> playerPackageNames = plugins.stream()
            .flatMap(p->p.getPlayers().stream())
            .map(p -> p.getClass().getCanonicalName())
            .map(name-> packageName(name))
            .collect(Collectors.toList());
        
        playerPackageNames.stream()
            .filter(p->Collections.frequency(playerPackageNames, p)>1)
            .distinct()
            .forEach(p->
                System.out.format("multiple occuring package name: %s\n", p)
            );
    }
    
    String className(String name) {
        int index = name.lastIndexOf(".");
        return name.substring(index+1);
    }
    
    String packageName(String name) {
        return name.substring(0,name.lastIndexOf("."));
    }

    private List<P> getPlugins(String[] pluginFolders, Predicate<Plugin> selector) {
        PluginManager pm = PluginManagerFactory.createPluginManager();
        pm.addPluginsFrom(ClassURI.CLASSPATH, new OptionReportAfter());
        Arrays.asList(pluginFolders).stream().forEach(folder -> {
            pm.addPluginsFrom(new File(folder).toURI(), new OptionReportAfter());
        });
        PluginManagerUtil pmu = new PluginManagerUtil(pm);
        return pmu.getPlugins(Plugin.class).stream().filter(selector).map(p -> (P) p).collect(Collectors.toList());
    }
}
