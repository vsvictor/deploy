package d2.polpharma.services.prm


import d2.modules.admins.checkAdminAuth
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import ic.network.http.BasicHttpAuth


fun checkPolpharmaPrmAdminAuth (auth : BasicHttpAuth) = checkAdminAuth(
	{ checkPolpharmaSuperadminAuth(it) },
	polpharmaPrmAdminsStorage,
	auth
)