package org.rsbot;

import org.rsbot.bot.Bot;
import org.rsbot.gui.BotGUI;
import org.rsbot.log.LogOutputStream;
import org.rsbot.log.SystemConsoleHandler;
import org.rsbot.security.RestrictedSecurityManager;
import org.rsbot.util.Extractor;
import org.rsbot.util.GlobalConfiguration;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
	private static BotGUI gui;

	public static void main(final String[] args) throws Exception {
		bootstrap();
		new Extractor().run();
		commands(args);
		System.setSecurityManager(new RestrictedSecurityManager());
		System.setProperty("java.io.tmpdir", GlobalConfiguration.Paths.getGarbageDirectory());
		gui = new BotGUI();
		gui.setVisible(true);
		gui.addBot();
	}

	private static void commands(final String[] args) {
		if (args.length > 1) {
			if (args[0].toLowerCase().endsWith("delete")) {
				final File jarOld = new File(args[1]);
				if (jarOld.exists()) {
					if (!jarOld.delete()) {
						jarOld.deleteOnExit();
					}
				}
			}
		}
	}

	/**
	 * Returns the Bot for any object loaded in its client. For internal use
	 * only (not useful for script writers).
	 *
	 * @param o Any object from within the client.
	 * @return The Bot for the client.
	 */
	public static Bot getBot(final Object o) {
		return gui.getBot(o);
	}

	/**
	 * Returns the size of the panel that clients should be drawn into. For
	 * internal use.
	 *
	 * @return The client panel size.
	 */
	public static Dimension getPanelSize() {
		return gui.getPanel().getSize();
	}

	private static void bootstrap() {
		Logger.getLogger("").setLevel(Level.ALL);
		Logger.getLogger("").addHandler(new SystemConsoleHandler());
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private final Logger log = Logger.getLogger("EXCEPTION");

			public void uncaughtException(final Thread t, final Throwable e) {
				log.logp(Level.SEVERE, "EXCEPTION", "", "Unhandled exception in thread " + t.getName() + ": ", e);
			}
		});
		System.setErr(new PrintStream(new LogOutputStream(Logger.getLogger("STDERR"), Level.SEVERE), true));
	}
}
