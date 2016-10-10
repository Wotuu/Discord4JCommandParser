/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author wouter.koppenol
 */
public interface ICommandListener {
    
    /**
     * Strict command listener means that parameters must match exactly; no additional
     * parameters are allowed for the command is triggered.
     * @return True if the command is strict; false if additional parameters are allowed.
     */
    Boolean isStrict();
    
    /**
     * True to require a mention before the command is triggered, false if it doesn't.
     * Note: the mention may occur at any place in the user's message.
     * @return True or false.
     */
    Boolean requiresMention();
    
    /**
     * Get the command for which this listener will be triggered.
     * @return Example; "radio get-listeners", will return "get-listeners".
     * If you want to support more commands with the same result, return more entries.
     * That way both "gl" and "get-listeners" will get the current listeners.
     */
    String[] getCommands();
    
    /**
     * After all filters have passed, this function is triggered. Put your logic
     * which you want to execute when a matching command is triggered by the user
     * here.
     * @param message The source message that was matched to this command.
     * @param params Any additional parameters the user has provided. Note that if
     * you have a strict command, these parameters will always be empty (otherwise
     * it wouldn't be strict, either remove the strict requirement or pass less parameters).
     */
    void handleCommand(IMessage message, String[] params);
}
