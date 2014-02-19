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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.xml.crypto.KeySelector;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;

public class IRCInput extends ListenerAdapter<PircBotX> implements
		Listener<PircBotX> {

	private static PircBotX bot;
	private Map<String, Properties> settings = new HashMap<String, Properties>();
	private Map<String, Integer> keys = new HashMap<String, Integer>();
	private ArrayList<User> authenticatedUsers = new ArrayList<User>();
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

	public IRCInput() throws AWTException, IOException {
		for (int i = 0; i < KeyEvent.KEY_LAST; i++) {
			keys.put(KeyEvent.getKeyText(i).toLowerCase(), i);
		}
		robot = new Robot();
		loadSettings();
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Configuration<PircBotX> config = new Configuration.Builder()
					.setName(getProperty("settings", "bot_name"))
					.setLogin(getProperty("settings", "bot_login"))
					.setAutoNickChange(true)
					.setServer(getProperty("settings", "ip"),
							Integer.parseInt(getProperty("settings", "port")))
					.addAutoJoinChannel(getProperty("settings", "channel"))
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

	private void loadSettings() throws IOException {
		Scanner file = new Scanner(new File(
				"settings.conf"));
		String region = "none";
		Properties props = new Properties();
		while(file.hasNext()) {
			String s = file.nextLine();
			if (s.matches("\\[([^\\]]){1,}\\](^.){0,}")) {
				System.out.println(s);
				settings.put(region, props);
				region = s.substring(1, s.length() - 1);
				props = new Properties();
			} else if (s.matches("[^:]{1,}[:][^:]{1,}")) {
				String[] line = s.split(":");
				props.put(line[0], line[1]);
			}

		}
		settings.put(region, props);
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
			throws Exception {
		if (Boolean.parseBoolean(getProperty("settings", "use_password"))) {
			if (authenticatedUsers.contains(event.getUser())) {
				event.respond("You have already authenticated");
			} else if (event.getMessage().equals(
					getProperty("settings", "password"))) {
				authenticatedUsers.add(event.getUser());
				event.respond("Youu have successfuly authenticated");
			} else {
				event.respond("Incorrect Password");
			}
		}
	}

	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception {
		if (event.getUser() != bot.getUserBot()) {
			event.respond(getProperty("channel", "join_message").replaceAll(
					"%n", event.getUser().getNick()));
		}
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		String message = event.getMessage();
		System.out.println(message);
		if (Boolean.parseBoolean(getProperty("settings", "use_password"))) {
			if (!authenticatedUsers.contains(event.getUser())) {
				event.respond("You can not use that command. You must first PM me the password you were given by the admin");
				return;
			}
		}
		if (settings.get("keybinds").containsKey(message)) {
			int keyCode = keys.get(getProperty("keybinds", message));
			robot.keyPress(keyCode);
			Thread.sleep(10);
			robot.keyRelease(keyCode);
		}
	}

	private String getProperty(String section, Object key) {
		return (String) settings.get(section).get(key);
	}

}
