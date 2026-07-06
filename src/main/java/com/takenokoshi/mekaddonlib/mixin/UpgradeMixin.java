package com.takenokoshi.mekaddonlib.mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradePlugin;
import com.takenokoshi.mekaddonlib.upgrade.IAdditiionalUpgradePlugin;
import com.takenokoshi.mekaddonlib.upgrade.IAdditionalUpgrade;

import io.netty.buffer.ByteBuf;
import mekanism.api.Upgrade;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;

@Mixin(value = { Upgrade.class }, remap = false)
public class UpgradeMixin {

    @Shadow
    @Final
    @Mutable
    @SuppressWarnings("target")
    static Upgrade[] $VALUES;

    @Shadow
    @Final
    @Mutable
    static Codec<Upgrade> CODEC;

    @Shadow
    @Final
    @Mutable
    static IntFunction<Upgrade> BY_ID;

    @Shadow
    @Final
    @Mutable
    static StreamCodec<ByteBuf, Upgrade> STREAM_CODEC;

    @Unique
    private static final Map<Upgrade, IAdditionalUpgrade> mek_addon_lib$TO_ADDITIONAL_CACHE = new HashMap<>();
    @Unique
    private static final Map<String, Upgrade> mek_addon_lib$FROM_ADDITINAL_CACHE = new HashMap<>();
    @Unique
    private static final Map<String, IAdditionalUpgrade> mek_addon_lib$NAME_CACHE = new HashMap<>();
    @Unique
    private static final Map<IAdditionalUpgrade, ItemLike> mek_addon_lib$ITEM_CACHE = new HashMap<>();

    @Invoker("<init>")
    private static Upgrade mek_addon_lib$invokeInit(String string, int i, String name, ILangEntry langKey,
            ILangEntry descLangKey,
            int maxStack, EnumColor color) {
        return null;
    }

    @Unique
    private static Upgrade mek_addon_lib$createNewUpgrade(String name, ILangEntry langKey, ILangEntry descLangKey,
            int maxStack, EnumColor color) {
        int index = $VALUES.length;
        Upgrade value = mek_addon_lib$invokeInit(name.toUpperCase(), index, name, langKey, descLangKey, maxStack,
                color);
        Upgrade[] newVALUES = Arrays.copyOf($VALUES, index + 1);
        newVALUES[index] = value;
        $VALUES = newVALUES;
        return value;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    static void mek_addon_lib$addUpgrades(CallbackInfo ci) {
        ModList.get().getAllScanData().forEach(scandata -> {
            for (AnnotationData annotationData : scandata.getAnnotations()) {
                if (annotationData.annotationType().getClassName()
                        .equals(AdditionalUpgradePlugin.class.getName())) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(annotationData.clazz().getClassName());
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                    if (!IAdditiionalUpgradePlugin.class.isAssignableFrom(clazz)) {
                        Mekanism.logger.error("[Error form Mekanism Addon Library] "
                                + clazz
                                + " has @AdditionalUpgradePlugin but doesn't implement IAdditiionalUpgradePlugin");
                        continue;
                    }
                    IAdditiionalUpgradePlugin plugin;
                    try {
                        plugin = (IAdditiionalUpgradePlugin) clazz.getConstructor().newInstance();
                    } catch (InstantiationException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] Failed to instantiate additional upgrade plugin '{}'. The class may be abstract or an interface.",
                                clazz.getName());
                        continue;
                    } catch (IllegalAccessException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] Failed to instantiate additional upgrade plugin '{}'. The constructor is not accessible (it should be public).",
                                clazz.getName());
                        continue;
                    } catch (IllegalArgumentException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] Failed to instantiate additional upgrade plugin '{}'. Invalid constructor arguments were supplied.",
                                clazz.getName());
                        continue;
                    } catch (InvocationTargetException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] The constructor of additional upgrade plugin '{}' threw an exception.",
                                clazz.getName());
                        continue;
                    } catch (NoSuchMethodException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] Failed to instantiate additional upgrade plugin '{}'. A public no-argument constructor is required.",
                                clazz.getName());
                        continue;
                    } catch (SecurityException e) {
                        Mekanism.logger.error(
                                "[Error form Mekanism Addon Library] \"Failed to instantiate additional upgrade plugin '{}'. Access to the constructor was denied by the security manager.",
                                clazz.getName());
                        continue;
                    }
                    plugin.getAdditionalUpgrades().forEach(additionalUpgrade -> {
                        String name = additionalUpgrade.actualName();
                        if (mek_addon_lib$FROM_ADDITINAL_CACHE.containsKey(name)) {
                            Mekanism.logger
                                    .error("[Error form Mekanism Addon Library] Duplicate additional upgrade: " + name);
                            return;
                        }
                        Upgrade value = mek_addon_lib$createNewUpgrade(
                                name,
                                additionalUpgrade.langKey(),
                                additionalUpgrade.descLangKey(),
                                additionalUpgrade.maxStack(),
                                additionalUpgrade.color());
                        mek_addon_lib$FROM_ADDITINAL_CACHE.put(name, value);
                        mek_addon_lib$TO_ADDITIONAL_CACHE.put(value, additionalUpgrade);
                        mek_addon_lib$NAME_CACHE.put(name, additionalUpgrade);
                    });
                }
            }
        });

        // regenerate fields because return values of Upgrade.values() was changed.
        Upgrade[] values = Upgrade.values();
        Function<String, Upgrade> nameLookup = StringRepresentable.createNameLookup(values, Function.identity());
        Function<String, Upgrade> remapper = (it) -> "gas".equals(it) ? Upgrade.CHEMICAL
                : (Upgrade) nameLookup.apply(it);
        CODEC = new StringRepresentable.EnumCodec<>(values, remapper);
        BY_ID = ByIdMap.continuous(Enum::ordinal, Upgrade.values(), OutOfBoundsStrategy.WRAP);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    }

    @ModifyVariable(method = "buildMap", at = @At(value = "STORE", ordinal = 0), name = "upgrades")
    private static Map<Upgrade, Integer> mek_addon_lib$buildAdditionalUpgrades(
            @Nullable Map<Upgrade, Integer> upgrades,
            @Nullable CompoundTag nbtTags) {
        if (upgrades == null) {
            upgrades = new HashMap<>();
        }
        if (nbtTags != null && nbtTags.contains("mek_addon_lib_additional_upgrades")) {
            ListTag list = nbtTags.getList("mek_addon_lib_additional_upgrades", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                upgrades.put(
                        mek_addon_lib$FROM_ADDITINAL_CACHE.get(tag.getString("type")),
                        tag.getInt("amount"));
            }
        }
        return upgrades;
    }

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = "saveMap", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private static Set<Map.Entry<Upgrade, Integer>> mek_addon_lib$saveAdditionalUpgrades(
            Set<Map.Entry<Upgrade, Integer>> upgrades,
            @Local(argsOnly = true) CompoundTag nbtTags) {
        ListTag list = new ListTag();
        for (Map.Entry<Upgrade, Integer> entry : upgrades) {
            if (mek_addon_lib$isAdditional(entry.getKey())) {
                list.add(mek_addon_lib$TO_ADDITIONAL_CACHE.get(entry.getKey()).getTag(entry.getValue()));
            }
        }
        nbtTags.put("mek_addon_lib_additional_upgrades", list);
        return Set.of(upgrades.stream()
                .filter(entry -> !mek_addon_lib$isAdditional(entry.getKey()))
                .toArray(Map.Entry[]::new));
    }

    @Unique
    public static Upgrade mek_addon_lib$getUpradeFromAdditional(IAdditionalUpgrade additionalUpgrade) {
        return mek_addon_lib$FROM_ADDITINAL_CACHE.get(additionalUpgrade.actualName());
    }

    @Unique
    public static IAdditionalUpgrade mek_addon_lib$convertToAdditional(Upgrade upgrade) {
        return mek_addon_lib$TO_ADDITIONAL_CACHE.getOrDefault(upgrade, null);
    }

    @Unique
    public static boolean mek_addon_lib$isAdditional(Upgrade upgrade) {
        return mek_addon_lib$TO_ADDITIONAL_CACHE.containsKey(upgrade);
    }

    @Unique
    public static ItemStack mek_addon_lib$getStack(IAdditionalUpgrade additionalUpgrade, int amount) {
        return new ItemStack(mek_addon_lib$ITEM_CACHE.getOrDefault(additionalUpgrade, Items.REDSTONE), amount);
    }

    @Unique
    public static void mek_addon_lib$registerItem(IAdditionalUpgrade additionalUpgrade, ItemLike itemLike) {
        mek_addon_lib$ITEM_CACHE.put(additionalUpgrade, itemLike);
    }

}
