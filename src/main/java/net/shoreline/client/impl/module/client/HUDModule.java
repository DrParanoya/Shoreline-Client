package net.shoreline.client.impl.module.client;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.shoreline.client.ShorelineMod;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.api.render.anim.Animation;
import net.shoreline.client.impl.event.gui.hud.RenderOverlayEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.render.ColorUtil;
import net.shoreline.client.util.string.EnumFormatter;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
public class HUDModule extends ToggleModule {

    //
    // private static final HudScreen HUD_SCREEN = new HudScreen();
    //
    Config<Boolean> watermarkConfig = new BooleanConfig("Watermark", "Displays client name and version watermark", true);
    Config<Boolean> directionConfig = new BooleanConfig("Direction", "Displays facing direction", true);
    Config<Boolean> armorConfig = new BooleanConfig("Armor", "Displays player equipped armor and durability", true);
    Config<VanillaHud> potionHudConfig = new EnumConfig<>("PotionHud", "Renders the Minecraft potion Hud", VanillaHud.HIDE, VanillaHud.values());
    Config<VanillaHud> itemNameConfig = new EnumConfig<>("ItemName", "Renders the Minecraft item name display", VanillaHud.HIDE, VanillaHud.values());
    Config<Boolean> potionEffectsConfig = new BooleanConfig("PotionEffects", "Displays active potion effects", true);
    Config<Boolean> potionColorsConfig = new BooleanConfig("PotionColors", "Displays active potion colors", true);
    Config<Boolean> durabilityConfig = new BooleanConfig("Durability", "Displays the current held items durability", false);
    Config<Boolean> coordsConfig = new BooleanConfig("Coords", "Displays world coordinates", true);
    Config<Boolean> netherCoordsConfig = new BooleanConfig("NetherCoords", "Displays nether coordinates", true);
    Config<Boolean> serverBrandConfig = new BooleanConfig("ServerBrand", "Displays the current server brand", false);
    Config<Boolean> speedConfig = new BooleanConfig("Speed", "Displays the current movement speed of the player in kmh", true);
    Config<Boolean> pingConfig = new BooleanConfig("Ping", "Display server response time in ms", true);
    Config<Boolean> tpsConfig = new BooleanConfig("TPS", "Displays server ticks per second", true);
    Config<Boolean> fpsConfig = new BooleanConfig("FPS", "Displays game FPS", true);
    Config<Boolean> arraylistConfig = new BooleanConfig("Arraylist", "Displays a list of all active modules", true);
    Config<Ordering> orderingConfig = new EnumConfig<>("Ordering", "The ordering of the arraylist", Ordering.LENGTH, Ordering.values(), () -> arraylistConfig.getValue());
    Config<Rendering> renderingConfig = new EnumConfig<>("Rendering", "The rendering mode of the HUD", Rendering.UP, Rendering.values());
    // Rainbow settings
    Config<RainbowMode> rainbowModeConfig = new EnumConfig<>("Rainbow", "The rendering mode for rainbow", RainbowMode.OFF, RainbowMode.values());
    Config<Float> rainbowSpeedConfig = new NumberConfig<>("Rainbow-Speed", "The speed for the rainbow color cycling", 0.1f, 50.0f, 100.0f);
    Config<Integer> rainbowSaturationConfig = new NumberConfig<>("Rainbow-Saturation", "The saturation of rainbow colors", 0, 35, 100);
    Config<Integer> rainbowBrightnessConfig = new NumberConfig<>("Rainbow-Brightness", "The brightness of rainbow colors", 0, 100, 100);
    Config<Float> rainbowDifferenceConfig = new NumberConfig<>("Rainbow-Difference", "The difference offset for rainbow colors", 0.1f, 40.0f, 100.0f);
    //
    private final DecimalFormat decimal = new DecimalFormat("0.0");
    public HUDModule() {
        super("HUD", "Displays the HUD (heads up display) screen.",
                ModuleCategory.CLIENT);
    }

    @EventListener
    public void onRenderOverlayPost(RenderOverlayEvent.Post event) {
        if (mc.player != null && mc.world != null) {
            Window res = mc.getWindow();
            //
            int rainbowOffset = 0;
            // Render offsets for each corner of the screen.
            float topLeft = 2.0f;
            float topRight = topLeft;
            float bottomLeft = res.getScaledHeight() - 11.0f;
            float bottomRight = bottomLeft;
            // center = res.getScaledHeight() - 11 / 2.0f
            boolean renderingUp = renderingConfig.getValue() == Rendering.UP;
            if (mc.currentScreen instanceof ChatScreen) {
                bottomLeft -= 14.0f;
                bottomRight -= 14.0f;
            }
            if (potionHudConfig.getValue() == VanillaHud.MOVE
                    && !mc.player.getStatusEffects().isEmpty()) {
                topRight += 27.0f;
            }
            if (watermarkConfig.getValue()) {
                RenderManager.renderText(event.getContext(), String.format("%s §f%s-%s",
                                ShorelineMod.MOD_NAME, ShorelineMod.MOD_VER,
                                ShorelineMod.MOD_BUILD_NUMBER), 2.0f,
                        topLeft, getHudColor(rainbowOffset));
                // topLeft += 9.0f;
            }
            if (arraylistConfig.getValue()) {
                List<Module> modules = Managers.MODULE.getModules();
                modules = switch (orderingConfig.getValue()) {
                    case ALPHABETICAL -> modules.stream()
                            .sorted(Comparator.comparing(m -> m.getName()))
                            .toList();
                    case LENGTH -> modules.stream()
                            .sorted(Comparator.comparing(m -> -RenderManager.textWidth(getFormattedModule(m))))
                            .toList();
                };
                for (Module m : modules) {
                    if (m instanceof ToggleModule t) {
                        final Animation anim = t.getAnimation();
                        float factor = anim.getScaledTime();
                        if (factor <= 0.01f || t.isHidden()) {
                            continue;
                        }
                        String text = getFormattedModule(m);
                        int width = RenderManager.textWidth(text);
                        RenderManager.renderText(event.getContext(), text,
                                res.getScaledWidth() - width * factor - 1.0f,
                                renderingUp ? topRight : bottomRight, getHudColor(rainbowOffset));
                        if (renderingUp) {
                            topRight += 9.0f;
                        } else {
                            bottomRight -= 9.0f;
                        }
                        rainbowOffset++;
                    }
                }
            }
            if (potionEffectsConfig.getValue()) {
                for (StatusEffectInstance e : mc.player.getStatusEffects()) {
                    final StatusEffect effect = e.getEffectType();
                    if (effect == StatusEffects.NIGHT_VISION) {
                        continue;
                    }
                    boolean amplifier = e.getAmplifier() > 1 && !e.isInfinite();
                    Text duration = StatusEffectUtil.getDurationText(e, 1.0f, mc.world.getTickManager().getTickRate());
                    String text = String.format("%s %s§f%s",
                            effect.getName().getString(),
                            amplifier ? e.getAmplifier() + " " : "",
                            e.isInfinite() ? "" : duration.getString());
                    int width = RenderManager.textWidth(text);
                    RenderManager.renderText(event.getContext(), text,
                            res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                            potionColorsConfig.getValue() ? effect.getColor() : getHudColor(rainbowOffset));
                    if (renderingUp) {
                        bottomRight -= 9.0f;
                    } else {
                        topRight += 9.0f;
                    }
                    rainbowOffset++;
                }
            }
            if (serverBrandConfig.getValue() && mc.getServer() != null) {
                String brand = mc.getServer().getVersion();
                int width = RenderManager.textWidth(brand);
                RenderManager.renderText(event.getContext(), brand,
                        res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                if (renderingUp) {
                    bottomRight -= 9.0f;
                } else {
                    topRight += 9.0f;
                }
                rainbowOffset++;
            }
            if (speedConfig.getValue()) {
                double x = mc.player.getX() - mc.player.prevX;
                // double y = mc.player.getY() - mc.player.prevY;
                double z = mc.player.getZ() - mc.player.prevZ;
                double dist = Math.sqrt(x * x + z * z) / 1000.0;
                double div = 0.05 / 3600.0;
                float timer = Modules.TIMER.isEnabled() ? Modules.TIMER.getTimer() : 1.0f;
                final double speed = dist / div * timer;
                String text = String.format("Speed §f%skm/h",
                        decimal.format(speed));
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getContext(), text,
                        res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                if (renderingUp) {
                    bottomRight -= 9.0f;
                } else {
                    topRight += 9.0f;
                }
                rainbowOffset++;
            }
            if (durabilityConfig.getValue() && mc.player.getMainHandStack().isDamageable()) {
                int n = mc.player.getMainHandStack().getMaxDamage();
                int n2 = mc.player.getMainHandStack().getDamage();
                String text1 = "Durability ";
                String text2 = String.valueOf(n - n2);
                int width = RenderManager.textWidth(text1);
                int width2 = RenderManager.textWidth(text2);
                Color color = ColorUtil.hslToColor((float) (n - n2) / (float) n * 120.0f, 100.0f, 50.0f, 1.0f);
                RenderManager.renderText(event.getContext(), text1,
                        res.getScaledWidth() - width - width2 - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                RenderManager.renderText(event.getContext(), text2,
                        res.getScaledWidth() - width2 - 1.0f, renderingUp ? bottomRight : topRight,
                        color.getRGB());
                if (renderingUp) {
                    bottomRight -= 9.0f;
                } else {
                    topRight += 9.0f;
                }
                rainbowOffset++;
            }
            if (pingConfig.getValue() && !mc.isInSingleplayer()) {
                int latency = Managers.NETWORK.getClientLatency();
                String text = String.format("Ping §f%dms", latency);
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getContext(), text,
                        res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                if (renderingUp) {
                    bottomRight -= 9.0f;
                } else {
                    topRight += 9.0f;
                }
                rainbowOffset++;
            }
            if (tpsConfig.getValue()) {
                float curr = Managers.TICK.getTpsCurrent();
                float avg = Managers.TICK.getTpsAverage();
                String text = String.format("TPS §f%s §7[§f%s§7]",
                        decimal.format(avg),
                        decimal.format(curr));
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getContext(), text,
                        res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                if (renderingUp) {
                    bottomRight -= 9.0f;
                } else {
                    topRight += 9.0f;
                }
                rainbowOffset++;
            }
            if (fpsConfig.getValue()) {
                String text = String.format("FPS §f%d", mc.getCurrentFps());
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getContext(), text,
                        res.getScaledWidth() - width - 1.0f, renderingUp ? bottomRight : topRight,
                        getHudColor(rainbowOffset));
                // bottomRight -= 9.0f;
                rainbowOffset++;
            }
            if (coordsConfig.getValue()) {
                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();
                boolean nether = mc.world.getRegistryKey() == World.NETHER;
                double nx = nether ? mc.player.getX() * 8 :
                        mc.player.getX() / 8;
                double nz = nether ? mc.player.getZ() * 8 :
                        mc.player.getZ() / 8;
                RenderManager.renderText(event.getContext(), String.format(
                                "XYZ §f%s, %s, %s " + (netherCoordsConfig.getValue() ?
                                        "§7[§f%s, %s§7]" : ""),
                                nether ? decimal.format(nx) : decimal.format(x),
                                decimal.format(y),
                                nether ? decimal.format(nz) : decimal.format(z),
                                nether ? decimal.format(x) : decimal.format(nx),
                                nether ? decimal.format(z) : decimal.format(nz)),
                        2, bottomLeft, getHudColor(rainbowOffset));
                bottomLeft -= 9.0f;
                rainbowOffset++;
            }
            if (directionConfig.getValue()) {
                final Direction direction = mc.player.getHorizontalFacing();
                String dir = EnumFormatter.formatDirection(direction);
                String axis = EnumFormatter.formatAxis(direction.getAxis());
                boolean pos = direction.getDirection() == Direction.AxisDirection.POSITIVE;
                RenderManager.renderText(event.getContext(),
                        String.format("%s §7[§f%s%s§7]", dir, axis,
                                pos ? "+" : "-"), 2, bottomLeft,
                        getHudColor(rainbowOffset));
                // bottomLeft -= 9.0f;
                rainbowOffset++;
            }
            if (armorConfig.getValue()) {
                final Entity riding = mc.player.getVehicle();
                //
                int x = res.getScaledWidth() / 2 + 15;
                int y = res.getScaledHeight();
                if (mc.player.isTouchingWater() && mc.player.getAir() > 0) {
                    y -= 65;
                } else if (riding instanceof LivingEntity entity) {
                    y -= 45 + (int) Math.ceil((entity.getMaxHealth() - 1.0f) / 20.0f) * 10;
                } else if (riding != null) {
                    y -= 45;
                } else {
                    y -= mc.player.isCreative() ?
                            (mc.player.isRiding() ? 45 : 38) : 55;
                }
                for (int i = 3; i >= 0; --i) {
                    ItemStack armor = mc.player.getInventory().armor.get(i);
                    event.getContext().drawItem(armor, x, y);
                    event.getContext().drawItemInSlot(mc.textRenderer, armor, x, y);
                    x += 18;
                }
            }
        }
    }

    @EventListener
    public void onRenderOverlayStatusEffect(RenderOverlayEvent.StatusEffect event) {
        if (potionHudConfig.getValue() == VanillaHud.HIDE) {
            event.cancel();
        }
    }

    @EventListener
    public void onRenderOverlayItemName(RenderOverlayEvent.ItemName event) {
        if (itemNameConfig.getValue() != VanillaHud.KEEP) {
            event.cancel();
        }
        if (itemNameConfig.getValue() == VanillaHud.MOVE) {
            final Window window = mc.getWindow();
            int x = window.getScaledWidth() / 2 - 90;
            int y = window.getScaledHeight() - 49;
            boolean armor = !mc.player.getInventory().armor.isEmpty();
            if (mc.player.getAbsorptionAmount() > 0.0f) {
                y -= 9;
            }
            if (armor) {
                y -= 9;
            }
            event.setX(x);
            event.setY(y);
        }
    }

    private int getHudColor(int rainbowOffset) {
        return switch (rainbowModeConfig.getValue()) {
            case OFF -> Modules.COLORS.getRGB();
            case STATIC -> rainbow(1L);
            case GRADIENT -> rainbow(rainbowOffset);
        };
    }

    private String getFormattedModule(final Module module) {
        final String metadata = module.getModuleData();
        if (!metadata.equals("ARRAYLIST_INFO")) {
            return String.format("%s §7[§f%s§7]", module.getName(),
                    module.getModuleData());
        }
        return module.getName();
    }

    private int rainbow(long offset) {
        float hue = (float) (((double) System.currentTimeMillis() * (rainbowSpeedConfig.getValue() / 10)
                + (double) (offset * 500L)) % (30000 / (rainbowDifferenceConfig.getValue() / 100))
                / (30000 / (rainbowDifferenceConfig.getValue() / 20.0f)));
        return Color.HSBtoRGB(hue, rainbowSaturationConfig.getValue() / 100.0f,
                rainbowBrightnessConfig.getValue() / 100.0f);
    }

    public enum VanillaHud {
        MOVE,
        HIDE,
        KEEP
    }

    public enum Ordering {
        LENGTH,
        ALPHABETICAL
    }

    public enum Rendering {
        UP,
        DOWN
    }

    public enum RainbowMode {
        OFF,
        GRADIENT,
        STATIC
    }
}
