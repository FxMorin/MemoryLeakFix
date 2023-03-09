package ca.fxco.memoryleakfix.config;

public @interface MinecraftRequirement {

    String minVersion() default "";

    String maxVersion() default "";
}
