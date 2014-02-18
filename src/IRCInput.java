import java.awt.AWTException;
import java.awt.Robot;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class IRCInput extends ListenerAdapter<PircBotX> implements Listener<PircBotX>{
	
	private static PircBotX bot;
	private Map<String, Integer> keyBinds = new HashMap<String, Integer>();
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
		robot = new Robot();
		loadSettings();
		try {
			Configuration<PircBotX> config = new Configuration.Builder()
		    .setName(name)
		    .setLogin("none")
		    .setAutoNickChange(true)
		    .setServer(address,port)
		    .addAutoJoinChannel(channel)
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
		for(String s = ""; (s=in.readLine()) != null;){
			if(s.equals("[settings]")){
				region = "settings";
				continue;
			}
			if(s.equals("[keybinds]")){
				region = "keybinds";
				continue;
			}
			
			String[] line = s.split(":");
			if(region.equals("settings")){
				if(line[0].equals("ip")){
					address = line[1];
				}else if(line[0].equals("port")){
					port = Integer.parseInt(line[1]);
				}else if(line[0].equals("channel")){
					channel = line[1];
				}else if(line[0].equals("bot_name")){
					name = line[1];
				}
			}else if(region.equals("keybinds")){
				keyBinds.put(line[0], KeyEvent.getExtendedKeyCodeForChar(line[1].charAt(0)));
			}
			
		}
	}
	
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		String message = event.getMessage();
		System.out.println(message);
		if(keyBinds.containsKey(message)){
		robot.keyPress(keyBinds.get(message));
		Thread.sleep(10);
		robot.keyRelease(keyBinds.get(message));
		}
	}

}
