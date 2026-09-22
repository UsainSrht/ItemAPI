package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class Legacy1214Handlers {

    private static final MethodHandle UNBREAKABLE_FACTORY;

    static {
        MethodHandle handle = null;
        try {
            Class<?> clazz = Class.forName("io.papermc.paper.datacomponent.item.Unbreakable");
            Method method = clazz.getMethod("unbreakable", boolean.class);
            handle = MethodHandles.lookup().unreflect(method);
        } catch (Throwable ignored) {
        }
        UNBREAKABLE_FACTORY = handle;
    }

    private Legacy1214Handlers() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void applyUnbreakable(ItemStack stack, DataComponentType type, boolean enabled, boolean showInTooltip) {
        if (stack == null || type == null) {
            return;
        }
        if (!enabled) {
            stack.unsetData(type);
            return;
        }
        if (type instanceof DataComponentType.Valued valued && UNBREAKABLE_FACTORY != null) {
            try {
                Object unbreakable = UNBREAKABLE_FACTORY.invoke(showInTooltip);
                stack.setData(valued, unbreakable);
                return;
            } catch (Throwable ignored) {
            }
        }
        if (type instanceof DataComponentType.NonValued nonValued) {
            stack.setData(nonValued);
        } else {
            stack.unsetData(type);
        }
    }
}
