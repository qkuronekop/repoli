package jp.s64.java.repoli.realm;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import io.realm.Realm;
import io.realm.RealmResults;
import jp.s64.java.repoli.core.IRepositoryDataContainer;
import jp.s64.java.repoli.internal.ReturningRepositoryDataContainer;
import jp.s64.java.repoli.realm.core.IRealmStorage;
import jp.s64.java.repoli.rxjava1.base.BaseRxStorage;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by shuma on 2017/05/27.
 */

public abstract class RealmBinaryStorage extends BaseRxStorage implements IRealmStorage {

    @Override
    public Observable<IRepositoryDataContainer<byte[], byte[]>> getBySerializedKey(final String serializedKey) {
        final Supplier<Realm> realm = Suppliers.memoize(new Supplier<Realm>() {
            @Override
            public Realm get() {
                return getRealmInstance();
            }
        });

        return Observable
                .<Void>just(null)
                .observeOn(getScheduler())
                .map(new Func1<Void, RealmResults<BinaryStorageObject>>() {

                    @Override
                    public RealmResults<BinaryStorageObject> call(Void _) {
                        return realm.get()
                                .where(BinaryStorageObject.class)
                                .equalTo("serializedKey", serializedKey)
                                .findAll();
                    }

                })
                .map(new Func1<RealmResults<BinaryStorageObject>, IRepositoryDataContainer<byte[], byte[]>>() {
                    @Override
                    public IRepositoryDataContainer<byte[], byte[]> call(RealmResults<BinaryStorageObject> rows) {
                        return rows.size() > 0 ? new ReturningRepositoryDataContainer<byte[], byte[]>(rows.first()) : new ReturningRepositoryDataContainer<byte[], byte[]>();
                    }
                })
                .doOnNext(new Action1<IRepositoryDataContainer<byte[], byte[]>>() {
                    @Override
                    public void call(IRepositoryDataContainer<byte[], byte[]> _) {
                        if (closeAfter()) realm.get().close();
                    }
                });
    }

    @Override
    public Observable<Integer> removeBySerializedKey(final String serializedKey) {
        final Supplier<Realm> realm = Suppliers.memoize(new Supplier<Realm>() {
            @Override
            public Realm get() {
                return getRealmInstance();
            }
        });

        return Observable
                .<Void>just(null)
                .observeOn(getScheduler())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void _) {
                        realm.get().beginTransaction();

                    }
                })
                .map(new Func1<Void, RealmResults<BinaryStorageObject>>() {

                    @Override
                    public RealmResults<BinaryStorageObject> call(Void _) {
                        return realm.get()
                                .where(BinaryStorageObject.class)
                                .equalTo("serializedKey", serializedKey)
                                .findAll();
                    }

                })
                .map(new Func1<RealmResults<BinaryStorageObject>, Integer>() {
                    @Override
                    public Integer call(RealmResults<BinaryStorageObject> rows) {
                        int ret = rows.size();
                        {
                            rows.deleteAllFromRealm();
                        }
                        return ret;
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer _) {
                        realm.get().commitTransaction();
                        if (closeAfter()) realm.get().close();
                    }
                });
    }

    @Override
    public Observable<Integer> removeRelativesByRelatedKey(final String relatedKey) {
        final Supplier<Realm> realm = Suppliers.memoize(new Supplier<Realm>() {
            @Override
            public Realm get() {
                return getRealmInstance();
            }
        });

        return Observable
                .<Void>just(null)
                .observeOn(getScheduler())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void _) {
                        realm.get().beginTransaction();
                    }
                })
                .map(new Func1<Void, RealmResults<BinaryStorageObject>>() {

                    @Override
                    public RealmResults<BinaryStorageObject> call(Void _) {
                        return realm.get()
                                .where(BinaryStorageObject.class)
                                .equalTo("relatedKey", relatedKey)
                                .findAll();
                    }

                })
                .map(new Func1<RealmResults<BinaryStorageObject>, Integer>() {

                    @Override
                    public Integer call(RealmResults<BinaryStorageObject> rows) {
                        int ret = rows.size();
                        {
                            rows.deleteAllFromRealm();
                        }
                        return ret;
                    }

                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer _) {
                        realm.get().commitTransaction();
                        if (closeAfter()) realm.get().close();
                    }
                });
    }

    @Override
    public Observable<Void> saveBySerializedKey(final String serializedKey, final String relatedKey, final IRepositoryDataContainer<byte[], byte[]> container) {
        final Supplier<Realm> realm = Suppliers.memoize(new Supplier<Realm>() {
            @Override
            public Realm get() {
                return getRealmInstance();
            }
        });

        return Observable
                .<Void>just(null)
                .observeOn(getScheduler())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void _) {
                        realm.get().beginTransaction();
                    }
                })
                .map(new Func1<Void, BinaryStorageObject>() {
                    @Override
                    public BinaryStorageObject call(Void _) {
                        BinaryStorageObject obj = realm.get().createObject(BinaryStorageObject.class, serializedKey);
                        {
                            //obj.setSerializedKey(serializedKey);
                            obj.setRelatedKey(relatedKey);
                        }
                        {
                            obj.setSavedAtTimeMillis(container.getSavedAtTimeMillis());
                            obj.setRequestedAtTimeMillis(container.getRequestedAtTimeMillis());
                            obj.setBody(container.getBody());
                            obj.setAttachment(container.getAttachment());
                        }
                        return obj;
                    }
                })
                .doOnNext(new Action1<BinaryStorageObject>() {
                    @Override
                    public void call(BinaryStorageObject obj) {
                        realm.get().insertOrUpdate(obj);
                    }
                })
                .doOnNext(new Action1<BinaryStorageObject>() {
                    @Override
                    public void call(BinaryStorageObject obj) {
                        realm.get().commitTransaction();
                        if (closeAfter()) realm.get().close();
                    }
                })
                .map(new Func1<BinaryStorageObject, Void>() {
                    @Override
                    public Void call(BinaryStorageObject obj) {
                        return null; // succeed
                    }
                });
    }

}