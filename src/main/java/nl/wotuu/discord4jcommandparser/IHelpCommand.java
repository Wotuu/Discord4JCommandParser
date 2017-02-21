/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

/**
 *
 * @author wouter.koppenol
 */
public interface IHelpCommand {
    
    /**
     * Get the message that should be printed if the user asks for help for this command.
     * @return The help message.
     */
    String getHelpMessage();
}
