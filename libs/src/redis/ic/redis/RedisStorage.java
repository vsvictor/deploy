package ic.redis;


import ic.interfaces.getter.Getter1;
import ic.interfaces.openable.Openable;
import ic.interfaces.setter.Setter1;
import ic.serial.stringmap.StringMapSerializable;
import ic.storage.Storage;
import ic.storage.StringMapStorage;
import ic.struct.order.OrderedCountableSet;
import ic.struct.collection.ConvertCollection;
import ic.struct.set.EditableSet;
import ic.text.Charset;
import ic.throwables.AlreadyExists;
import ic.throwables.InvalidValue;
import ic.throwables.NotExists;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.Skip.SKIP;


public class RedisStorage extends StringMapStorage {


	private final Jedis jedis;

	private final @Nullable String path;


	private String getFullName(String name) {
		final String encodedName = Charset.UTF_8.encodeUrl(name);
		if (path == null) return encodedName;
		else return path + "/" + encodedName;
	}


	@Override protected Getter1<String, String> getInput(String name) throws NotExists {
		final String fullName = getFullName(name);
		final java.util.Map<String, String> javaMap; synchronized (jedis) {
			javaMap = jedis.hgetAll(fullName);
		}
		return javaMap::get;
	}

	@Override protected Setter1<String, String> getOutput(String name) {
		final String fullName = getFullName(name);
		class RedisOutput implements Setter1<String, String>, Openable {
			@Override public void open() {
				synchronized (jedis) {
					for (String key : jedis.keys(fullName + "/*")) jedis.del(key);
					for (String key : jedis.hkeys(fullName)) {
						jedis.hdel(fullName, key);
					}
				}
			}
			@Override public void set(String key, String value) {
				synchronized (jedis) {
					if (value == null) 	jedis.hdel(fullName, key);
					else 				jedis.hset(fullName, key, value);
				}
			}
		}
		return new RedisOutput();
	}

	@Override public OrderedCountableSet<String> getKeys() { // TODO: Reimplement
		final String prefix; {
			if (path == null) 	prefix = "";
			else 				prefix = path + "/";
		}
		final java.util.Set<String> jedisKeys; {
			synchronized (jedis) {
				jedisKeys = jedis.keys(prefix + "*");
			}
		}
		return new OrderedCountableSet.Default<>(
			ALPHABETIC_STRING_ORDER,
			new ConvertCollection<>(
				new EditableSet.FromJavaSet<>(jedisKeys),
				rawKey -> {
					if (!rawKey.startsWith(prefix)) throw new InvalidValue.Error("rawKey:");
					String key = rawKey.substring(prefix.length());
					if (key.contains("/")) throw SKIP;
					return Charset.UTF_8.decodeUrl(key);
				}
			)
		);
	}

	@Override public boolean isFolder(String key) throws NotExists {
		final String fullName = getFullName(key);
		final String declaredClassName; {
			synchronized (jedis) {
				declaredClassName = jedis.hget(fullName, StringMapSerializable.KEY_CLASS);
			}
		}
		if (declaredClassName == null) 			throw NOT_EXISTS;
		if (declaredClassName.equals("null")) 	throw NOT_EXISTS;
		return declaredClassName.equals(RedisStorage.class.getName());
	}

	@Override public Storage getParent() { return null; }

	@Override public String getName() { return null; }

	@Override protected Storage implementGetFolder(String key) throws NotExists {
		final String fullName = getFullName(key);
		return new RedisStorage(jedis, fullName);
	}

	@Override public void removeOrThrow(String key) throws NotExists {
		final String fullName = getFullName(key);
		System.out.println("RedisStorage remove " + fullName);
		synchronized (jedis) {
			for (String subkey : jedis.keys(fullName + "/*")) jedis.del(subkey);
			jedis.del(fullName);
		}
	}

	@Override protected Storage implementCreateFolder(String key) throws AlreadyExists {
		final String fullName = getFullName(key);
		synchronized (jedis) {
			final String declaredClassName = jedis.hget(fullName, StringMapSerializable.KEY_CLASS);
			if (declaredClassName != null && !declaredClassName.equals("null")) throw ALREADY_EXISTS;
			jedis.hset(fullName, StringMapSerializable.KEY_CLASS, RedisStorage.class.getName());
		}
		return new RedisStorage(jedis, fullName);
	}


	// Constructors:

	// Private:

	private RedisStorage(Jedis jedis, String path) {
		this.jedis = jedis;
		this.path = path;
	}

	private RedisStorage(Jedis jedis) {
		this(jedis, null);
	}


	// Public:

	private static final Jedis createJedis(final String host, final int port, final String password) {
		final Jedis jedis = new Jedis(host, port);
		jedis.auth(password);
		return jedis;
	}

	public RedisStorage(final String host, final String password, final int port) {
		this(createJedis(host, port, password));
	}


	private static final Jedis createJedis(final String host, final String password) {
		final Jedis jedis = new Jedis(host);
		jedis.auth(password);
		return jedis;
	}

	public RedisStorage(final String host, final String password) {
		this(createJedis(host, password));
	}


}
