import java.awt.AWTException;
import java.awt.Robot;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.xml.crypto.KeySelector;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class IRCInput extends ListenerAdapter<PircBotX> implements Listener<PircBotX>{
	
	private static PircBotX bot;
	private Map<String, Properties> settings = new HashMap<String, Properties>();
	private Map<String, Integer> keys = new HashMap<String, Integer>();
	String channel, name, address;
	int port;
	Robot robot;

	/**
	 * @param args
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws AWTException, IOException {
		new IRCInput();
	}
	
	public IRCInput() throws AWTException, IOException{
		for(int i = 0; i < KeyEvent.KEY_LAST; i++){
			keys.put(KeyEvent.getKeyText(i).toLowerCase(), i);
			System.out.println(KeyEvent.getKeyText(i).toLowerCase());
		}
		robot = new Robot();
		loadSettings();
		try {
			Configuration<PircBotX> config = new Configuration.Builder()
		    .setName(settings.get("settings").getProperty("bot_name"))
		    .setLogin("none")
		    .setAutoNickChange(true)
		    .setServer(settings.get("settings").getProperty("ip"),Integer.parseInt(settings.get("settings").getProperty("port")))
		    .addAutoJoinChannel(settings.get("settings").getProperty("channel"))
		    .buildConfiguration();
			config.getListenerManager().addListener(this);
			bot = new PircBotX(config);
				bot.startBot();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IrcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

	}
	
	private void loadSettings() throws IOException{
		DataInputStream in = new DataInputStream(new FileInputStream(new File("settings.conf")));
		String region = "none";
		Properties props = new Properties();
		for(String s = ""; (s=in.readLine()) != null;){
			if(s.matches("\\[([^\\]]){1,}\\](^.){0,}")){
				System.out.println(s);
				settings.put(region, props);
				region = s.substring(1, s.length()-1);
				props = new Properties();
			}else if(s.matches("[^:]{1,}[:][^:]{1,}")){
				String[] line = s.split(":");
				props.put(line[0], line[1]);
			}
			
		}
		settings.put(region, props);
	}
	
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		String message = event.getMessage();
		System.out.println(message);
		if(settings.get("keybinds").containsKey(message)){
		int keyCode  = keys.get(settings.get("keybinds").get(message));
		robot.keyPress(keyCode);
		Thread.sleep(10);
		robot.keyRelease(keyCode);
		}
	}

}
