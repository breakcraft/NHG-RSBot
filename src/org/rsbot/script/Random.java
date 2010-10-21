package org.rsbot.script;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Methods;

import java.awt.*;

public abstract class Random extends Methods implements PaintListener {

    private volatile boolean enabled = true;

	private Script script;

    protected String name;

    /**
     * Detects whether or not this anti-random should
     * activate.
     *
     * @return <tt>true</tt> if the current script
     * should be paused and control passed to this
     * anti-random's loop.
     */
    public abstract boolean activateCondition();

    public abstract int loop();
    
    
    /**
     * Called after the method providers for this Random
     * become available for use in initialization. 
     */
    public void onStart() {
    	
    }

	public void onFinish() {

	}

    /**
     * Override to provide a time limit in seconds for
     * this anti-random to complete.
     *
     * @return The number of seconds after activateCondition
     *         returns <tt>true</tt> before the anti-random should be
     *         detected as having failed. If this time is reached
     *         the random and running script will be stopped.
     */
    public int getTimeout() {
        return 0;
    }
    
    @Override
    public final void init(MethodContext ctx) {
    	super.init(ctx);
    	onStart();
    }

    public final boolean isActive() {
        return script != null;
    }
    
    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	/**
	 * Stops the current script; player can be logged out before
	 * the script is stopped.
	 *
	 * @param logout <tt>true</tt> if the player should be logged
	 * out before the script is stopped.
	 */
	protected void stopScript(boolean logout) {
		script.stopScript(logout);
	}

    public final boolean run(Script ctx) {
        if (!activateCondition()) {
            return false;
        }
		script = ctx;
        name = getClass().getAnnotation(ScriptManifest.class).name();
		ctx.ctx.bot.getEventManager().removeListener(ctx);
		for (Script s : ctx.delegates) {
			ctx.ctx.bot.getEventManager().removeListener(s);
		}
		ctx.ctx.bot.getEventManager().addListener(this);
        log("Random event started: " + name);
        int timeout = getTimeout();
        if (timeout > 0) {
            timeout *= 1000;
            timeout += System.currentTimeMillis();
        }
        while (ctx.isActive()) {
            try {
                int wait = loop();
                if (wait == -1) {
                    break;
                } else if (timeout > 0 && System.currentTimeMillis() >= timeout) {
                    log.warning("Time limit reached for " + name + ".");
                    ctx.stopScript();
                } else {
					sleep(wait);
				}
            } catch (Exception ex) {
                log.severe(ex.toString());
                break;
            }
        }
		script = null;
        onFinish();
        log("Random event finished: " + name);
		ctx.ctx.bot.getEventManager().removeListener(this);
		ctx.ctx.bot.getEventManager().addListener(ctx);
		for (Script s : ctx.delegates) {
			ctx.ctx.bot.getEventManager().addListener(s);
		}
		return true;
	}

	public final void onRepaint(Graphics g) {
		Point p = mouse.getLocation();
		int w = game.getWidth(), h = game.getHeight();
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, p.x - 1, p.y - 1);
		g.fillRect(p.x + 1, 0, w - (p.x + 1), p.y - 1);
		g.fillRect(0, p.y + 1, p.x - 1, h - (p.y - 1));
		g.fillRect(p.x + 1, p.y + 1, w - (p.x + 1), h - (p.y - 1));
	}

}
