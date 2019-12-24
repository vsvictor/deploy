package d2.polpharma.services.prm.model


import ic.struct.collection.Collection


class UserArrangementState private constructor (
	val name : String
) {
	companion object {
		val OPEN 		= UserArrangementState("OPEN")
		val CONFIRMED 	= UserArrangementState("CONFIRMED")
		val CANCELED 	= UserArrangementState("CANCELED")
		val states = Collection.Default<UserArrangementState>(
			OPEN,
			CONFIRMED,
			CANCELED
		)
		fun byName (name: String) = states.findOrThrow { it.name == name }
	}
}