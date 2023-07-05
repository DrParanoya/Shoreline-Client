package com.caspian.client.api.handler.rotation;

import com.caspian.client.api.module.Module;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationRequest implements Comparable<RotationRequest>
{
    //
    private final Module requester;
    private final long time;
    private final int priority;
    //
    private float yaw, pitch;

    /**
     *
     *
     * @param requester
     * @param priority
     * @param yaw
     * @param pitch
     */
    public RotationRequest(Module requester,
                           RotationPriority priority,
                           float yaw,
                           float pitch)
    {
        this.requester = requester;
        this.time = System.currentTimeMillis();
        this.priority = priority.getPriority();
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     *
     *
     * @param requester
     * @param yaw
     * @param pitch
     */
    public RotationRequest(Module requester,
                           float yaw,
                           float pitch)
    {
        this(requester, RotationPriority.NORMAL, yaw, pitch);
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     */
    @Override
    public int compareTo(RotationRequest other)
    {
        if (priority == other.getPriority())
        {
            return Long.compare(time, other.getTime());
        }
        return Integer.compare(priority, other.getPriority());
    }

    public Module getRequester()
    {
        return requester;
    }

    /**
     *
     *
     * @return
     */
    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }

    public int getPriority()
    {
        return priority;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }
}
