package ic.util.reflect


import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.set.CountableSet
import ic.throwables.Skip.SKIP
import java.lang.reflect.Field


fun getFieldNames(c : Class<*>) : CountableSet<String> {

	return CountableSet.Default(
		ConvertCollection<String, Field>(
			Collection.Default<Field>(*c.declaredFields)
		) { field ->
			if (java.lang.reflect.Modifier.isStatic(field.modifiers)) throw SKIP;
			field.name
		}
	)

}


fun getStaticFieldNames(c : Class<*>) : CountableSet<String> {

	return CountableSet.Default(
		ConvertCollection<String, Field>(
			Collection.Default<Field>(*c.declaredFields)
		) { field ->
			if (!java.lang.reflect.Modifier.isStatic(field.modifiers)) throw SKIP;
			field.name
		}
	)

}