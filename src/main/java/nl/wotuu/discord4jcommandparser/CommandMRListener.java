/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private IUser ourBot;
    
    public CommandMRListener(IUser ourBot){
        this.ourBot = ourBot;
    }
    
    /**
     * Triggers commands on a message that was deemed to contain commands.
     *
     * @param event The event which contains the message that was sent.
     */
    @Override
    public void handle(MessageReceivedEvent event) { // This is called when the ReadyEvent is dispatched
        System.out.println(">> handle()");
        IMessage message = event.getMessage();

        // Radio message
        String content = message.getContent();
        System.out.println(content);

        Boolean isMentioned = false;
        for (IUser user : message.getMentions()) {
            if (user.getID().equals(this.ourBot.getID())) {
                System.out.println(user.mention());
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
            System.out.println(listener.getClass().getName());
            System.out.println("Strict: " + listener.isStrict());
            for(String param : paramsList ){
                System.out.println("param: " + param);
            }
            // If mention is there, and matches strict checking
            if (((listener.requiresMention() && isMentioned) || !listener.requiresMention()) &&
                    ((listener.isStrict() && paramsList.isEmpty()) || !listener.isStrict())) {
                try {
                    listener.handleCommand(message, paramsList.toArray(new String[0]));
                } catch (Exception ex) {
                    try {
                        System.err.println(ex);
                        message.getChannel().sendMessage("Unable to process: " + ex.getLocalizedMessage());
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex1) {
                        System.err.println(ex1);
                    }
                }
            }
        }
        System.out.println("OK handle()");
    }
    
    /**
     * Get a command by recursively finding it in a command's sub commands.
     * @param rootListener The root sub-listener to start traversing in search for
     * subcommands which match the parameters.
     * @param params The parameters to find sub listeners for.
     * @return The found command listener, or NULL if none was found.
     */
    protected ICommandListener getCommandRecursive(ISubListener rootListener, List<String> params){
        System.out.println(">> " + params.size());
        // For any potential sub listener ..
        for(ICommandListener subListener : rootListener.getSubListener() ){
            // For each commands it will be triggered on
            for(String command : subListener.getCommands() ){
                // If it triggers the first parameter, find more
                if( command.equals(params.get(0)) ){
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
                            System.out.println("Found: " + recursiveListener.getClass().getName() + ", paramsSize: " + params.size());
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
                    
                    // System.out.println("Clearing " + params.size() + " params, setting " + currentParams.size() + " params");
                    // Restore parameters back to the list
                    params.clear();
                    // System.out.println("2: clearing " + params.size() + " params, setting " + currentParams.size() + " params");
                    params.addAll(currentParams);
                    
                    System.out.println("OK 2 - " + params.size());
                    return result;
                }
            }
        }
        System.out.println("OK " + params.size());
        
        // Not found any matching commands
        return null;
    }
}