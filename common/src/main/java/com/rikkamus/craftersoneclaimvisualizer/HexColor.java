package com.rikkamus.craftersoneclaimvisualizer;

import lombok.experimental.UtilityClass;
import org.joml.Vector4f;

import java.util.HexFormat;

@UtilityClass
public class HexColor {

    public static Vector4f parse(String hexColor) {
        if (hexColor.startsWith("#")) hexColor = hexColor.substring(1);

        int length = hexColor.length();
        if (length != 6 && length != 8) throw new IllegalArgumentException("Invalid hex color");

        Vector4f color = new Vector4f();
        color.x = HexFormat.fromHexDigits(hexColor, 0, 2) / 255f;
        color.y = HexFormat.fromHexDigits(hexColor, 2, 4) / 255f;
        color.z = HexFormat.fromHexDigits(hexColor, 4, 6) / 255f;

        if (length == 8) color.w = HexFormat.fromHexDigits(hexColor, 6, 8) / 255f;
        else color.w = 1f;

        return color;
    }

}
