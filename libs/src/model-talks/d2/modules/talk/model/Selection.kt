package d2.modules.talk.model


import ic.annotations.Degenerate
import ic.annotations.Hide
import ic.storage.CacheStorage
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.collection.FilterCollection
import ic.struct.set.CountableSet
import ic.struct.set.FilterCountableSet
import ic.struct.set.UnionCountableSet
import ic.throwables.OutOfLimit

import d2.modules.talk.model.Talk.getTalksMapper
import ic.date.DayIndex.nullableDateToDayIndex
import java.util.Objects.equals


@Degenerate
class Selection @Hide private constructor() {

	companion object {


		private fun getSelectedTalksByUserAndTalkEventStorage(storage: CacheStorage): CacheStorage {
			return storage.createFolderIfNotExists("selected-talks-by-user-and-talk-event")
		}


		@Synchronized
		fun getSelectedPlacesCountByTalk(storage: CacheStorage, talkEventId: String, talkId: String): Int {
			val selectedTalksByUserAndTalkEventStorage = getSelectedTalksByUserAndTalkEventStorage(storage)
			return FilterCollection<String>(selectedTalksByUserAndTalkEventStorage.keys) { userId ->
				val selectedTalksByTalkEventStorage = selectedTalksByUserAndTalkEventStorage.createFolderIfNotExists(userId)
				selectedTalksByTalkEventStorage.get(talkEventId) { CountableSet.Default<String>() }.contains(talkId)
			}.count
		}


		fun isTalkAvailableToSelect(storage: CacheStorage, talkEventId: String, talkId: String): Boolean {
			val placesCount = getTalksMapper(storage)[talkId].placesCount ?: return true
			return getSelectedPlacesCountByTalk(storage, talkEventId, talkId) <= placesCount
		}


		@JvmStatic fun getSelectedTalkIds(storage: CacheStorage, userId: String, talkEventId: String): CountableSet<String> {
			return getSelectedTalksByUserAndTalkEventStorage(storage).createFolderIfNotExists(userId).get(talkEventId) { CountableSet.Default() }
		}

		@JvmStatic fun getSelectedTalks(storage: CacheStorage, userId: String, talkEventId: String): Collection<Talk> {
			val talksMapper = getTalksMapper(storage)
			return ConvertCollection(
				getSelectedTalkIds(storage, userId, talkEventId)
			) { talksMapper[it] }
		}


		@Synchronized
		@Throws(OutOfLimit::class)
		@JvmStatic fun setSelectedTalks(storage: CacheStorage, userId: String, talkEventId: String, dayIndex: Int?, talkIds: CountableSet<String>) {

			val selectedTalksByUserAndTalkEventStorage = getSelectedTalksByUserAndTalkEventStorage(storage)

			val selectedTalksByTalkEventStorage = selectedTalksByUserAndTalkEventStorage.createFolderIfNotExists(userId)

			val selectedTalkIds = getSelectedTalkIds(storage, userId, talkEventId)

			val talksMapper = getTalksMapper(storage)

			talkIds.forEach { talkId ->
				if (selectedTalkIds.contains(talkId)) return@forEach
				if (!isTalkAvailableToSelect(storage, talkEventId, talkId)) throw OutOfLimit()
			}

			val newSelectedTalkIds = UnionCountableSet(
				object : FilterCountableSet<String>(
					selectedTalkIds
				) {
					override fun filter(talkId: String): Boolean {
						if (dayIndex == null) {
							return false
						} else {
							val talk = talksMapper[talkId]
							return !equals(nullableDateToDayIndex(talk.startDate), dayIndex)
						}
					}
				},
				talkIds
			)

			selectedTalksByTalkEventStorage.set(talkEventId, newSelectedTalkIds)

		}
	}


}
