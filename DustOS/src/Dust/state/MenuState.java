package Dust.state;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.*;

import org.cardboard.dtanjp.Computer;

import Dust.Config;
import Dust.Display;
import Dust.Dust;
import Dust.MenuOutputStream;
import Dust.api.state.State;
import Dust.gui.DustButton;

public class MenuState extends State {

	public MenuState() {
		button = new JButton("Shutdown");
		button.setBounds(690, 30, 100, 25);
		button.setFocusable(false);
		button.setBackground(Color.BLACK);
		button.setForeground(Color.CYAN);
		button.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		drivermenu = new DustButton("<html>[Drivers]<br/>==["+Computer.getInstance().GetDrivers().size()+"]==</html>");
		drivermenu.setBounds(20, 200, 100, 100);
		
		appMenu = new DustButton("<html>[Applications]<br/>==["+Computer.getInstance().GetApplications().size()+"]==</html>");
		appMenu.setBounds(140, 200, 100, 100);
		
		infomenu = new DustButton("<html>[Info]</html>");
		infomenu.setBounds(260, 200, 100, 100);
		
		cmdinput = new JTextField("");
		cmdinput.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		cmdinput.setBounds(0, 735, 794, 30);
		cmdinput.setBackground(Color.BLACK);
		cmdinput.setCaretColor(Color.GREEN);
		cmdinput.setSelectedTextColor(Color.WHITE);
		cmdinput.setDisabledTextColor(Color.GRAY);
		cmdinput.setSelectionColor(Color.BLUE);
		cmdinput.setForeground(Color.CYAN);
		cmdinput.setFont(new Font("Serif", Font.BOLD, 16));
		
		messageBox = new JTextArea("");
		messageBox.setEditable(false);
		messageBox.setBackground(Color.BLUE);
		messageBox.setForeground(Color.CYAN);
		messageBox.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		messageBox.setBounds(5, 450, 780, 280);
		messageBox.setLineWrap(true);
		messageBox.setFont(new Font("TimesRoman", Font.BOLD, 16));
		messageBox.setAutoscrolls(true);
		PrintStream printStream = new PrintStream(new MenuOutputStream(messageBox));
		System.setOut(printStream);
		System.setErr(printStream);
		
		scrollpane = new JScrollPane(messageBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBackground(Color.BLACK);
		scrollpane.setForeground(Color.CYAN);
		scrollpane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		scrollpane.setAutoscrolls(true);
		scrollpane.setBounds(5, 450, 780, 280);
		scrollpane.setWheelScrollingEnabled(true);
		cmdinput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					if(!e.getActionCommand().isEmpty())
						Dust.GetInstance().IssueCommand(e.getActionCommand());
					cmdinput.setText("");
			}
		});
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Computer.getInstance().GetOS().Destruct();
				Dust.GetInstance().allowShutDown = true;
				System.out.println("[DustOS]: Shutting down...");
			}
			
		});
		drivermenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dust.GetInstance().drivermenu.SetCurrentState();
			}
			
		});
		appMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dust.GetInstance().appMenu.SetCurrentState();
			}
			
		});
		AddComponent("InfoMenubtn", infomenu);
		AddComponent("DriverMenubtn", drivermenu);
		AddComponent("AppMenubtn", appMenu);
		AddComponent("Scrollpane", scrollpane);
		AddComponent("Shutdownbtn", button);
		AddComponent("Commandline", cmdinput);
	}
	
	@Override
	public void Enter() {
	}
	
	@Override
	public void Update() {
		if(infomenu.isPressed()) {
			messageBox.setText("");
			System.out.println("[DustOS Information]");
			System.out.println("===========================================");
			System.out.println("[Info]: This is a prototype OS for a prototype virtual PC application.");
			System.out.println("[Info]: Drivers are plugins that are continuously being runned.");
			System.out.println("[Info]: Applications are plugins that can run while being focused and only 1 at a time.");
			System.out.println("===========================================");
			System.out.println("[ Commands ]: type '/' followed by the command");
			System.out.println("[Example]: /exit");
			System.out.println("===========================================");
			System.out.println("[classes]: Displays all the OS classes.");
			System.out.println("[info]: Displays version.");
			System.out.println("[exit]: Shuts down the OS and the PC.");
			System.out.println("[clear]: Clears the console.");
			System.out.println("[drivers]: Displays driver information.");
			System.out.println("[help]: Displays commands.");
			System.out.println("* NOTE *");
			System.out.println("Drivers can have their own commands. They extend the functionality of the OS.");
		}
	}

	@Override
	public void Render(Graphics2D g) {
		//Background
		Display.GetInstance().setBackground(Color.BLACK);
		
		//Debug info
		g.setColor(Color.CYAN);
		g.setFont(new Font("TimesRoman", Font.BOLD, 16));
		g.drawString("Computer: "+org.cardboard.dtanjp.Config.COMPUTER_NAME, 5, 75);
		g.drawString("Computer Version: "+org.cardboard.dtanjp.Config.VERSION, 5, 90);
		g.drawString("FPS: "+org.cardboard.dtanjp.Config.FPS, 5, 120);
		
		g.setColor(Color.CYAN);
		g.setFont(new Font("TimesRoman", Font.BOLD, 24));
		g.drawString("[DUST OS]", 5, 50);
		g.drawLine(0, 60, Config.APP_WIDTH, 60);
		
	}

	public void Println(String line) {
		messageBox.append(" "+line+"\n");
		//Auto scroll to bottom of text area
		scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
	}
	
	@Override
	public void Exit() {
	}
	
	/** Variables **/
	public JTextField cmdinput = null;
	public JButton button = null;
	public JTextArea messageBox = null;
	private JScrollPane scrollpane = null;
	private JButton drivermenu = null, appMenu = null;
	private DustButton infomenu = null;
}
