package jp.s64.repoli.rxjava1.base;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import org.junit.Test;

import jp.s64.java.repoli.core.DataKey;
import jp.s64.java.repoli.core.IRepositoryDataContainer;
import jp.s64.java.repoli.core.ISerializer;
import jp.s64.java.repoli.internal.ReturningRepositoryDataContainer;
import jp.s64.java.repoli.preset.serializer.SerializableSerializer;
import jp.s64.java.repoli.rxjava1.base.BaseRxProvider;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

/**
 * Created by shuma on 2017/05/26.
 */

public class BaseRxProviderTest {

    @Test
    public void test() {
        final DataKey<String, String> key = new DataKey<>(TypeToken.of(String.class), TypeToken.of(String.class), "", "");

        final String bodyValue, attachmentValue;
        final ReturningRepositoryDataContainer<byte[], byte[]> org = new ReturningRepositoryDataContainer<>();
        {
            org.setBody(SerializableSerializer.INSTANCE.serialize(key.getBodyType(), bodyValue = "body-value", Sets.<ISerializer>newHashSet()));
            org.setAttachment(SerializableSerializer.INSTANCE.serialize(key.getBodyType(), attachmentValue = "attachment-value", Sets.<ISerializer>newHashSet()));
        }

        BaseRxProvider provider = new BaseRxProvider() {

            @Override
            public Observable<IRepositoryDataContainer<byte[], byte[]>> requestBySerializedKey(String serializedKey) {
                return Observable.<IRepositoryDataContainer<byte[], byte[]>>just(org);
            }

        };

        Observable<IRepositoryDataContainer<String, String>> obs = provider.request(key);

        TestSubscriber<IRepositoryDataContainer<String, String>> sub = new TestSubscriber<>();

        obs.subscribe(sub);

        sub.awaitTerminalEvent();

        sub.assertNoErrors();
        sub.assertValueCount(1);

        IRepositoryDataContainer<String, String> ret = sub.getOnNextEvents().get(0);

        assertEquals(bodyValue, ret.getBody());
        assertEquals(attachmentValue, ret.getAttachment());
    }

}
