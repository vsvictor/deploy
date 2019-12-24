package ic.annotations;


import java.lang.annotation.*;


@Documented

@Target({

	ElementType.METHOD,
	ElementType.CONSTRUCTOR,
	ElementType.FIELD,
	ElementType.TYPE

})

public @interface Necessary {

}
