package com.rikkamus.craftersoneclaimvisualizer;

import lombok.experimental.UtilityClass;
import org.joml.Vector4f;

import java.util.HexFormat;

@UtilityClass
public class Color {

    public static Vector4f parseRgbaHex(String hexColor) {
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

    public static String toRgbaHex(Vector4f rgba) {
        int r = (int) (rgba.x * 255f);
        int g = (int) (rgba.y * 255f);
        int b = (int) (rgba.z * 255f);
        int a = (int) (rgba.w * 255f);

        HexFormat format = HexFormat.of();
        return "#" + format.toHexDigits(r, 2) + format.toHexDigits(g, 2) + format.toHexDigits(b, 2) + format.toHexDigits(a, 2);
    }

    public static Vector4f parseArgbInt(int intColor) {
        return new Vector4f(
            (intColor >>> 16 & 255) / 255f,
            (intColor >>> 8 & 255) / 255f,
            (intColor & 255) / 255f,
            (intColor >>> 24 & 255) / 255f
        );
    }

}
