/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whereyoudey.form.component;

import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Label;
import com.sun.lwuit.Painter;
import com.sun.lwuit.geom.Rectangle;
import com.sun.lwuit.layouts.BoxLayout;
import com.whereyoudey.form.WrappingLabel;
import com.whereyoudey.utils.FontUtil;
import com.whereyoudey.utils.UiUtil;

/**
 *
 * @author Vikram S
 */
public class Section {

    private final Form form;
    private final WrappingLabel title;
    private final WrappingLabel desc;
    private final Container container;

    public Section(final Form form, String title, String desc) {
        this.form = form;
        container = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        addLine();
        this.title = new WrappingLabel(title, FontUtil.getMediumBoldFont());
        container.addComponent(this.title);
        this.desc = new WrappingLabel(desc, FontUtil.getSmallNormalFont());
        container.addComponent(this.desc);
        container.setFocusable(true);
        form.addComponent(container);
    }

    private void addLine() {
        Label line = new Label(" ");
        line.setWidth(Display.getInstance().getDisplayWidth());
        Font smallFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        line.getStyle().setFont(smallFont);
        line.getStyle().setBgPainter(new Painter() {

            public void paint(Graphics g, Rectangle r) {
                g.setColor(0x000000);
                g.fillRect(r.getX(), r.getY() + 10, r.getSize().getWidth(), 1);
            }
        });
        container.addComponent(line);
    }

    private Label addBigFontLabel(final String ph) {
        Label phoneNumber = new Label(ph);
        Font bigFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        phoneNumber.getStyle().setFont(bigFont);
        container.addComponent(phoneNumber);
        return phoneNumber;
    }

    private Label addSmallFontLabel(final String txt) {
        Label label = new Label(txt);
        Font smallFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        label.getStyle().setFont(smallFont);
        container.addComponent(label);
        return label;
    }

    public void setDetails(String description) {
        desc.setText(description, FontUtil.getSmallNormalFont());
    }

    public void addComponent(Component comp) {
        comp.getStyle().setMargin(Component.LEFT, 0);
        comp.getSelectedStyle().setMargin(Component.LEFT, 0);
        container.addComponent(comp);
    }
}
