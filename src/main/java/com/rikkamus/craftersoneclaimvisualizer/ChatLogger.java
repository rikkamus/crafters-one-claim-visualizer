package com.rikkamus.craftersoneclaimvisualizer;

import lombok.experimental.UtilityClass;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

@UtilityClass
public class ChatLogger {

    public static void log(String message, ChatFormatting formatting) {
        log(Component.literal(message).withStyle(formatting));
    }

    public static void log(Component component) {
        if (Minecraft.getInstance().player != null) Minecraft.getInstance().gui.getChat().addMessage(component);
    }

}
