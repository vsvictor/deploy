package ic.util.reflect


fun getFieldType(c : Class<Any>, field : String) : Class<out Any> {

	return c.getField(field).type

}