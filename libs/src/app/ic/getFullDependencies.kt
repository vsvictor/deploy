package ic


import ic.struct.list.EditableList
import ic.struct.list.List
import ic.throwables.NotExists


fun getFullDependencies(distribution: Distribution, packageName: String) : List<String> {

	val fullDependencies = EditableList.Default<String>()

	val addToFullDependenciesAction : (String) -> Unit = { dependency ->
		try {
			fullDependencies.findOrThrow { alreadyAddedPackageName -> alreadyAddedPackageName == dependency }
		} catch (notFound: NotExists) {
			fullDependencies.add(dependency)
		}
	}

	Package.load(distribution, packageName).dependencies.forEach { dependency ->
		getFullDependencies(distribution, dependency).forEach(addToFullDependenciesAction)
		addToFullDependenciesAction.invoke(dependency)
	}

	return fullDependencies

}
