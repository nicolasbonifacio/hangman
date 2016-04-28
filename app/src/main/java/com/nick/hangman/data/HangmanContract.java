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
    public static final String PATH_TALE_SCORE_CATEGORY = "tale_score_category";
    public static final String PATH_LEVEL = "level";
    public static final String PATH_WORD = "word";
    public static final String PATH_SCORE_MODEL = "score_model";
    public static final String PATH_TALE_OVERALL = "tale_overall";

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
        public static final String COLUMN_LAST_USED = "last_used";
        public static final String COLUMN_TALE_PLAYER = "tale_player";

        public static Uri buildPlayerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLastUsedFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildPlayerLastUsed(int flag) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(flag)).build();
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
        public static final String COLUMN_LAST_USED = "last_used";

        public static Uri buildLanguageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLastUsedFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildLanguageLastUsed(int flag) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(flag)).build();
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

    /* Inner class that defines the table contents of the tale_score_category table */
    public static final class TaleScoreCategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TALE_SCORE_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TALE_SCORE_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TALE_SCORE_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "tale_score_category";

        // Columns
        public static final String COLUMN_LOC_KEY_PLAYER = "player_id";
        public static final String COLUMN_LOC_KEY_CATEGORY = "category_id";
        public static final String COLUMN_PLAYER_SCORE = "player_score";
        public static final String COLUMN_PATH_ORDER = "path_order";
        public static final String COLUMN_CATEGORY_ENABLED = "category_enabled";
        public static final String COLUMN_CATEGORY_ENABLE_SCORE = "category_enable_score";

        public static Uri buildTaleScoreCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTaleScoreCategoryWithPlayerCategory(int talePayer, int language) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(talePayer))
                    .appendPath(Integer.toString(language))
                    .build();
        }

        public static String getTalePlayerFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getLanguageLastUsedFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
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

        public static Uri buildWordWithLanguageCategoryLevel(int language, int category, int isUsed, int level) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(language))
                    .appendPath(Integer.toString(category))
                    .appendPath(Integer.toString(isUsed))
                    .appendPath(Integer.toString(level))
                    .build();
        }

        public static Uri buildWordWithLanguageCategory(int language, int category, int isUsed) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(language))
                    .appendPath(Integer.toString(category))
                    .appendPath(Integer.toString(isUsed))
                    .build();
        }

        public static Uri buildCountWordsUsageWithLanguageCategoryLevel(int language, int category, int level) {
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

        public static String getIsUsedFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }

        public static String getLevelFromUri(Uri uri) {
            return uri.getPathSegments().get(4);
        }

        public static String getLevelForWordUsageFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }

    }

    /* Inner class that defines the table contents of the score_model table */
    public static final class ScoreModelEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORE_MODEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE_MODEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE_MODEL;

        // Table name
        public static final String TABLE_NAME = "score_model";

        // Columns
        public static final String COLUMN_LOC_KEY_CATEGORY = "category_id";
        public static final String COLUMN_NUM_ERRORS = "num_errors";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_QTD_STARS = "qtd_stars";

        public static Uri buildScoreModelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getNumErrorsFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildScoreModelWithCategoryNumErrors(int category, int numErrors) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(category))
                    .appendPath(Integer.toString(numErrors))
                    .build();
        }

    }

    /* Inner class that defines the table contents of the tale_overall table */
    public static final class TaleOverallEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_TALE_OVERALL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TALE_OVERALL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TALE_OVERALL;

        // Table name
        public static final String TABLE_NAME = "tale_overall";

        // Columns
        public static final String COLUMN_LOC_KEY_PLAYER = "player_id";
        public static final String COLUMN_LOC_KEY_CATEGORY = "category_id";
        public static final String COLUMN_LOC_KEY_WORD = "word_id";
        public static final String COLUMN_NUM_STARS = "num_stars";

        public static Uri buildTaleOverallUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTaleOverallWithPlayerCategory(int player, int category) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(player))
                    .appendPath(Integer.toString(category))
                    .build();
        }

        public static Uri buildTaleOverallWithPlayer(int player) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(player))
                    .build();
        }

        public static String getPlayerFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }

}
