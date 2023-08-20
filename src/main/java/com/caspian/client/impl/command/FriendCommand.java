package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.PlayerArgument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FriendCommand extends Command
{
    //
    Argument<String> actionArg = new StringArgument("Action", "Whether to add" +
            " or remove the friend", Arrays.asList("add", "remove"));
    Argument<PlayerEntity> playerArg = new PlayerArgument("Player", "The " +
            "player to add/remove friend");

    /**
     *
     */
    public FriendCommand()
    {
        super("friend", "<add/remove> <player name>", "Adds/Removes a friend " +
                "from the player list");
    }

    /**
     *
     */
    @Override
    public void onCommandInput()
    {
        final PlayerEntity player = playerArg.parse();
        if (player != null)
        {
            final String action = actionArg.parse();
            if (action != null)
            {
                if (action.equalsIgnoreCase("add"))
                {
                    ChatUtil.clientSendMessage("Added friend with name " +
                            Formatting.AQUA + player.getEntityName() + Formatting.RESET + "!");
                    Managers.SOCIAL.addFriend(player.getUuid());
                }
                else if (action.equalsIgnoreCase("remove")
                        || action.equalsIgnoreCase("del"))
                {
                    ChatUtil.clientSendMessage("Removed friend with name " +
                            Formatting.AQUA + player.getEntityName() + Formatting.RESET + "!");
                    Managers.SOCIAL.remove(player.getUuid());
                }
            }
            else
            {
                ChatUtil.clientSendMessage("Added friend with name " +
                        Formatting.AQUA + player.getEntityName() + Formatting.RESET + "!");
                Managers.SOCIAL.addFriend(player.getUuid());
            }
        }
    }
}
