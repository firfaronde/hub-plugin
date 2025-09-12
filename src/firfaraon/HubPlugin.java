package firfaraon;

import arc.Core;
import arc.Events;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Timer;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.mod.Plugin;
import mindustry.net.Administration;

public class HubPlugin extends Plugin {
    public static Seq<Server> servers;
    // 0 on map, gamemode
    // 1 on Maze, sandbox
    public static final String defaultLabel = "[stat]%p on %m[stat], %g";
    public static Administration.Config labelmessage = new Administration.Config("labelmessage", "Status table message", defaultLabel, null, ()->{
        String message = Core.settings.getString("labelmessage", defaultLabel);
        Log.info("Formatted status table @", message.replace("%p", "Player count").replace("%m", "mapname").replace("%g", "mode name"));
    });
    public static Administration.Config labelDistance = new Administration.Config("labeldistance", "Distance between labels", 2, null, ()->{});
    public static Administration.Config offlineMessage = new Administration.Config("offlinemessage", "This message displays when server offline", "[scarlet]Offline", null, ()->{});
    public static Administration.Config connectdst = new Administration.Config("connectdst", "Distance at which plugin redirects player to server", 2, null, ()->{});

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
                Log.info("Server added!");
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

        handler.register("remove-server", "<id>", "Remove server from list", (args)->{
            if(!Strings.canParseInt(args[0])) {
                Log.info("Cannot parse @", args[0]);
                return;
            }

            Server s = servers.find(ss->ss.id==Integer.parseInt(args[0]));

            servers.remove(s);

            Log.info("Removed [@] @", s.id, s.name);
            save();
        });

        handler.register("rename-server", "<id> <name>", "Re-name server in the list", (args)->{
            if(!Strings.canParseInt(args[0])) {
                Log.info("Cannot parse @", args[0]);
                return;
            }

            Server s = servers.find(ss->ss.id==Integer.parseInt(args[0]));
            String from = s.name;

            s.name = args[1];

            Log.info("Re-named from @ to @", from, s.name);
            save();
        });
    }

    public static void loadHub() {
        // servers = Core.settings.getJson("servers", Seq.class, Seq::new);
	    servers = Core.settings.getJson("servers", Seq.class, Server.class, Seq::new);
        Log.info("Loaded @ servers total", servers.size);
        Timer.schedule(()->{
            for(Server server : servers) {
                server.label();
            }
        }, 0, 3);

        Timer.schedule(() -> {
            Groups.player.each(p -> {
                servers.each(s -> {
                    if(!s.offline) {
                        int px = (int) (p.x / 8);
                        int py = (int) (p.y / 8);
                        int sx = Point2.x(s.pos);
                        int sy = Point2.y(s.pos);

                        float distance = (float) Math.sqrt((sx - px) * (sx - px) + (sy - py) * (sy - py));
                        if (distance <= Core.settings.getInt("connectdst", 2)) {
                            Call.connect(p.con, s.ip, s.port);
                        }
                    }
                });
            });
        }, 0, 1);
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
