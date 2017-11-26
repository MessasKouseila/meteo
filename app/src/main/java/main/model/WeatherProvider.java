package main.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class WeatherProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.loubia.tp_meteo";
    public static final String PATH_TO_DATA = "weather";
    // URI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_TO_DATA);
    // Types MIME
    public static final String TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH_TO_DATA;
    public static final String TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH_TO_DATA;
    private static final UriMatcher membreMatcher;
    private static final int DIR = 0;
    private static final int ITEM = 1;

    //
    static {
        membreMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        membreMatcher.addURI(AUTHORITY, PATH_TO_DATA, DIR);
        membreMatcher.addURI(AUTHORITY, PATH_TO_DATA + "/*/*", ITEM);
    }

    private CityDB dbOpenHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbOpenHelper = new CityDB(context);
        db = dbOpenHelper.getWritableDatabase();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CityDB.TABLE_NAME);
        // S'il s'agit d'une requête sur une ligne, on limite le résultat.
        switch (membreMatcher.match(uri)) {
            case ITEM:
                qb.appendWhere(CityDB.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                break;
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CityDB.COLUMN_NAME;
        } else {
            orderBy = sortOrder;
        }

        // Applique la requête à la base.
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Enregistre le ContextResolver pour qu'il soit averti
        // si le résultat change.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Renvoie un curseur.
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (membreMatcher.match(uri)) {
            case DIR:
                return TYPE_DIR;
            case ITEM:
                return TYPE_ITEM;
            default:
                throw new IllegalArgumentException("URI non supportée : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri _uri, @Nullable ContentValues values) {
        // Insère la nouvelle ligne. Renvoie son numéro en cas de succès
        long rowID = db.insert(CityDB.TABLE_NAME, "data", values);

        // Renvoie l'URI de la nouvelle ligne.
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Echec de l'ajout d'une ligne dans " + _uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;

        switch (membreMatcher.match(uri)) {
            case DIR:
                count = db.delete(CityDB.TABLE_NAME, selection, selectionArgs);
                break;

            case ITEM:
                String segment = uri.getPathSegments().get(1);
                count = db.delete(CityDB.TABLE_NAME, CityDB.COLUMN_ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("URI non supportée : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch (membreMatcher.match(uri)) {
            case DIR:
                count = db.update(CityDB.TABLE_NAME, values, selection, selectionArgs);
                break;

            case ITEM:
                String segment = uri.getPathSegments().get(1);
                count = db.update(CityDB.TABLE_NAME, values, CityDB.COLUMN_ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("URI inconnue " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
