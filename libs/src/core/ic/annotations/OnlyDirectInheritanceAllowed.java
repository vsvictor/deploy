package ic.annotations;

import java.lang.annotation.*;


@Documented

@Target({

	ElementType.TYPE

})

public @interface OnlyDirectInheritanceAllowed {

}
