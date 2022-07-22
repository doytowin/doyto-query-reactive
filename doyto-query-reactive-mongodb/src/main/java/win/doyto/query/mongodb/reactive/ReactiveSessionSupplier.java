package win.doyto.query.mongodb.reactive;

import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;

import java.io.Closeable;
import java.util.function.Supplier;

/**
 * ReactiveSessionSupplier
 *
 * @author f0rb on 2022-07-21
 */
public interface ReactiveSessionSupplier extends Supplier<ClientSession>, Closeable {

    MongoClient getMongoClient();

    ClientSession get();

    void close();

}