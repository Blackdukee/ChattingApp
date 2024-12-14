package org.example.gui;
import javax.swing.border.EmptyBorder;
import  java.awt.*;
public class Utilities {
    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0);
    public static final Color PRIMARYP_COLOR =  Color.decode("#2F2D2D");
    public static final Color SECONDARY_COLOR = Color.decode("#484444");
    public static final Color Text_COLOR = Color.WHITE;
    public static final Font FONT = new Font("Inter", Font.PLAIN, 14);
    public static EmptyBorder addPadding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

}
