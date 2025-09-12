### Building
- `./gradlew jar`
### Using
- move plugin jar file to config/mods directory
- Add your servers to the serverlist via
- `add-server`
- Check serverlist via
- `list-servers`
- Check `config` and `help` commands for more commands.
### Example usage
- add-server Foo 123 456 192.168.1.1 6567
- [I] Server added!
- list-servers
- [I] [13] Foo - 192.168.1.1:6567
- remove-server 13
- [I] Removed [13] Foo
- config labeldistance 2
- [I] labeldistance set to 2.
- config offlinemessage [scarlet]Server offline
- [I] offlinemessage set to [scarlet]Server offline.
- config connectdst 2
- [I] connectdst set to 2.
- config labelmessage %p players on map %m gamemode %g
- [I] Formatted status table Player count players on map mapname gamemode mode name
- [I] labelmessage set to %p players on map %m gamemode %g.
