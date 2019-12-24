package d2.polpharma.services.prm.model


import d2.modules.events.model.Event
import org.json.JSONObject


class FishkiEvent : Event {

	val pointsAmount 	: Int
	val fishkiAmount 	: Int
	val pointsRemaining : Int

	override fun toJson (eventJson: JSONObject) {
		eventJson.put("pointsAmount", 		pointsAmount)
		eventJson.put("fishkiAmount", 		fishkiAmount)
		eventJson.put("pointsRemaining", 	pointsRemaining)
	}

	override val classToDeclare = FishkiEvent::class.java
	override fun serialize (json: JSONObject) {
		super.serialize(json)
		json.put("pointsAmount", 	pointsAmount)
		json.put("fishkiAmount", 	fishkiAmount)
		json.put("pointsRemaining", pointsRemaining)
	}
	constructor (json: JSONObject) : super (json) {
		pointsAmount 	= json.getInt("pointsAmount")
		fishkiAmount 	= json.getInt("fishkiAmount")
		pointsRemaining = json.getInt("pointsRemaining")
	}

	constructor (pointsAmount : Int, fishkiAmount : Int, pointsRemaining : Int) {
		this.pointsAmount = pointsAmount
		this.fishkiAmount = fishkiAmount
		this.pointsRemaining = pointsRemaining
	}

}