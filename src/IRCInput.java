import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class IRCInput extends ListenerAdapter<PircBotX> implements Listener<PircBotX>{
	
	private static PircBotX bot;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		Configuration<PircBotX> config = new Configuration.Builder()
	    .setName("IrcBot")
	    .setLogin("none")
	    .setAutoNickChange(true)
	    .setServer("irc.esper.net",5555)
	    .addAutoJoinChannel("#irctest")
	    .buildConfiguration();
		config.getListenerManager().addListener(new IRCInput());
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
	
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		System.out.println(event.getMessage());
		event.respond("to you too");
		event.getBot().sendIRC().message(event.getChannel().getName(), event.getMessage());
		event.getBot().sendIRC().changeNick("&#04");
		
	}

}
