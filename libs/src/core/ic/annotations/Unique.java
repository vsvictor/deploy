package ic.annotations;

import java.lang.annotation.*;

@Documented

@Retention(RetentionPolicy.CLASS)

@Target({

	ElementType.FIELD,
	ElementType.TYPE,
	ElementType.METHOD,
	ElementType.PARAMETER,
	ElementType.LOCAL_VARIABLE

})

public @interface Unique {

}
