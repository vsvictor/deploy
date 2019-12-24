package ic.annotations;

import java.lang.annotation.*;


@Documented

@Target({

	ElementType.METHOD,
	ElementType.CONSTRUCTOR

})

public @interface AlwaysThrows {

	Class<? extends Throwable> value() default RuntimeException.class;

}
