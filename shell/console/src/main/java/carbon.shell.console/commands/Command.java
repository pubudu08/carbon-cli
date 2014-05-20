package carbon.shell.console.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pubudu on 5/20/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Command {
    /**
     * Returns the scope or sub shell of the command
     */
    String scope();

    /**
     * REturns the name of the command if used inside a shell
     */
    String name();

    /**
     * Returns the description of the command which is used to generate command line help
     */
    String description() default "";

    /**
     * Returns a detailed description of the command
     */
    String detailedDescription() default "";
}
