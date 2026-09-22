package me.usainsrht.itemapi.yamlitem;

import me.usainsrht.itemapi.yamlitem.handler.Legacy1214Handlers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class YamlItemMultiVersionTest {

    @Test
    void testLegacy1214HandlersClassLoading() {
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("me.usainsrht.itemapi.yamlitem.handler.Legacy1214Handlers");
            assertNotNull(clazz);
        });
    }

    @Test
    void testLegacy1214HandlersInvocationSafety() {
        assertDoesNotThrow(() -> {
            Legacy1214Handlers.applyUnbreakable(null, null, false, true);
        });
    }

    @Test
    void testModern26HandlersClassLoadingAndInvocationSafety() {
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("me.usainsrht.itemapi.yamlitem.handler.Modern26Handlers");
            assertNotNull(clazz);
            me.usainsrht.itemapi.yamlitem.handler.Modern26Handlers.applyUnbreakable(null, false, true);
            me.usainsrht.itemapi.yamlitem.handler.Modern26Handlers.applyHideTooltip(null, true);
        });
    }
}
