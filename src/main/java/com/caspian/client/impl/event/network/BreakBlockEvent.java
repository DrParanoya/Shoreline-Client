package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.util.math.BlockPos;

@Cancelable
public class BreakBlockEvent extends Event
{
    private final BlockPos pos;

    public BreakBlockEvent(BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }
}
