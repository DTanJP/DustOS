
import org.cardboard.dtanjp.Computer;
import org.cardboard.dtanjp.Config;

import Dust.Dust;
import Plugin.Driver;
import Plugin.Plugin;
import fileIO.ReadingWorker;
import fileIO.WritingWorker;

/**
 * MainApp.java
 * DustOS:
 * A simple basic OS for virtual computer
 * @author David Tan
 **/
public class MainApp implements Plugin {

	@Override
	public boolean RequestShutDown() {
		return Dust.GetInstance().allowShutDown;
	}
	
	@Override
	public void Destruct() {
		//Shuts down all drivers
		for(Driver d : Computer.getInstance().GetDrivers().values()) {
			if(d == null)
				continue;
			if(d.GetDriver() == null)
				continue;
			d.GetDriver().Destruct();
		}
		//Shuts down the current application
		if(Dust.GetInstance().currentApplication != null)
			Dust.GetInstance().currentApplication.Destruct();
	}

	@Override
	public void Initialize() {
		WritingWorker.ClearFile(Config.OUTPUT_LOG);
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.translaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");
		dust = Dust.GetInstance();
		if(SetupRequired()) {
			if(!dust.configDir.exists() || !dust.configDir.isDirectory())
				dust.configDir.mkdir();
			if(!dust.configFile.exists() || !dust.configFile.isFile()) {
				WritingWorker.CreateFile(dust.configFile.getPath());
				WritingWorker.WriteFile(dust.configFile, 
						"Enable [ERROR-LOG]: "+Config.ENABLE_ERROR_LOG, 
						"Enable [Title bar]: "+!dust.disableTitleBar);
			}
			initialSetup = true;
		}
		if(!initialSetup) {
			Config.ENABLE_ERROR_LOG = ReadingWorker.FindLine(dust.configFile, "[ERROR-LOG]").toLowerCase().endsWith("true");
			dust.disableTitleBar = ReadingWorker.FindLine(dust.configFile, "[Title bar]").toLowerCase().endsWith("false");
		}
		dust.Initialize();
		//Determine which drivers are disabled
		for(String d : Computer.getInstance().GetDrivers().keySet()) {
			if(Computer.getInstance().GetDrivers().get(d) != null) {
				Computer.getInstance().GetDrivers().get(d).InitiateDriverClass();
				boolean status = Computer.getInstance().GetDrivers().get(d).GetDriver() != null;
				Dust.GetInstance().EnabledDrivers.put(d, status);
			}
		}
		//Determine which applications are disabled
		for(String a : Computer.getInstance().GetApplications().keySet()) {
			if(Computer.getInstance().GetApplications().get(a) != null) {
				Computer.getInstance().GetApplications().get(a).InitiateApplicationClass();
				boolean status = Computer.getInstance().GetApplications().get(a).GetApplication() != null;
				Dust.GetInstance().EnabledApplications.put(a, status);
			}
		}
		//Initializes all drivers
		for(Driver d : Computer.getInstance().GetDrivers().values()) {
			if(d == null)
				continue;
			if(d.GetDriver() == null)
				continue;
			if(Dust.GetInstance().EnabledDrivers.get(d.Name()))
				d.GetDriver().Initialize();
		}
	}

	@Override
	public void OnDisable() {
	}

	@Override
	public void OnEnable() {
	}

	@Override
	public void Request(Plugin sender, String plugin, String command, Object... param) {
		//If the request is made towards the OS
		if(plugin.equalsIgnoreCase("OS")) {
			dust.HandleRequest(sender, command, param);
			return;
		}
		
		//Send to drivers otherwise send to application
		if(plugin.length() > 0) {
			if(command == null || command.length() == 0)
				return;
			
			if(Computer.getInstance().GetDrivers().containsKey(plugin))
				Computer.getInstance().GetDrivers().get(plugin).GetDriver().Request(sender, plugin, command, param);
			else if(Computer.getInstance().GetApplications().containsKey(plugin))
				Computer.getInstance().GetApplications().get(plugin).GetApplication().Request(sender, plugin, command, param);
		}
	}

	@Override
	public void Update() {
		dust.Update();
		//Updates all drivers
		for(Driver d : Computer.getInstance().GetDrivers().values()) {
			if(d == null)
				continue;
			if(d.GetDriver() == null)
				continue;
			d.GetDriver().Update();
		}
	}
	
	/** Check to see if anything needs to be setup **/
	private boolean SetupRequired() {
		int errors = 0;
		if(!dust.configDir.exists() || !dust.configDir.isDirectory())
			errors++;
		if(!dust.configFile.exists() || !dust.configFile.isFile())
			errors++;
		return errors > 0;
	}
	
	/** Variables **/
	public static Dust dust;
	private boolean initialSetup = false;
}
