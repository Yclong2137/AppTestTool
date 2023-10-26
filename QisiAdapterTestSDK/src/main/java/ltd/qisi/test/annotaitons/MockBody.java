package ltd.qisi.test.annotaitons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于结构体参数
 *
 * @author Yclong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MockBody {
    /**
     * 原始类型
     */
    Class<?> rawType() default Void.class;

    /**
     * 参数类型
     */
    Class<?>[] type() default {};

    /**
     * 模版（json格式）
     */
    String template() default "";

}
