package ic.annotations

import java.lang.annotation.Documented


@MustBeDocumented
@Target(
	AnnotationTarget.FIELD,
	AnnotationTarget.FUNCTION,
	AnnotationTarget.PROPERTY_GETTER,
	AnnotationTarget.PROPERTY_SETTER,
	AnnotationTarget.VALUE_PARAMETER,
	AnnotationTarget.LOCAL_VARIABLE,
	AnnotationTarget.PROPERTY
)
annotation class Between(vararg val value: Int)
