/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.wotuu.discord4jcommandparser;

import java.util.List;

/**
 *
 * @author wouter.koppenol
 */
public interface ISubListener {
    
    /**
     * Gets any sub commands tied to this command.
     * A sub command can be used to chain commands to for example:
     * "say hi", "say" can be a command, then "hi" can be a command as well.
     * Or "radio get_currently_playing". "r gcp" could have the same effect.
     * @return 
     */
    List<ICommandListener> getSubListener();
}
