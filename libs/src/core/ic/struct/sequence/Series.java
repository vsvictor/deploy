package ic.struct.sequence;


import ic.interfaces.getter.SafeGetter;
import ic.throwables.End;


public interface Series<Item> extends SafeSeries<Item, RuntimeException>, SafeGetter<Item, End> {


}
