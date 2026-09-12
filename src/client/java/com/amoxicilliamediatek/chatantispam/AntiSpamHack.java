package com.amoxicilliamediatek.chatantispam;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public final class AntiSpamHack
{
    private static boolean combining;

    private AntiSpamHack()
    {
    }

    public static boolean isCombining()
    {
        return combining;
    }

    public static Text combine(Text message,
        List<ChatHudLine.Visible> chatLines, int maxTextLength)
    {
        if(chatLines.isEmpty())
            return message;

        MinecraftClient minecraft = MinecraftClient.getInstance();
        List<OrderedText> newLines = minecraft.textRenderer
            .wrapLines(message, maxTextLength);

        int spamCounter = 1;
        int matchingLines = 0;

        for(int i = chatLines.size() - 1; i >= 0; i--)
        {
            String oldLine = getLineString(chatLines.get(i).content());

            if(matchingLines <= newLines.size() - 1)
            {
                String newLine = getLineString(newLines.get(matchingLines));

                if(matchingLines < newLines.size() - 1)
                {
                    if(oldLine.equals(newLine))
                        matchingLines++;
                    else
                        matchingLines = 0;
                    continue;
                }

                if(!oldLine.startsWith(newLine))
                {
                    matchingLines = 0;
                    continue;
                }

                if(i > 0 && matchingLines == newLines.size() - 1)
                {
                    String nextOldLine = getLineString(
                        chatLines.get(i - 1).content());
                    int oldSpamCounter = parseSpamCounter(
                        oldLine + nextOldLine, newLine.length());
                    if(oldSpamCounter > 0)
                    {
                        spamCounter += oldSpamCounter;
                        matchingLines++;
                        continue;
                    }
                }

                if(oldLine.length() == newLine.length())
                    spamCounter++;
                else
                {
                    int oldSpamCounter = parseSpamCounter(oldLine, newLine.length());
                    if(oldSpamCounter <= 0)
                    {
                        matchingLines = 0;
                        continue;
                    }
                    spamCounter += oldSpamCounter;
                }
            }

            for(int line = i + matchingLines; line >= i; line--)
                chatLines.remove(line);
            matchingLines = 0;
        }

        if(spamCounter == 1)
            return message;

        return withSpamCounter(message, spamCounter);
    }

    public static void beginCombining()
    {
        combining = true;
    }

    public static void endCombining()
    {
        combining = false;
    }

    private static String getLineString(OrderedText line)
    {
        StringBuilder text = new StringBuilder();
        line.accept((index, style, codePoint) -> {
            text.appendCodePoint(codePoint);
            return true;
        });
        return text.toString();
    }

    private static int parseSpamCounter(String text, int messageLength)
    {
        if(messageLength > text.length())
            return 0;

        String suffix = text.substring(messageLength);
        if(!suffix.startsWith(" [x") || !suffix.endsWith("]"))
            return 0;

        String counter = suffix.substring(3, suffix.length() - 1);
        try
        {
            int value = Integer.parseInt(counter);
            return value > 0 ? value : 0;
        } catch(NumberFormatException exception)
        {
            return 0;
        }
    }

    private static MutableText withSpamCounter(Text message,
        int spamCounter)
    {
        MutableText combined = message.copy();
        return combined.append(" [x" + spamCounter + "]");
    }
}
