package ru.dante.scpfoundation.receivers;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProvider;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.activity.MainActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static ru.dante.scpfoundation.Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE;

public class ReceiverTimer extends BroadcastReceiver {

    public static final int NOTIF_ID = 963;

    @Inject
    MyPreferenceManager mMyPreferencesManager;
    @Inject
    DbProviderFactory mDbProviderFactory;
    @Inject
    ApiClient mApiClient;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive with action: %s", intent.getAction());
        if (context.getString(R.string.receiver_action_timer).equals(intent.getAction())) {
            MyApplication.getAppComponent().inject(this);
            download(context);
        }
    }

    protected void download(Context context) {
        mApiClient.getRecentArticlesForPage(1)
                .flatMap(articles -> {
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();
                    List<Article> newArticles = new ArrayList<>();
                    for (Article apiArticle : articles) {
                        Article inDbArticle = dbProvider.getArticleSync(apiArticle.url);
                        if (inDbArticle == null) {
                            //so its new one, increase counter
                            newArticles.add(apiArticle);
                        }
                    }
                    dbProvider.close();
                    return Observable.just(newArticles);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(newArticles -> mDbProviderFactory.getDbProvider()
                        .saveRecentArticlesList(newArticles, Constants.Api.ZERO_OFFSET)
                        .flatMap(savedArts -> Observable.just(newArticles)))
                .subscribe(
                        result -> {
                            sendNotification(context, result);
                        }
                        , error -> Timber.e(error, "error while getRecentArts"));
    }

    public void sendNotification(Context ctx, List<Article> dataFromWeb) {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.scp_24x24);

        //Set the text that is displayed in the status bar when the notification first arrives.
        builder.setTicker(dataFromWeb.get(0).title);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_LINK, Constants.Urls.NEW_ARTICLES);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        //		builder.setContentTitle("Новые статьи");

        // Content text, which appears in smaller text below the title
        //				builder.setContentText("Новые статьи");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText(dataFromWeb.get(0).title);//"Всего новых статей:");

        builder.setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if (dataFromWeb.size() == NUM_OF_ARTICLES_ON_RECENT_PAGE) {
            String[] events = new String[dataFromWeb.size()];
            //TODO save to res
            inboxStyle.setBigContentTitle("Новые статьи: " + dataFromWeb.size() + "+");
            // Moves events into the expanded layout
            for (int i = 0; i < events.length; i++) {
                events[i] = dataFromWeb.get(i).title;
                inboxStyle.addLine(events[i]);
            }
            builder.setNumber(dataFromWeb.size());
        } else {
            //to test
            //newQuont = "10";
            String[] events = new String[dataFromWeb.size()];
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("Новые статьи: " + dataFromWeb.size());
            // Moves events into the expanded layout
            for (int i = 0; i < events.length; i++) {
                events[i] = dataFromWeb.get(i).title;
                inboxStyle.addLine(events[i]);
            }
            builder.setNumber(dataFromWeb.size());
        }

        // Moves the expanded layout object into the notification object.
        builder.setStyle(inboxStyle);

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Новые статьи: " + dataFromWeb.size());
        if (mMyPreferencesManager.isNotificationVibrationEnabled()) {
            //TODO move to const
            builder.setVibrate(new long[]{500, 500, 500, 500, 500});
        }
        //LED
        if (mMyPreferencesManager.isNotificationLedEnabled()) {
            //TODO move to const
            builder.setLights(Color.WHITE, 3000, 3000);
        }
        //Sound//LED
        if (mMyPreferencesManager.isNotificationSoundEnabled()) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        //TODO move to const
        notificationManager.notify(NOTIF_ID, builder.build());
    }
}