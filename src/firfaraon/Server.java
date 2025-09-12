package firfaraon;

import arc.Core;
import arc.math.geom.Point2;
import arc.scene.event.SceneEvent;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Call;

import static firfaraon.HubPlugin.defaultLabel;

public class Server {
    public String name;
    public final int id;

    public final String ip;
    public final int port;

    public final int pos;

    public transient boolean offline = false;

    public Server(){
        this.name = "";
        this.id = -1;
        this.ip = "";
        this.port = 0;
        this.pos = 0;
    }

    public Server(String name, int id, int x, int y, String ip, int port){
        this.pos = Point2.pack(x, y);
        this.name = name;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Spawns label at pos
     * */
    public void label() {
        int labelDistance = Core.settings.getInt("labeldistance", 2);
        Vars.net.pingHost(ip, port, s->{
            Call.label(s.name, 3.5f, Point2.x(pos)*8, (Point2.y(pos)+labelDistance)*8);
            String message = Core.settings.getString("labelmessage", defaultLabel);
            String modeName;
            if(s.modeName == null)
                modeName = s.mode.name();
            else
                modeName = s.modeName;
            message = message.replace("%p", s.players+"").replace("%m", s.mapname).replace("%g", modeName);
            Call.label(message, 3.55f, Point2.x(pos)*8, (Point2.y(pos)-labelDistance)*8);
            setOffline(false);
        }, f->{
            Call.label(Core.settings.getString("offlinemessage", "[scarlet]Offline"), 3.55f, Point2.x(pos)*8, (Point2.y(pos)-labelDistance)*8);
            Log.debug("Failed to ping host!", f);
	        Log.debug(f);
            setOffline(true);
        });
    }

    public void setOffline(boolean o) {
        this.offline=o;
    }
}
