package Dust.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class DustButton extends JButton {

	/** Generate serial version UID **/
	private static final long serialVersionUID = 6008098947916649984L;
	
	/** Constructor **/
	public DustButton(String name) {
		setText(name);
		setFocusable(false);
		setBackground(Color.BLACK);
		setForeground(Color.CYAN);
		setBorder(BorderFactory.createLineBorder(Color.CYAN));
		//setHorizontalAlignment(SwingConstants.CENTER);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pressed = true;
			}
			
		});
	}
	
	public boolean isPressed() {
		if(isEnabled() && isVisible()) {
			boolean result = pressed;
			pressed = false;
			return result;
		}
		return false;
	}
	
	/** Variables **/
	private boolean pressed = false;
}
