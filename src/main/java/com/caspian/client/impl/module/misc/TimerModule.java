package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.render.TickCounterEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;

import java.text.DecimalFormat;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TimerModule extends ToggleModule
{
    //
    Config<Float> ticksConfig = new NumberConfig<>("Ticks", "Tick speed",
            0.1f, 2.0f, 50.0f);
    Config<Boolean> tpsSyncConfig = new BooleanConfig("TPSSync", "Syncs game " +
            "tick speed to server tick speed", false);
    //
    private float prevTimer = -1.0f;
    private float timer = 1.0f;

    /**
     *
     */
    public TimerModule()
    {
        super("Timer", "Changes the client tick speed",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        DecimalFormat decimal = new DecimalFormat("0.0#");
        return decimal.format(timer);
    }

    /**
     *
     */
    @Override
    public void toggle()
    {
        Modules.SPEED.setPrevTimer();
        if (Modules.SPEED.isUsingTimer())
        {
            return;
        }
        super.toggle();
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (Modules.SPEED.isUsingTimer())
            {
                return;
            }
            if (tpsSyncConfig.getValue())
            {
                timer = Math.max(Managers.TICK.getTpsCurrent() / 20.0f, 0.1f);
                return;
            }
            timer = ticksConfig.getValue();
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTickCounter(TickCounterEvent event)
    {
        if (timer != 1.0f)
        {
            event.cancel();
            event.setTicks(timer);
        }
    }

    /**
     *
     */
    public void resetTimer()
    {
        if (prevTimer > 0.0f)
        {
            this.timer = prevTimer;
            prevTimer = -1.0f;
        }
    }

    /**
     *
     *
     * @param timer
     */
    public void setTimer(float timer)
    {
        prevTimer = this.timer;
        this.timer = timer;
    }
}
