package com.amoxicilliamediatek.chatantispam.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.amoxicilliamediatek.chatantispam.client.ChatHudAccess;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen
{
    private static final int BUTTON_SIZE = 12;
    private static final int BUTTON_MARGIN = 3;

    protected ChatScreenMixin(Text title)
    {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void chatantispam$drawCopyButton(DrawContext graphics,
        int mouseX, int mouseY, float delta, CallbackInfo callback)
    {
        CopyTarget target = chatantispam$getCopyTarget(mouseX, mouseY);
        if(target == null)
            return;

        graphics.fill(target.x, target.y, target.x + BUTTON_SIZE,
            target.y + BUTTON_SIZE, 0xCC202020);
        graphics.drawText(this.client.textRenderer, Text.literal("C"), target.x + 3,
            target.y + 2, 0xFFFFFFFF, false);
        graphics.drawTooltip(this.client.textRenderer,
            Text.literal("Copy chat"), mouseX, mouseY);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chatantispam$copyChat(Click event,
        boolean doubleClick, CallbackInfoReturnable<Boolean> callback)
    {
        if(event.button() != 0)
            return;

        CopyTarget target = chatantispam$getCopyTarget(
            (int)event.x(), (int)event.y());
        if(target == null)
            return;

        this.client.keyboard.setClipboard(target.message.getString());
        callback.setReturnValue(true);
    }

    @Unique
    private CopyTarget chatantispam$getCopyTarget(int mouseX, int mouseY)
    {
        ChatHud chat = this.client.inGameHud.getChatHud();
        ChatHudAccess access = (ChatHudAccess)(Object)chat;
        double scale = access.chatantispam$getScale();
        int lineHeight = access.chatantispam$getLineHeight();
        double linePosition = ((double)this.height - mouseY - 40.0D)
            / (scale * lineHeight);
        int lineIndex = MathHelper.floor(linePosition
            + access.chatantispam$getScrolledLines());
        List<?> visibleMessages = access.chatantispam$getVisibleMessages();
        if(lineIndex < 0 || lineIndex >= visibleMessages.size())
            return null;

        int linesPerPage = Math.min(chat.getVisibleLineCount(),
            visibleMessages.size());
        if(linePosition < 0 || linePosition >= linesPerPage)
            return null;

        int buttonX = (int)(access.chatantispam$getWidth()
            + BUTTON_MARGIN * scale);
        int chatTop = (int)((this.height - 40) / scale);
        int buttonY = (int)((chatTop - (lineIndex
            - access.chatantispam$getScrolledLines() + 1) * lineHeight
            + Math.ceil((lineHeight - BUTTON_SIZE) / 2.0D)) * scale);
        if(mouseX < buttonX || mouseX > buttonX + BUTTON_SIZE
            || mouseY < buttonY || mouseY > buttonY + BUTTON_SIZE)
            return null;

        Text message = access.chatantispam$getMessageForLine(lineIndex);
        return message == null ? null : new CopyTarget(buttonX, buttonY, message);
    }

    @Unique
    private record CopyTarget(int x, int y, Text message)
    {
    }
}
