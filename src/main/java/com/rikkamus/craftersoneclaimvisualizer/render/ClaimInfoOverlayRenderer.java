package com.rikkamus.craftersoneclaimvisualizer.render;

import com.rikkamus.craftersoneclaimvisualizer.claim.Claim;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClaimInfoOverlayRenderer {

    public static void renderClaimOverlay(Claim claim, GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;

        int x = 5;
        int y = 5;

        if (claim != null) {
            List<Component> lines = List.of(
                Component.literal("ID: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getClaimId())).withStyle(ChatFormatting.YELLOW)),
                Component.literal("Owner: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getOwner())).withStyle(ChatFormatting.YELLOW)),
                Component.literal("Type: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(claim.getType())).withStyle(ChatFormatting.YELLOW))
            );

            for (Component line : lines) {
                guiGraphics.drawString(font, line, x, y, 0xFFFFFFFF);
                y += font.lineHeight;
            }
        } else {
            guiGraphics.drawString(font, Component.literal("Unclaimed").withStyle(ChatFormatting.RED), x, y, 0xFFFFFFFF);
        }
    }

}
