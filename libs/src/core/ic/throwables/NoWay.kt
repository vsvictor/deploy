package ic.throwables


open class NoWay : Exception () {



}


object NO_WAY : NoWay() { override fun fillInStackTrace() = null }