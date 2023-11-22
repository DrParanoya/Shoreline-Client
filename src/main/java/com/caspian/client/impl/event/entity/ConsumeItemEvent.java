package com.caspian.client.impl.event.entity;

import com.caspian.client.api.event.Event;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ConsumeItemEvent extends Event
{
    //
    private final ItemStack activeItemStack;

    public ConsumeItemEvent(ItemStack activeItemStack)
    {
        this.activeItemStack = activeItemStack;
    }

    public ItemStack getStack()
    {
        return activeItemStack;
    }

    public Item getItem()
    {
        return activeItemStack.getItem();
    }
}
