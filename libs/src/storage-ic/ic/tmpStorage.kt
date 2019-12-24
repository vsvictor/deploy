package ic


import ic.storage.Directory
import ic.storage.StreamStorage


val tmpStorage : StreamStorage = Directory.createIfNotExists("/tmp/ic").createFolderIfNotExists(app.packageName)