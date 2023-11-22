package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.player.PlayerJumpEvent;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.world.BlockCollisionEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.mixin.accessor.AccessorKeyBinding;
import com.caspian.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShapes;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class JesusModule extends ToggleModule
{
    //
    Config<JesusMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "walking on water", JesusMode.SOLID, JesusMode.values());
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "NCP Updated " +
            "bypass for floating offsets", false,
            () -> modeConfig.getValue() == JesusMode.SOLID);
    //
    private int floatTimer = 1000;
    private boolean fluidState;
    //
    private double floatOffset;

    /**
     *
     */
    public JesusModule()
    {
        super("Jesus", "Allow player to walk on water", ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        floatOffset = 0.0;
        //
        floatTimer = 1000;
        KeyBinding.setKeyPressed(((AccessorKeyBinding) mc.options.jumpKey).getBoundKey(), false);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onBlockCollision(BlockCollisionEvent event)
    {
        BlockState state = event.getState();
        if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled()
                || mc.player.isSpectator() || mc.player.isOnFire()
                || state.getFluidState().isEmpty())
        {
            return;
        }
        if (modeConfig.getValue() != JesusMode.DOLPHIN
                && ((state.getBlock() == Blocks.WATER
                | state.getFluidState().getFluid() == Fluids.WATER)
                || state.getBlock() == Blocks.LAVA))
        {
            event.cancel();
            event.setVoxelShape(VoxelShapes.fullCube());
            if (mc.player.getVehicle() != null)
            {
                event.setVoxelShape(VoxelShapes.cuboid(new Box(0.0, 0.0, 0.0, 1.0,
                        0.949999988079071, 1.0)));
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerJump(PlayerJumpEvent event)
    {
        if (!isInFluid() && isOnFluid())
        {
            event.cancel();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled())
            {
                return;
            }
            if (modeConfig.getValue() == JesusMode.SOLID)
            {
                if (!strictConfig.getValue())
                {
                    floatOffset = floatOffset == 0.05 ? 0.0 : 0.05;
                }
                if (isInFluid() || mc.player.fallDistance > 3.0f
                        || mc.player.isSneaking())
                {
                    floatOffset = 0.0;
                }
                if (!mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed())
                {
                    if (isInFluid())
                    {
                        floatTimer = 0;
                        Managers.MOVEMENT.setMotionY(0.11);
                        return;
                    }
                    if (floatTimer == 0)
                    {
                        Managers.MOVEMENT.setMotionY(0.30);
                    }
                    else if (floatTimer == 1)
                    {
                        Managers.MOVEMENT.setMotionY(0.0);
                    }
                    floatTimer++;
                }
            }
            else if (modeConfig.getValue() == JesusMode.DOLPHIN && isInFluid()
                    && !mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed())
            {
                KeyBinding.setKeyPressed(((AccessorKeyBinding) mc.options.jumpKey).getBoundKey(), true);
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onMovementPackets(MovementPacketsEvent event)
    {
        if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled())
        {
            return;
        }
        if (event.getStage() == EventStage.PRE
                && modeConfig.getValue() == JesusMode.TRAMPOLINE)
        {
            boolean inFluid = getFluidBlockInBB(mc.player.getBoundingBox()) != null;
            if (inFluid && !mc.player.isSneaking())
            {
                mc.player.setOnGround(false);
            }
            Block block = mc.world.getBlockState(new BlockPos((int) Math.floor(mc.player.getX()),
                    (int) Math.floor(mc.player.getY()),
                    (int) Math.floor(mc.player.getZ()))).getBlock();
            if (fluidState && !mc.player.getAbilities().flying && !mc.player.isTouchingWater())
            {
                if (mc.player.getVelocity().y < -0.3 || mc.player.isOnGround()
                        || mc.player.isHoldingOntoLadder())
                {
                    fluidState = false;
                    return;
                }
                Managers.MOVEMENT.setMotionY(mc.player.getVelocity().y / 0.9800000190734863 + 0.08);
                Managers.MOVEMENT.setMotionY(mc.player.getVelocity().y - 0.03120000000005);
            }
            if (isInFluid())
            {
                Managers.MOVEMENT.setMotionY(0.1);
            }
            if (!isInFluid() && block instanceof FluidBlock
                    && mc.player.getVelocity().y < 0.2)
            {
                Managers.MOVEMENT.setMotionY(0.5);
                fluidState = true;
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.isClientPacket() || mc.player == null || mc.getNetworkHandler() == null
                || mc.player.age <= 20 || Modules.FLIGHT.isEnabled()
                || Modules.PACKET_FLY.isEnabled())
        {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet
                && packet.changesPosition()
                && modeConfig.getValue() == JesusMode.SOLID && !isInFluid()
                && isOnFluid() && mc.player.fallDistance <= 3.0f)
        {
            double y = packet.getY(mc.player.getY());
            ((AccessorPlayerMoveC2SPacket) packet).hookSetY(y - floatOffset);
            if (strictConfig.getValue())
            {
                floatOffset += 0.12;
                if (floatOffset > 0.4)
                {
                    floatOffset = 0.2;
                }
            }
        }
    }

    public boolean isInFluid()
    {
        return mc.player.isTouchingWater() || mc.player.isInLava();
    }

    /**
     *
     * @param box
     * @return
     */
    public BlockState getFluidBlockInBB(Box box)
    {
        return getFluidBlockInBB(MathHelper.floor(box.minY - 0.2));
    }

    /**
     *
     * @param minY
     * @return
     */
    public BlockState getFluidBlockInBB(int minY)
    {
        for(int i = MathHelper.floor(mc.player.getBoundingBox().minX); i < MathHelper.ceil(mc.player.getBoundingBox().maxX); i++)
        {
            for(int j = MathHelper.floor(mc.player.getBoundingBox().minZ); j < MathHelper.ceil(mc.player.getBoundingBox().maxZ); j++)
            {
                BlockState state = mc.world.getBlockState(new BlockPos(i, minY, j));
                if (state.getBlock() instanceof FluidBlock)
                {
                    return state;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public boolean isOnFluid()
    {
        if (mc.player.fallDistance >= 3.0f)
        {
            return false;
        }
        final Box bb = mc.player.getVehicle() != null ?
                mc.player.getVehicle().getBoundingBox().contract(0.0, 0.0, 0.0)
                        .offset(0.0, -0.05000000074505806, 0.0) :
                mc.player.getBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0,
                        -0.05000000074505806, 0.0);
        boolean onLiquid = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); x++)
        {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); z++)
            {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR)
                {
                    if (!(block instanceof FluidBlock))
                    {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public enum JesusMode
    {
        SOLID,
        DOLPHIN,
        TRAMPOLINE
    }
}
