/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 *
 * @author Wouter
 */
public abstract class CommandMRListener implements IListener<MessageReceivedEvent>, ISubListener {
    
    /**
     * The user that is our bot. Used for checking if we were mentioned or not.
     */
    private final String ourBotID;
    
    /**
     * True to print some debug info.
     */
    private Boolean debug;
    
    public CommandMRListener(String userID){
        assert(userID != null);
        this.ourBotID = userID;
    }
    
    /**
     * Triggers commands on a message that was deemed to contain commands.
     *
     * @param event The event which contains the message that was sent.
     */
    @Override
    public void handle(MessageReceivedEvent event) { // This is called when the ReadyEvent is dispatched
        log(">> handle()");
        IMessage message = event.getMessage();
        Assert.assertTrue("Message is null!", message != null);
        
        // Radio message
        String content = message.getContent();
        log(content);

        Boolean isMentioned = false;
        Assert.assertTrue(message.getMentions() != null);
        
        for (IUser user : message.getMentions()) {
            Assert.assertTrue(user != null);
            log(user.getClass().getName());
            Assert.assertTrue(user.getID() != null);
            
            if (user.getID().equals(ourBotID)) {
                log(user.mention());
                isMentioned = true;
                // Replace all mentions to the bot
                content = content.replace(user.mention().replace("!", ""), "").trim();
            }
        }

        // Construct params so any handler doesn't need to
        String[] split = content.split(" ");
        List<String> paramsList = new ArrayList<>(Arrays.asList(split));
        
        ICommandListener listener = this.getCommandRecursive(this, paramsList);
        
        if (listener != null) {
            log(listener.getClass().getName());
            log("Strict: " + listener.isStrict());
            for(String param : paramsList ){
                log("param: " + param);
            }
            // If mention is there, and matches strict checking
            if (((listener.requiresMention() && isMentioned) || !listener.requiresMention()) &&
                    ((listener.isStrict() && paramsList.isEmpty()) || !listener.isStrict())) {
                try {
                    listener.handleCommand(message, paramsList.toArray(new String[0]));
                } catch (Exception ex) {
                    try {
                        System.err.println(ex);
                        // Print the stacktrace in markdown
                        message.getChannel().sendMessage("Unable to process; " + ex.getClass().getName() + ": ```" + Utils.getStackTraceString(ex.getStackTrace()) + "```");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex1) {
                        System.err.println(ex1);
                    }
                }
            }
        }
        log("OK handle()");
    }
    
    /**
     * Get a command by recursively finding it in a command's sub commands.
     * @param rootListener The root sub-listener to start traversing in search for
     * subcommands which match the parameters.
     * @param params The parameters to find sub listeners for.
     * @return The found command listener, or NULL if none was found.
     */
    protected ICommandListener getCommandRecursive(ISubListener rootListener, List<String> params){
        log(">> getCommandRecursive(): " + params.size());
        if( !params.isEmpty() ){
            // For any potential sub listener ..
            for(ICommandListener subListener : rootListener.getSubListener() ){
                // For each commands it will be triggered on
                for(String command : subListener.getCommands() ){
                    // If it triggers the first parameter, find more
                    if( !params.isEmpty() && command.equalsIgnoreCase(params.get(0)) ){
                        // Pop the first parameter
                        params.remove(0);

                        // Keep a copy of the parameters as they are now
                        List<String> currentParams = new ArrayList<>();
                        currentParams.addAll(params);
                        ICommandListener result = null;
                        // If this sub listener has more sub listeners themselves
                        if( subListener instanceof ISubListener ){
                            // Find them too
                            ICommandListener recursiveListener = getCommandRecursive((ISubListener)subListener, params);
                            if( recursiveListener != null ){
                                log("Found: " + recursiveListener.getClass().getName() + ", paramsSize: " + params.size());
                                // We found something
                                result = recursiveListener;
                                // Be sure to pass its parameters correctly (otherwise
                                // we'd include too much parameters)
                                currentParams.clear();
                                currentParams.addAll(params);
                            }
                        }
                        // Nothing found; we already had our match
                        if( result == null ){
                            result = subListener;
                        }

                        // log("Clearing " + params.size() + " params, setting " + currentParams.size() + " params");
                        // Restore parameters back to the list
                        params.clear();
                        // log("2: clearing " + params.size() + " params, setting " + currentParams.size() + " params");
                        params.addAll(currentParams);

                        log("OK 2 getCommandRecursive(): " + params.size());
                        return result;
                    }
                }
            }
        }
        log("OK getCommandRecursive(): " + params.size());
        
        // Not found any matching commands
        return null;
    }
    
    /**
     * Logs a message to the console.
     * @param message Message to log (if debug is enabled).
     */
    private void log(String message){
        if( this.debug ){
            System.out.println(message);
        }
    }
    
    /**
     * Enables debugging of the class; will print out messages in the log.
     */
    public void enableDebug(){
        this.debug = true;
    }
}
