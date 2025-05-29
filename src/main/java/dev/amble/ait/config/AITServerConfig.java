package dev.amble.ait.config;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

import java.util.ArrayList;
import java.util.List;

public class AITServerConfig {

    public static final String CATEGORY = "server";

    public static final ConfigClassHandler<AITServerConfig> INSTANCE = ConfigClassHandler.createBuilder(AITServerConfig.class)
            .id(YACLPlatform.rl(AITMod.MOD_ID, "server"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("ait-server.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = CATEGORY)
    @dev.isxander.yacl3.config.v2.api.autogen.Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    public boolean minifyJson = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    public boolean ghostMonument = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    public boolean lockDimensions = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    public boolean rwfEnabled = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    public boolean tntCanTeleportThroughDoors = true;

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    public List<String> worldBlacklist = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    public List<String> travelBlacklist = new ArrayList<>(List.of(
            AITDimensions.TIME_VORTEX_WORLD.getValue().toString()));

    public int travelPerTick = 2;

    public boolean sendBulk = true;
    public int maxTardises = -1;

    public static class StringListFactory implements ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {

        // used by the reflections
        public StringListFactory() { }

        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }

        @Override
        public String provideNewValue() {
            return "";
        }
    }
}
