package firfaraon;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Timer;
import mindustry.game.EventType;
import mindustry.mod.Plugin;

public class HubPlugin extends Plugin {
    public static Seq<Server> servers = new Seq<Server>();

    @Override
    public void init() {
        Events.on(EventType.ServerLoadEvent.class, (e)->{
            Log.info("Loading hub plugin...");
            loadHub();
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("add-server", "<name> <x> <y> <ip> <port>", "Add server to hub", (args)->{
            try {
                servers.add(new Server(
                        args[0],
                        getNextServerId(),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        args[3],
                        Integer.parseInt(args[4])
                ));
                save();
            } catch (NumberFormatException e) {
                Log.err("Invalid x, y or port!", e);
            }
        });

        handler.register("list-servers", "List added severs", (args)->{
            if(!servers.isEmpty())
                servers.each(s->{
                    Log.info("[@] @ - @:@", s.id, s.name, s.ip, s.port);
                });
            else
                Log.info("No servers added");
        });

        handler.register("save-servers", "Manually save stored servers", (args)->{
            save();
            Log.info("Servers saved!");
        });
    }

    public static void loadHub() {
        Core.settings.getJson("servers", Seq.class, Seq::new);
        Timer.schedule(()->{
            for(Server server : servers) {
                server.label();
            }
        }, 0, 3);
    }

    public int getNextServerId() {
        Core.settings.put("nextserverid", Core.settings.getInt("nextserverid", 0)+1);
        return Core.settings.getInt("nextserverid", 0);
    }

    public void save() {
        Core.settings.putJson("servers", Server.class, servers);
        Log.debug("Servers saved!");
    }
}
