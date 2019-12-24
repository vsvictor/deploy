package ic.interfaces.getter


import ic.annotations.Repeat
import ic.throwables.None


interface UntypedGetter1<Arg> : SafeUntypedGetter1<Arg, None> {

	@JvmDefault
	operator @Repeat override fun <Type> get (arg: Arg) : Type = super.get(arg)

}
