package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.PositionHandler;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PositionManager implements Globals
{
    //
    private final PositionHandler handler;

    /**
     *
     *
     */
    public PositionManager()
    {
        handler = new PositionHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(double x, double y, double z)
    {
        Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y, z, isOnGround()));
        setPositionClient(x, y, z);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public void setPositionClient(double x, double y, double z)
    {
        if (mc.player != null && mc.world != null)
        {
            mc.player.setPosition(x, y, z);
        }
    }

    /**
     *
     *
     * @return
     */
    public Vec3d getPos()
    {
        return new Vec3d(getX(), getY(), getZ());
    }

    /**
     *
     *
     * @return
     */
    public Vec3d getEyePos()
    {
        EntityPose pose = mc.player.getPose();
        return getPos().add(0.0, mc.player.getActiveEyeHeight(pose,
                mc.player.getDimensions(pose)), 0.0);
    }

    public double getX()
    {
        return handler.getX();
    }

    public double getY()
    {
        return handler.getY();
    }

    public double getZ()
    {
        return handler.getZ();
    }

    /**
     *
     *
     * @return
     */
    public boolean isSneaking()
    {
        return handler.isSneaking();
    }

    /**
     *
     *
     * @return
     */
    public boolean isSprinting()
    {
        return handler.isSprinting();
    }

    /**
     *
     * @return
     */
    public boolean isOnGround()
    {
        return handler.isOnGround();
    }
}