package ltd.qisi.test.annotaitons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于方法
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MockMethod {

    String desc();

    @Deprecated int order() default Integer.MAX_VALUE;
}
