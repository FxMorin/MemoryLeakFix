package ca.fxco.memoryleakfix.config;

public @interface VersionRange {

    String minVersion() default "";

    String maxVersion() default "";
}
