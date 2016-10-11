# Discord4JCommandParser
Small library which parses incoming messages to a Discord bot using Discord4J, allowing the developer to easily respond to commands given by the user with minimal configuration.

# Usage example
Create a class extending CommandMRListener. This class will serve as the root access point for all commands. For example, if the user enters the command `radio get-currently-playing`, this class will handle the `radio` part of the message.

```java
public class RootCommandMRListener extends CommandMRListener {
    public RootCommandMRListener(String botUserID) {
        super(botUserID);
    }

    /**
     * List of root commands.
     * @return The list of root commands (first parameter to trigger the message).
     */
    @Override
    public List<ICommandListener> getSubListener() {
        ArrayList<ICommandListener> commands = new ArrayList<>();
        // Add your root commands here
        return commands;
    }
}
```

Add an instance of this class to your Discord4J's EventDispatcher:

```java
public static void main(String[] args) {
	Main.client = Main.getClient(Main.TOKEN, true);
	EventDispatcher dispatcher = client.getDispatcher();
	
	dispatcher.registerListener(new RootCommandMRListener(Main.ID));
}
```

Adding a new command is as simple as implementing the `ICommandListener` interface.

```java
public class RadioCommand implements ICommandListener, ISubListener {
    @Override
    public Boolean isStrict() { return false; }

    @Override
    public Boolean requiresMention() { return false; }

    @Override
    public String[] getCommands() { return new String[] { "radio", "r" }; }

    @Override
    public void handleCommand(IMessage message, String[] params) {
        // Your logic here
    }

    @Override
    public List<ICommandListener> getSubListener() {
        ArrayList<ICommandListener> subCommands = new ArrayList<>();
        // subCommands.add(new CurrentlyPlayingCommand());
        return subCommands;
    }
}
```

This interface will give you some options to give to your command. `Strict` will mean the matched parameters must much exactly, non-strict will allow you to pass parameters to your command (ex.: `radio request 321`; `321` may be the parameter).

If you want your command to have sub-commands, simply implement the `ISubListener` interface and fill it with other `ICommandListener` instances.

This way you can chain commands to a previous commands for neat nesting of commands.