package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.DecodePacketEvent;

/**
 * @author linus
 * @since 1.0
 */
public class AntiBookBanModule extends ToggleModule {

    /**
     *
     */
    public AntiBookBanModule() {
        super("AntiBookBan", "Prevents getting book banned (Causes connection issues, only use if actually book banned)", ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onDecodePacket(DecodePacketEvent event) {
        event.cancel();
    }
}
