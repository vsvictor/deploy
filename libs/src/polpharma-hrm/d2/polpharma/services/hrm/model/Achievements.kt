package d2.polpharma.services.hrm.model


import d2.polpharma.services.hrm.polpharmaHrmAchievementsStorage
import ic.struct.map.CountableMap


const val ACHIEVEMENT_SCENARIOS_PASSED_1 = "SCENARIOS_PASSED_1"
const val ACHIEVEMENT_SCENARIO_PASSED_100 = "SCENARIO_PASSED_100"
const val ACHIEVEMENT_SUBJECTS_PASSED_1 = "SUBJECTS_PASSED_1"
const val ACHIEVEMENT_SUBJECTS_PASSED_2 = "SUBJECTS_PASSED_2"
const val ACHIEVEMENT_SUBJECTS_PASSED_3 = "SUBJECTS_PASSED_3"
const val ACHIEVEMENT_SUBJECTS_PASSED_4 = "SUBJECTS_PASSED_4"
const val ACHIEVEMENT_SUBJECTS_PASSED_5 = "SUBJECTS_PASSED_5"
const val ACHIEVEMENT_SUBJECTS_PASSED_6 = "SUBJECTS_PASSED_6"
const val ACHIEVEMENT_SUBJECTS_PASSED_7 = "SUBJECTS_PASSED_7"
const val ACHIEVEMENT_ACHIEVEMENTS_7 = "ACHIEVEMENTS_7"
const val ACHIEVEMENT_WIKI_1 = "WIKI_1"
const val ACHIEVEMENT_WIKI_30 = "WIKI_30"
const val ACHIEVEMENT_WIKI_100 = "WIKI_100"


fun getAchievements(userId: String): CountableMap<Int, String> = d2.modules.achievements.model.getAchievements(polpharmaHrmAchievementsStorage, userId)


fun pushAchievement (
	userId 					: String,
	achievement 			: String,
	stack 					: Boolean,
	notificationCategory 	: String?
) {
	d2.modules.achievements.model.pushAchievement(
		polpharmaHrmAchievementsStorage,
		userId,
		achievement,
		stack
	) { newCount ->
		if (notificationCategory != null) {
			pushNotification(
				userId,
				Notification(
					Notification.TYPE_ACHIEVEMENT,
					notificationCategory,
					false,
					achievement
				)
			)
		}
		if (newCount >= 7) {
			pushAchievement(userId, ACHIEVEMENT_ACHIEVEMENTS_7, false, Notification.CATEGORY_COMMON)
		}
	}
}