package com.takenokoshi.mekaddonlib.mixin.mekanism;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
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
import com.takenokoshi.mekaddonlib.upgrade.AdditionalUpgradeUtils;
import com.takenokoshi.mekaddonlib.upgrade.IAdditiionalUpgradePlugin;
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
import net.minecraft.util.StringRepresentable;

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
    private static void mek_addon_lib$addUpgrades(CallbackInfo ci) {
        ServiceLoader.load(IAdditiionalUpgradePlugin.class).forEach(plugin -> {

            plugin.getAdditionalUpgrades().forEach(additionalUpgrade -> {
                String name = additionalUpgrade.actualName();
                if (AdditionalUpgradeUtils.isDuplicate(name)) {
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
                AdditionalUpgradeUtils.putCache(additionalUpgrade, value);
            });

            plugin.getEnergyModifiers().forEach(AdditionalUpgradeUtils::registerEnergyModifier);
            plugin.getSpeedModifiers().forEach(AdditionalUpgradeUtils::registerSpeedModifier);
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
                        AdditionalUpgradeUtils
                                .getUpgradeFromAdditional(AdditionalUpgradeUtils.valueOf(tag.getString("type"))),
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
            if (AdditionalUpgradeUtils.isAdditional(entry.getKey())) {
                list.add(AdditionalUpgradeUtils.convertToAdditional(entry.getKey()).getTag(entry.getValue()));
            }
        }
        nbtTags.put("mek_addon_lib_additional_upgrades", list);
        return Set.of(upgrades.stream()
                .filter(entry -> !AdditionalUpgradeUtils.isAdditional(entry.getKey()))
                .toArray(Map.Entry[]::new));
    }

}
