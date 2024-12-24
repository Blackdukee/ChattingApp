package org.example.gui;

import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Utilities class provides common utility methods and constants for the GUI components.
 */
public class Utilities {
    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0); // Updated to be truly transparent
    public static final Color PRIMARYP_COLOR = Color.decode("#2F2D2D");
    public static final Color SECONDARY_COLOR = Color.decode("#484444");
    public static final Color Text_COLOR = Color.WHITE;
    public static final Font FONT = new Font("Inter", Font.PLAIN, 14);

    /**
     * Adds padding to a component.
     *
     * @param top    the top padding
     * @param left   the left padding
     * @param bottom the bottom padding
     * @param right  the right padding
     * @return an EmptyBorder with the specified padding
     */
    public static EmptyBorder addPadding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    /**
     * Applies the theme to a component and its children.
     *
     * @param component the component to which the theme is applied
     */
    public static void applyTheme(Component component) {
        component.setBackground(PRIMARYP_COLOR);
        component.setForeground(Text_COLOR);
        component.setFont(FONT);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child);
            }
        }
    }
}
