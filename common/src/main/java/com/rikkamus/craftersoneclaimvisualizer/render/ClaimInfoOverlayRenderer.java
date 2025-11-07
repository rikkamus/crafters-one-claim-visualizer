package com.rikkamus.craftersoneclaimvisualizer.render;

import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClaimInfoOverlayRenderer {

    public static void renderClaimOverlay(Claim claim, int x, int y, Alignment horizontalAlignment, Alignment verticalAlignment, GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;
        final int lineHeight = font.lineHeight;

        List<Component> lines;

        if (claim != null) {
            lines = List.of(
                Component.literal("ID: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getClaimId())).withStyle(ChatFormatting.YELLOW)),
                Component.literal("Owner: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getOwner())).withStyle(ChatFormatting.YELLOW)),
                Component.literal("Type: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getType())).withStyle(ChatFormatting.YELLOW))
            );
        } else {
            lines = List.of(
                Component.literal("Unclaimed").withStyle(ChatFormatting.RED)
            );
        }

        y = verticalAlignment.translate(y, lines.size() * lineHeight);

        for (Component line : lines) {
            guiGraphics.drawString(font, line, horizontalAlignment.translate(x, font.width(line.getString())), y, 0xFFFFFFFF);
            y += font.lineHeight;
        }
    }

}
