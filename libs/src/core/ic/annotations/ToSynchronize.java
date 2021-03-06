package ic.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Documented

@Target({

	ElementType.METHOD,
	ElementType.CONSTRUCTOR

})

public @interface ToSynchronize {

	String value() default "";

}
