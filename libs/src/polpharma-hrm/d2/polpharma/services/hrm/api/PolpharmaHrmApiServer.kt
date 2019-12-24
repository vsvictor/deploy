package d2.polpharma.services.hrm.api


import d2.polpharma.services.hrm.api.achievements.AchievementsApiRoute
import d2.polpharma.services.hrm.api.education.EducationAdminRoute
import d2.polpharma.services.hrm.api.education.EducationApiRoute
import d2.polpharma.services.hrm.api.events.EventsApiRoute
import d2.polpharma.services.hrm.api.questions.QuestionsApiRoute
import d2.polpharma.services.hrm.api.survey.SurveyAdminRoute
import d2.polpharma.services.hrm.api.survey.SurveyApiRoute
import d2.polpharma.services.hrm.api.users.UsersApiRoute
import d2.polpharma.services.polpharmaCertEmail
import ic.Assets
import ic.network.http.*
import ic.struct.list.List
import ic.struct.set.CountableSet
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.throwables.WrongValue
import ic.tier


class PolpharmaHrmApiServer : HttpsServer () {

	override fun getDomainName (request: HttpRequest?) : String {
		if (tier == null)		throw NOT_SUPPORTED
		if (tier == "prod") 	return "hrm.polpharma-core.com"
		if (tier == "stage") 	return "stage.hrm.polpharma-core.com"
		else throw WrongValue()
	}

	override fun getCertEmail() = polpharmaCertEmail
	override fun getDomainNames() = CountableSet.Default<String>(getDomainName(null))

	override fun toAllowCors() = true

	override fun initRoute() = FolderHttpRoute.Final(

		UsersApiRoute(),
		AchievementsApiRoute(),
		EventsApiRoute(),

		EducationApiRoute(),
		SurveyApiRoute(),

		QuestionsApiRoute(),

		GetEventsApiMethod(),
		GetUsersAbsenceDaysApiMethod(),

		FolderHttpRoute.Final(
			"admin",
			List.Default<HttpRoute>(
				EducationAdminRoute(),
				SurveyAdminRoute()
			)
		),

		object : StorageHttpRoute() {
			override fun getStorage(path: String) = Assets.resources
		},

		Migrate()

	)

}