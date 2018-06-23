package Dust;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import org.cardboard.dtanjp.Computer;

import Dust.api.state.*;
import Dust.state.*;
import Plugin.*;

/**
 * Dust.java
 * 
 * @author David Tan
 **/
public class Dust {

	/** Constructor **/
	private Dust() {
	}
	
	/** Singleton **/
	public static Dust GetInstance() {
		if(instance == null)
			instance = new Dust();
		return instance;
	}
	
	public void Initialize() {
		if(!initialized) {
			EventQueue.invokeLater(new Runnable() {
		        public void run() {
		        	display = Display.GetInstance();
			        display.Initialize();
			        display.frame.setVisible(true);
			        
			        states = StateManager.getInstance();
					menu = new MenuState();
					drivermenu = new DriversState();
					appMenu = new ApplicationsState();
					appState = new AppState();
					menu.SetCurrentState();
					initialized = true;
		        }
		    });
		}
	}
	
	/** Methods **/
	public void Update() {
		if(!initialized)
			return;
		display.Update();
		if(computer.REQUEST_SHUTDOWN)
			computer.REQUEST_SHUTDOWN = allowShutDown;
		
		if(states != null) {
			if(states.CurrentState() != null)
				states.CurrentState().Update();
			else {
				if(menu != null && states.CurrentState() != menu)
					states.SetCurrent(menu);
			}
		}
		
		if(!commandline.isEmpty()) {
			if(commandline.startsWith("/")) {
				String cmd = commandline.substring(1, (commandline.contains(" ") ? commandline.indexOf(" ") : commandline.length()));
				boolean arguments = commandline.contains(" ");
				String args[] = new String[0];
				if(arguments) {
					args = new String[commandline.split(" ").length-1];
					for(int i=0;i<args.length; i++)
						args[i] = commandline.split(" ")[i+1];
				}
				HandleCommand(cmd, args);
			} else if(states.CurrentState() == menu)
				menu.Println("> "+commandline);
			commandline = "";
		}
	}
	
	public void HandleRequest(Plugin plugin, String command, Object... params) {
		if(!initialized)
			return;
		
		//Pass the display to the plugin that requests it
		if(plugin != null) {
			if(command.equalsIgnoreCase("jpanel"))
				plugin.Request(Computer.getInstance().GetOS(), "", "jpanel", display);
		}
		
		if(params != null) {
			if(params.length > 0) {
				if(command.equals("println") && states.CurrentState() == menu)
					menu.Println(params[0].toString());
				
				//If the user is in the appState and the application is making requests
				if(states.CurrentState() == appState && currentApplication == plugin)
					appState.HandleRequest(plugin, command, params);
			}
		}
	}
	
	private void HandleCommand(String command, String[] args) {
		if(!initialized)
			return;
		switch(command.toLowerCase()) {
		case "classes":
			menu.Println("[OS CLASSES]");
			for(String s : Computer.getInstance().osClasses.keySet())
				menu.Println(s);
			
			break;
		case "info":
			menu.Println("[DustOS]: Version: "+Config.VERSION);
			menu.Println("[DustOS]: Made by David Tan");
			menu.Println("[DustOS]: Used as a prototype OS for CardboardPC");
			break;
		case "exit":
			Computer.getInstance().GetOS().Destruct();
			Dust.GetInstance().allowShutDown = true;
			System.out.println("[DustOS]: Shutting down...");
			break;
		case "clear":
			menu.messageBox.setText("");
			break;
		case "drivers":
			if(args == null || args.length == 0) {
				menu.Println("Current selected driver: "+selectedDriver_name);
				menu.Println("/drivers [ls] :Lists all of the drivers loaded into the OS.");
				menu.Println("/drivers <drivername> <command> :Request something from a driver.");
				menu.Println("/drivers select <drivername> :Select a driver and any future command will be pass to that driver.");
				break;
			}
			Object cmd[];
			if(args[0].equals("ls")) {
				menu.Println("[DustOS]: === [ List of registered drivers<"+computer.GetDrivers().size()+"> ] ===");
				for(String d : computer.GetDrivers().keySet()) {
					if(d == null)
						continue;
					if(d.isEmpty())
						continue;
					menu.Println("[DustOS]: "+d+" <"+(EnabledDrivers.get(d)?"ENABLED":"DISABLED")+">");
				}
			} else if(args[0].equals("select")) {
				if(args.length >= 2) {
					if(args[1].equals("null") && selectedDriver != null) {
						selectedDriver = null;
						menu.Println("[DustOS]: Deselected driver");
					} else if(computer.GetDrivers().containsKey(args[1])) {
						selectedDriver = computer.GetDrivers().get(args[1]).GetDriver();
						if(selectedDriver != null) {
							menu.Println("[DustOS]: Selected driver: <"+args[1]+">");
							selectedDriver_name = args[1].replace(".jar", "").replace(".class","");
						} else
							menu.Println("[DustOS]: Driver is disabled. Cannot select: <"+args[1]+">");
					} else if(computer.GetDrivers().containsKey(args[1]+".jar")) {
						selectedDriver = computer.GetDrivers().get(args[1]+".jar").GetDriver();
						if(selectedDriver != null) {
							selectedDriver_name = args[1].replaceAll(".class", "");
							menu.Println("[DustOS]: Selected driver: <"+args[1]+".jar>");
						} else
							menu.Println("[DustOS]: Driver is disabled. Cannot select: <"+args[1]+">");
					} else if(computer.GetDrivers().containsKey(args[1]+".class")) {
						selectedDriver = computer.GetDrivers().get(args[1]+".class").GetDriver();
						if(selectedDriver != null) {
							menu.Println("[DustOS]: Selected driver: <"+args[1]+".class>");
							selectedDriver_name = args[1].replaceAll(".jar", "");
						} else
							menu.Println("[DustOS]: Driver is disabled. Cannot select: <"+args[1]+">");
					} else //No such driver
						menu.Println("[DustOS]: Cannot find driver: <"+args[1]+">");
				} else//Insufficient parameters
					menu.Println("[DustOS]: Usage: [/drivers select <drivername | null>]");
			} else {
				if(args.length > 0) {
					if(computer.GetDrivers().containsKey(args[0])) {
						if((args.length-1) > 1) {
							cmd = new Object[args.length-1];
							for(int i=1;i<args.length;i++)
								cmd[i-1] = args[i];
							computer.GetDrivers().get(args[0]).GetDriver().Request(computer.GetOS(), "", args[1], cmd);
						} else
							computer.GetDrivers().get(args[0]).GetDriver().Request(computer.GetOS(), "", args[1], 0);
					} else if(computer.GetDrivers().containsKey(args[0]+".jar")) {
						if((args.length-1) > 1) {
							cmd = new Object[args.length-1];
							for(int i=1;i<args.length;i++)
								cmd[i-1] = args[i];
							computer.GetDrivers().get(args[0]+".jar").GetDriver().Request(computer.GetOS(), "", args[1], cmd);
						} else
							computer.GetDrivers().get(args[0]+".jar").GetDriver().Request(computer.GetOS(), "", args[1], 0);
					} else if(computer.GetDrivers().containsKey(args[0]+".class")) {
						if((args.length-1) > 1) {
							cmd = new Object[args.length-1];
							for(int i=1;i<args.length;i++)
								cmd[i-1] = args[i];
							computer.GetDrivers().get(args[0]+".class").GetDriver().Request(computer.GetOS(), "", args[1], cmd);
						} else
							computer.GetDrivers().get(args[0]+".class").GetDriver().Request(computer.GetOS(), "", args[1], 0);
					} else
						menu.Println("[DustOS]: Cannot find driver: <"+args[0]+">");
				}
			}
			break;

		case "help":
			menu.Println("[DustOS] <Help>");
			menu.Println("[/exit]: Shutsdown the computer");
			menu.Println("[/help]: Brings up this menu");
			menu.Println("[/clear]: Clears the console");
			menu.Println("[/drivers]: Control drivers manually");
			menu.Println("[/info]: Displays information about DustOS");
		default:
			if(selectedDriver != null)
				selectedDriver.Request(computer.GetOS(), "", command, (Object[])args);
			else {
				if(command.equals("help")) break;
				menu.Println("[DustOS]: Unrecognized command: "+command);
				for(String s : args)
					menu.Println("- arg: "+s);
			}
			break;
		}
	}
	
	public static boolean IsInitialized() {
		return initialized;
	}
	
	public void IssueCommand(String line) {
		if(!initialized)
			return;
		commandline = line;
	}
	
	public static Image loadImage(String img) {
		Image result = null;
		JarEntry jarEntry;
		try(JarFile jarFile = new JarFile("./OS/DustOS.jar")) {
			jarEntry = jarFile.getJarEntry(img);
			InputStream in = jarFile.getInputStream(jarEntry);
			result = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** Variables **/
	public boolean allowShutDown = false;
	public static Computer computer = Computer.getInstance();
	private static Dust instance = null;
	private static boolean initialized = false;
	private String commandline = "";
	public Map<String, Boolean> EnabledDrivers = new HashMap<>();
	public Map<String, Boolean> EnabledApplications = new HashMap<>();
	public File configFile = new File("./OS/"+Config.APP_TITLE+"/"+Config.APP_TITLE+".cfg");
	public File configDir = new File("./OS/"+Config.APP_TITLE+"/");
	public boolean disableTitleBar = true;
	
	//The current application running
	public Plugin currentApplication = null;
	public Plugin selectedDriver = null;
	public String selectedDriver_name = "null";
	
	//GUI
	public static Display display = null;
	
	//States
	public static StateManager states = null;
	public MenuState menu = null;
	public DriversState drivermenu = null;
	public ApplicationsState appMenu = null;
	public AppState appState = null;
}
