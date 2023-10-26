package ltd.qisi.test.annotaitons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于参数
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MockField {
    /**
     * 参数名称
     */
    String name();

    /**
     * 备注
     */
    String remarks() default "";

}
