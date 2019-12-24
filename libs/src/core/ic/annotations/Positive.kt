package ic.annotations


@Target(
	AnnotationTarget.FIELD,
	AnnotationTarget.FUNCTION,
	AnnotationTarget.PROPERTY_GETTER,
	AnnotationTarget.PROPERTY_SETTER,
	AnnotationTarget.VALUE_PARAMETER,
	AnnotationTarget.LOCAL_VARIABLE,
	AnnotationTarget.PROPERTY
)
annotation class Positive
