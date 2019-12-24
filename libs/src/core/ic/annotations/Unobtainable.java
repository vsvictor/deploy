package ic.annotations;

import java.lang.annotation.*;


@Documented

@Target({

	ElementType.TYPE,
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.PARAMETER,
	ElementType.LOCAL_VARIABLE

})

public @interface Unobtainable {

}
