package com.amoxicilliamediatek.chatantispam.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.amoxicilliamediatek.chatantispam.AntiSpamHack;
import com.amoxicilliamediatek.chatantispam.client.ChatHudAccess;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

@Mixin(ChatHud.class)
public abstract class ChatComponentMixin implements ChatHudAccess
{
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

    @Shadow
    private List<ChatHudLine> messages;

    @Shadow
    private int scrolledLines;

    @Override
    public int chatantispam$getScrolledLines()
    {
        return scrolledLines;
    }

    @Override
    public List<ChatHudLine.Visible> chatantispam$getVisibleMessages()
    {
        return visibleMessages;
    }

    @Override
    public List<ChatHudLine> chatantispam$getAllMessages()
    {
        return messages;
    }

    @Invoker("getLineHeight")
    @Override
    public abstract int chatantispam$getLineHeight();

    @Invoker("getChatScale")
    @Override
    public abstract double chatantispam$getScale();

    @Invoker("getWidth")
    @Override
    public abstract int chatantispam$getWidth();

    @Override
    public Text chatantispam$getMessageForLine(int index)
    {
        int fullIndex = 0;
        for(int line = 0; line < visibleMessages.size(); line++)
        {
            if(line == index)
            {
                if(fullIndex < messages.size())
                    return messages.get(fullIndex).content();
                return null;
            }

            if(visibleMessages.get(line).endOfEntry())
                fullIndex++;
        }
        return null;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"), cancellable = true)
    private void chatantispam$combinePlayerMessage(Text message,
        MessageSignatureData signature, MessageIndicator indicator,
        CallbackInfo callback)
    {
        if(AntiSpamHack.isCombining())
            return;

        Text combined = AntiSpamHack.combine(message, visibleMessages,
            chatantispam$getWidth());
        if(combined == message)
            return;

        AntiSpamHack.beginCombining();
        try
        {
            ((ChatHud)(Object)this).addMessage(combined, signature, indicator);
        } finally
        {
            AntiSpamHack.endCombining();
        }
        callback.cancel();
    }
}
