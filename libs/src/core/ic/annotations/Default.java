package ic.annotations;

import java.lang.annotation.*;

@Documented

@Retention(RetentionPolicy.CLASS)

@Target({

	ElementType.METHOD

})

public @interface Default {

}
