package ca.fxco.memoryleakfix.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Experimental Remapping tool
 * It can switch the mapping used in mixins based on the mapping type using by the launcher/version
 */
// TODO: Run at compile time
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemapTarget {

    Remap value();

    Remap target();
}
