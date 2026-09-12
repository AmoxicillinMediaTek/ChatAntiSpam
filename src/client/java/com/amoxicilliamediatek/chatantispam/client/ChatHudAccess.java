package com.amoxicilliamediatek.chatantispam.client;

import java.util.List;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

public interface ChatHudAccess
{
    int chatantispam$getScrolledLines();

    List<ChatHudLine.Visible> chatantispam$getVisibleMessages();

    List<ChatHudLine> chatantispam$getAllMessages();

    int chatantispam$getLineHeight();

    double chatantispam$getScale();

    int chatantispam$getWidth();

    default Text chatantispam$getMessageForLine(int index)
    {
        List<ChatHudLine.Visible> visibleMessages =
            chatantispam$getVisibleMessages();
        int fullIndex = 0;
        for(int line = 0; line < visibleMessages.size(); line++)
        {
            if(line == index)
            {
                List<ChatHudLine> allMessages = chatantispam$getAllMessages();
                if(fullIndex < allMessages.size())
                    return allMessages.get(fullIndex).content();
                return null;
            }

            if(visibleMessages.get(line).endOfEntry())
                fullIndex++;
        }
        return null;
    }
}
