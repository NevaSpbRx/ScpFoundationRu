package ru.dante.scpfoundation.db;

import android.util.Pair;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.db.error.ScpNoArticleForIdError;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.db.model.VkImage;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class DbProvider {
    private Realm mRealm;

    DbProvider() {
        mRealm = Realm.getDefaultInstance();
    }

    public void close() {
        mRealm.close();
    }

    public Observable<RealmResults<Article>> getArticlesSortedAsync(String field, Sort order) {
        return mRealm.where(Article.class)
                .notEqualTo(field, Article.ORDER_NONE)
                .findAllSortedAsync(field, order)
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public Observable<RealmResults<Article>> getOfflineArticlesSortedAsync(String field, Sort order) {
        return mRealm.where(Article.class)
                .notEqualTo(Article.FIELD_TEXT, (String) null)
                //remove articles from main activity
                .notEqualTo(Article.FIELD_URL, Constants.Urls.ABOUT_SCP)
                .notEqualTo(Article.FIELD_URL, Constants.Urls.NEWS)
                .notEqualTo(Article.FIELD_URL, Constants.Urls.STORIES)
                .findAllSortedAsync(field, order)
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public Observable<Pair<Integer, Integer>> saveRecentArticlesList(List<Article> apiData, int offset) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //remove all aps from nominees if we update list
                    if (offset == 0) {
                        List<Article> nomineesApps =
                                realm.where(Article.class)
                                        .notEqualTo(Article.FIELD_IS_IN_RECENT, Article.ORDER_NONE)
                                        .findAll();
                        for (Article application : nomineesApps) {
                            application.isInRecent = Article.ORDER_NONE;
                        }
                    }
                    //check if we have app in db and update
                    for (int i = 0; i < apiData.size(); i++) {
                        Article applicationToWrite = apiData.get(i);
                        Article applicationInDb = realm.where(Article.class)
                                .equalTo(Article.FIELD_URL, applicationToWrite.url)
                                .findFirst();
                        if (applicationInDb != null) {
                            applicationInDb.isInRecent = offset + i;
//                                applicationInDb.title = applicationToWrite.title;

                            applicationInDb.rating = applicationToWrite.rating;

                            applicationInDb.authorName = applicationToWrite.authorName;
                            applicationInDb.authorUrl = applicationToWrite.authorUrl;

                            applicationInDb.createdDate = applicationToWrite.createdDate;
                            applicationInDb.updatedDate = applicationToWrite.updatedDate;
                        } else {
                            applicationToWrite.isInRecent = offset + i;
                            realm.insertOrUpdate(applicationToWrite);
                        }
                    }
                },
                () -> {
                    subscriber.onNext(new Pair<>(apiData.size(), offset));
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<Pair<Integer, Integer>> saveRatedArticlesList(List<Article> data, int offset) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //remove all aps from nominees if we update list
                    if (offset == 0) {
                        List<Article> articleList =
                                realm.where(Article.class)
                                        .notEqualTo(Article.FIELD_IS_IN_MOST_RATED, Article.ORDER_NONE)
                                        .findAll();
                        for (Article application : articleList) {
                            application.isInMostRated = Article.ORDER_NONE;
                        }
                    }
                    //check if we have app in db and update
                    for (int i = 0; i < data.size(); i++) {
                        Article applicationToWrite = data.get(i);
                        Article applicationInDb = realm.where(Article.class)
                                .equalTo(Article.FIELD_URL, applicationToWrite.url)
                                .findFirst();
                        if (applicationInDb != null) {
                            applicationInDb.isInMostRated = offset + i;
//                                applicationInDb.title = applicationToWrite.title;

                            applicationInDb.rating = applicationToWrite.rating;
                        } else {
                            applicationToWrite.isInMostRated = offset + i;
                            realm.insertOrUpdate(applicationToWrite);
                        }
                    }
                },
                () -> {
                    subscriber.onNext(new Pair<>(data.size(), offset));
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<Pair<Integer, Integer>> saveObjectsArticlesList(List<Article> data, String inDbField) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //remove all aps from this list while update it
                    List<Article> articleList =
                            realm.where(Article.class)
                                    .notEqualTo(inDbField, Article.ORDER_NONE)
                                    .findAll();
                    for (Article application : articleList) {
                        switch (inDbField) {
                            case Article.FIELD_IS_IN_OBJECTS_1:
                                application.isInObjects1 = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_OBJECTS_2:
                                application.isInObjects2 = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_OBJECTS_3:
                                application.isInObjects3 = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_OBJECTS_RU:
                                application.isInObjectsRu = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_EXPERIMETS:
                                application.isInExperiments = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_INCIDENTS:
                                application.isInIncidents = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_INTERVIEWS:
                                application.isInInterviews = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_OTHER:
                                application.isInOther = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_ARCHIVE:
                                application.isInArchive = Article.ORDER_NONE;
                                break;
                            case Article.FIELD_IS_IN_JOKES:
                                application.isInJokes = Article.ORDER_NONE;
                                break;
                            default:
                                Timber.e("unexpected inDbField id");
                                break;
                        }
                    }
                    //check if we have app in db and update
                    for (int i = 0; i < data.size(); i++) {
                        Article applicationToWrite = data.get(i);
                        Article applicationInDb = realm.where(Article.class)
                                .equalTo(Article.FIELD_URL, applicationToWrite.url)
                                .findFirst();
                        if (applicationInDb != null) {
                            switch (inDbField) {
                                case Article.FIELD_IS_IN_OBJECTS_1:
                                    applicationInDb.isInObjects1 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_2:
                                    applicationInDb.isInObjects2 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_3:
                                    applicationInDb.isInObjects3 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_RU:
                                    applicationInDb.isInObjectsRu = i;
                                    break;
                                case Article.FIELD_IS_IN_EXPERIMETS:
                                    applicationInDb.isInExperiments = i;
                                    break;
                                case Article.FIELD_IS_IN_INCIDENTS:
                                    applicationInDb.isInIncidents = i;
                                    break;
                                case Article.FIELD_IS_IN_INTERVIEWS:
                                    applicationInDb.isInInterviews = i;
                                    break;
                                case Article.FIELD_IS_IN_OTHER:
                                    applicationInDb.isInOther = i;
                                    break;
                                case Article.FIELD_IS_IN_ARCHIVE:
                                    applicationInDb.isInArchive = i;
                                    break;
                                case Article.FIELD_IS_IN_JOKES:
                                    applicationInDb.isInJokes = i;
                                    break;
                                default:
                                    Timber.e("unexpected inDbField id");
                                    break;
                            }
                            applicationInDb.title = applicationToWrite.title;

                            applicationInDb.type = applicationToWrite.type;
                        } else {
                            applicationToWrite.isInMostRated = i;
                            switch (inDbField) {
                                case Article.FIELD_IS_IN_OBJECTS_1:
                                    applicationToWrite.isInObjects1 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_2:
                                    applicationToWrite.isInObjects2 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_3:
                                    applicationToWrite.isInObjects3 = i;
                                    break;
                                case Article.FIELD_IS_IN_OBJECTS_RU:
                                    applicationToWrite.isInObjectsRu = i;
                                    break;
                                case Article.FIELD_IS_IN_EXPERIMETS:
                                    applicationToWrite.isInExperiments = i;
                                    break;
                                case Article.FIELD_IS_IN_INCIDENTS:
                                    applicationToWrite.isInIncidents = i;
                                    break;
                                case Article.FIELD_IS_IN_INTERVIEWS:
                                    applicationToWrite.isInInterviews = i;
                                    break;
                                case Article.FIELD_IS_IN_OTHER:
                                    applicationToWrite.isInOther = i;
                                    break;
                                case Article.FIELD_IS_IN_ARCHIVE:
                                    applicationToWrite.isInArchive = i;
                                    break;
                                case Article.FIELD_IS_IN_JOKES:
                                    applicationToWrite.isInJokes = i;
                                    break;
                                default:
                                    Timber.e("unexpected inDbField id");
                                    break;
                            }
                            realm.insertOrUpdate(applicationToWrite);
                        }
                    }
                },
                () -> {
                    subscriber.onNext(new Pair<>(data.size(), data.size()));
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    /**
     * @param articleUrl used as ID
     * @return Observable that emits managed, valid and loaded Article
     * and emits changes to it
     * or null if there is no one in DB with this url
     */
    public Observable<Article> getArticleAsync(String articleUrl) {
        return mRealm.where(Article.class)
                .equalTo(Article.FIELD_URL, articleUrl)
                .findAllAsync()
                .<List<Article>>asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid)
                .flatMap(arts -> arts.isEmpty() ? Observable.just(null) : Observable.just(arts.first()));
    }

    public Article getUnmanagedArticleSync(String url) {
        Article articleFromDb = mRealm.where(Article.class).equalTo(Article.FIELD_URL, url).findFirst();
        return articleFromDb == null ? null : mRealm.copyFromRealm(articleFromDb);
    }

    public Article getArticleSync(String url) {
        return mRealm.where(Article.class).equalTo(Article.FIELD_URL, url).findFirst();
    }

    /**
     * @param article obj to save
     * @return Observable that emits unmanaged saved article on successful insert or throws error
     */
    public Observable<Article> saveArticle(Article article) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //check if we have app in db and update
                    Article applicationInDb = realm.where(Article.class)
                            .equalTo(Article.FIELD_URL, article.url)
                            .findFirst();
                    if (applicationInDb != null) {
                        applicationInDb = realm.copyFromRealm(applicationInDb);
                        applicationInDb.text = article.text;
                        applicationInDb.title = article.title;
                        //tabs
                        applicationInDb.hasTabs = article.hasTabs;
                        applicationInDb.tabsTitles = article.tabsTitles;
                        applicationInDb.tabsTexts = article.tabsTexts;
                        //textParts
                        applicationInDb.textParts = article.textParts;
                        applicationInDb.textPartsTypes = article.textPartsTypes;
                        //images
                        applicationInDb.imagesUrls = article.imagesUrls;
                        //update localUpdateTimeStamp to be able to sort arts by this value
                        applicationInDb.localUpdateTimeStamp = System.currentTimeMillis();

                        //update it in DB such way, as we add unmanaged items
                        realm.insertOrUpdate(applicationInDb);
                    } else {
                        realm.insertOrUpdate(article);
                    }
                },
                () -> {
                    subscriber.onNext(article);
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<Pair<String, Long>> toggleFavorite(String url) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //check if we have app in db and update
                    Article applicationInDb = realm.where(Article.class)
                            .equalTo(Article.FIELD_URL, url)
                            .findFirst();
                    if (applicationInDb != null) {
                        if (applicationInDb.isInFavorite == Article.ORDER_NONE) {
                            applicationInDb.isInFavorite = (long) realm.where(Article.class)
//                                        .notEqualTo(Article.FIELD_IS_IN_FAVORITE, Article.ORDER_NONE)
                                    .max(Article.FIELD_IS_IN_FAVORITE) + 1;
                        } else {
                            applicationInDb.isInFavorite = Article.ORDER_NONE;
                        }

                        subscriber.onNext(new Pair<>(url, applicationInDb.isInFavorite));
                        subscriber.onCompleted();
                    } else {
                        Timber.e("No article to add to favorites for ID: %s", url);
                        subscriber.onError(new ScpNoArticleForIdError(url));
                    }
                },
                () -> {
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    /**
     * @param url used as Article ID
     * @return observable, that emits resulted readen state
     * or error if no artcile found
     */
    public Observable<Pair<String, Boolean>> toggleReaden(String url) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //check if we have app in db and update
                    Article applicationInDb = realm.where(Article.class)
                            .equalTo(Article.FIELD_URL, url)
                            .findFirst();
                    if (applicationInDb != null) {
                        applicationInDb.isInReaden = !applicationInDb.isInReaden;
                        subscriber.onNext(new Pair<>(url, applicationInDb.isInReaden));
                        subscriber.onCompleted();
                    } else {
                        Timber.e("No article to add to favorites for ID: %s", url);
                        subscriber.onError(new ScpNoArticleForIdError(url));
                    }
                },
                () -> {
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<String> deleteArticlesText(String url) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //check if we have app in db and update
                    Article applicationInDb = realm.where(Article.class)
                            .equalTo(Article.FIELD_URL, url)
                            .findFirst();
                    if (applicationInDb != null) {
                        applicationInDb.text = null;
                        applicationInDb.textParts = null;
                        applicationInDb.textPartsTypes = null;
                        applicationInDb.hasTabs = false;
                        applicationInDb.tabsTexts = null;
                        applicationInDb.tabsTitles = null;

                        subscriber.onNext(url);
                        subscriber.onCompleted();
                    } else {
                        Timber.e("No article to add to favorites for ID: %s", url);
                        subscriber.onError(new ScpNoArticleForIdError(url));
                    }
                },
                () -> {
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<User> getUserAsync() {
        return mRealm.where(User.class)
                .findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid)
                .flatMap(users -> Observable.just(users.isEmpty() ? null : users.first()));
    }

    public Observable<Void> saveUser(User user) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> realm.insertOrUpdate(user),
                () -> {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<Void> deleteUser() {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> realm.delete(User.class),
                () -> {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<Void> saveImages(List<VkImage> vkImages) {
        return Observable.create(subscriber -> mRealm.executeTransactionAsync(
                realm -> {
                    //clear
                    realm.delete(VkImage.class);
                    realm.insertOrUpdate(vkImages);
                },
                () -> {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                    mRealm.close();
                },
                error -> {
                    subscriber.onError(error);
                    mRealm.close();
                }));
    }

    public Observable<List<VkImage>> getGalleryImages() {
        return mRealm.where(VkImage.class)
                .findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid)
                .flatMap(realmResults -> Observable.just(mRealm.copyFromRealm(realmResults)));
    }
}