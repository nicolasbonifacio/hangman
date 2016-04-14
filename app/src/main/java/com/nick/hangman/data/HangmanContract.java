package com.nick.hangman.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nick on 2016-04-12.
 */
public class HangmanContract {

    public static final String CONTENT_AUTHORITY = "com.nick.hangman";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PLAYER = "player";
    public static final String PATH_LANGUAGE = "language";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_LEVEL = "level";
    public static final String PATH_WORD = "word";

    /* Inner class that defines the table contents of the player table */
    public static final class PlayerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;

        // Table name
        public static final String TABLE_NAME = "player";

        // Columns
        public static final String COLUMN_DESCR_NAME = "descr_name";
        public static final String COLUMN_SCORE = "score";

        public static Uri buildPlayerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the language table */
    public static final class LanguageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LANGUAGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGE;

        // Table name
        public static final String TABLE_NAME = "language";

        // Columns
        public static final String COLUMN_DESCR_LANGUAGE = "descr_language";

        public static Uri buildLanguageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the category table */
    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "category";

        // Columns
        public static final String COLUMN_LOC_KEY_LANGUAGE = "language_id";
        public static final String COLUMN_DESCR_CATEGORY = "descr_category";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLanguageFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildCategoryLanguage(int language) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(language)).build();
        }

    }

    /* Inner class that defines the table contents of the level table */
    public static final class LevelEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEVEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEVEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LEVEL;

        // Table name
        public static final String TABLE_NAME = "level";

        // Columns
        public static final String COLUMN_LOC_KEY_LANGUAGE = "language_id";
        public static final String COLUMN_DESCR_LEVEL = "descr_level";

        public static Uri buildLevelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLanguageFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildLevelLanguage(int language) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(language)).build();
        }

    }

    /* Inner class that defines the table contents of the word table */
    public static final class WordEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORD;

        // Table name
        public static final String TABLE_NAME = "word";

        // Columns
        public static final String COLUMN_LOC_KEY_LANGUAGE = "language_id";
        public static final String COLUMN_LOC_KEY_CATEGORY = "category_id";
        public static final String COLUMN_LOC_KEY_LEVEL = "level_id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_WORD_USED = "word_used";

        public static Uri buildWordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWordWithLanguageCategoryLevel(int language, int category, int level) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(language))
                    .appendPath(Integer.toString(category))
                    .appendPath(Integer.toString(level))
                    .build();
        }

        public static String getLanguageFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getLevelFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }

    }

}
