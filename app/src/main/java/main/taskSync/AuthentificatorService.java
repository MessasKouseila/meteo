package main.taskSync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import main.model.WeatherProvider;


/**
 * Created by hicham on 26/11/2017.
 */

public class AuthentificatorService extends Service {

    public static final String ACCOUNT = "WeOne";

    public static Account GetAccount() {
        final String accountName = ACCOUNT;
        return new Account(accountName, "app.com");
    }
    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        Log.i("APP", "Service created");
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onDestroy() {
        Log.i("APP", "Service destroyed");
    }

    public static void CreateSyncAccount(Context context) {

        Account account = AuthentificatorService.GetAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, WeatherProvider.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, WeatherProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, WeatherProvider.AUTHORITY, Bundle.EMPTY, 60 * 60);
        }
    }

}
