package d2.polpharma.services.db;


import ic.redis.RedisStorage;
import ic.storage.BindingStorage;


public class PolpharmaRedisDataBase extends BindingStorage {

	private PolpharmaRedisDataBase() {

		super (
			new RedisStorage("localhost", "3c0541dc50f500e0")
		);

	}

	public static final PolpharmaRedisDataBase POLPHARMA_REDIS_DATABASE = new PolpharmaRedisDataBase();

}
