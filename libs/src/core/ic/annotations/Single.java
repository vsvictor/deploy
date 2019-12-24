package ic.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Documented

@Target({

	ElementType.TYPE,
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.PARAMETER,
	ElementType.LOCAL_VARIABLE

})

public @interface Single {

}
