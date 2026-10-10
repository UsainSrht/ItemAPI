package me.usainsrht.itemapi.yamlitem;

import me.usainsrht.itemapi.yamlitem.handler.ComponentHandlerRegistry;
import me.usainsrht.itemapi.yamlitem.handler.Legacy1214Handlers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testComponentHandlerRegistryInitialization() {
        assertDoesNotThrow(() -> {
            ComponentHandlerRegistry registry = new ComponentHandlerRegistry();
            assertNotNull(registry);
            assertFalse(registry.registerSafely("non_existent_component_test", (s, t, v, p, par) -> {}));
        });
    }

    @Test
    void testYamlItemParserInitializationSafety() {
        assertDoesNotThrow(() -> {
            YamlItemParser parser = new YamlItemParser();
            assertNotNull(parser);
            assertNotNull(parser.handlers());
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

    @Test
    void testUnknownComponentResolutionReturnsNull() {
        ComponentHandlerRegistry registry = new ComponentHandlerRegistry();
        assertNull(registry.resolveType("completely_unknown_component_xyz"));
        assertFalse(registry.hasType("completely_unknown_component_xyz"));
        assertNull(registry.resolveType("map_color_non_existent"));
    }
}
