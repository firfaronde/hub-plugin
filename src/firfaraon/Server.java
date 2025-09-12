package firfaraon;

import arc.math.geom.Point2;
import arc.scene.event.SceneEvent;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Call;

public class Server {
    public final String name;
    public final int id;

    public final String ip;
    public final int port;

    public final int pos;
    public final float labelSize;
    public final int labelDistance = 2;

    public Server(String name, int id, int x, int y, String ip, int port){
        this.pos = Point2.pack(x, y);
        labelSize = 1f;
        this.name = name;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Spawns label at pos
     * */
    public void label() {
        Vars.net.pingHost(ip, port, s->{
            Call.label(s.name, 3.2f, Point2.x(pos), Point2.y(pos));
            Call.label(s.players + " players on "+s.mapname+", "+s.modeName, 3.2f, Point2.x(pos), Point2.y(pos));
        }, f->{
            Log.debug("Failed to ping host!", f);
        });
    }
}
