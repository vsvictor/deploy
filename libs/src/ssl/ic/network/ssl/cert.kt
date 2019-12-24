package ic.network.ssl


import ic.cmd.SystemConsole
import ic.storage.Directory
import ic.stream.ByteSequence
import ic.struct.collection.ConvertCollection
import ic.struct.set.CountableSet
import ic.text.JoinStrings

import ic.Storages.getCommonDataStorage
import ic.throwables.NotExists
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED


@Throws(NotSupported::class)
fun generateCertJks(

   email: String,
   domainNames: CountableSet<String>,
   keyStorePassword: String,
   keyPassword: String

): ByteSequence {

    val systemConsole = SystemConsole()
    systemConsole.writeLine("apt install -y certbot")
    println(systemConsole.read())
    systemConsole.writeLine(
        "certbot certonly " + (
			"--standalone " +
            "--non-interactive " +
            "--expand " +
            "--agree-tos " +
            "--email " + email + " " +
            JoinStrings(
                ConvertCollection(domainNames) { domainName -> "-d $domainName" },
				' '
			)
		)
    )
    println(systemConsole.read())
    val storage = getCommonDataStorage()
    val letsEncryptLiveDir = try { Directory.getExistingOrThrow("/etc/letsencrypt/live") } catch (notExists: NotExists) { throw NOT_SUPPORTED }
    val letsEncryptKeysDir = letsEncryptLiveDir.getNotNull<Directory>(letsEncryptLiveDir.keys.first)
    storage.removeIfExists("certificate.jks")
    systemConsole.writeLine("cd " + letsEncryptKeysDir.absolutePath)
    systemConsole.writeLine("openssl pkcs12 -export -out keystore.pkcs12 -in fullchain.pem -inkey privkey.pem -password pass:ilovejava")
    println(systemConsole.read())
    systemConsole.write("keytool -importkeystore")
    run {
        systemConsole.write(" -srckeystore keystore.pkcs12")
        systemConsole.write(" -srcstoretype PKCS12")
        systemConsole.write(" -destkeystore " + storage.absolutePath + "/certificate.jks")
        systemConsole.write(" -storepass $keyStorePassword")
        systemConsole.write(" -keypass $keyPassword")
        systemConsole.write(" -srcstorepass ilovejava")
        systemConsole.writeLine()
    }
    println(systemConsole.read())
    systemConsole.writeLine("rm keystore.pkcs12")
    return storage.read("certificate.jks")

}