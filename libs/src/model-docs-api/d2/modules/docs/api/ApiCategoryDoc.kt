package d2.modules.docs.api


import ic.struct.list.List


abstract class ApiCategoryDoc {

	abstract val name : String

	abstract val requests : List<ApiRequestDoc>

}