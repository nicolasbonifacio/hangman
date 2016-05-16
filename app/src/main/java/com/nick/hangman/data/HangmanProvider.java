package com.nick.hangman.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Nick on 2016-04-13.
 */
public class HangmanProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HangmanDbHelper mOpenHelper;

    static final int PLAYER = 100;
    static final int PLAYER_WITH_LAST_USED = 101;
    static final int LANGUAGE = 200;
    static final int LANGUAGE_WITH_LAST_USED = 201;
    static final int CATEGORY = 300;
    static final int CATEGORY_WITH_LANGUAGE = 301;
    static final int LEVEL = 400;
    static final int LEVEL_WITH_LANGUAGE = 401;
    static final int WORD = 500;
    static final int WORD_WITH_LANGUAGE_AND_CATEGORY_AND_LEVEL_NOT_USED = 501;
    static final int WORD_WITH_LANGUAGE_AND_CATEGORY_NOT_USED = 502;
    static final int WORD_COUNT_USAGE_WITH_LANGUAGE_CATEGORY_LEVEL = 503;
    static final int TALE_SCORE_CATEGORY = 600;
    static final int TALE_SCORE_CATEGORY_WITH_PLAYER_AND_CATEGORY = 601;
    static final int SCORE_MODEL = 700;
    static final int SCORE_MODEL_WITH_CATEGORY_AND_NUM_ERRORS = 701;
    static final int TALE_OVERALL = 800;
    static final int TALE_OVERALL_WITH_PLAYER_AND_CATEGORY = 801;
    static final int TALE_OVERALL_WITH_PLAYER = 802;
    static final int IMAGE = 900;
    static final int IMAGE_WITH_LAST_USED = 901;

    static final String WORD_GROUP_BY_FIELD = "WORD_USED";

    private static final SQLiteQueryBuilder sWordLanguageCategoryLevelQueryBuilder;
    static{
        sWordLanguageCategoryLevelQueryBuilder = new SQLiteQueryBuilder();

        //Inner join on tables WORD, LANGUAGE, CATEGORY AND LEVEL
        sWordLanguageCategoryLevelQueryBuilder.setTables(
                HangmanContract.WordEntry.TABLE_NAME +
                " INNER JOIN " + HangmanContract.LanguageEntry.TABLE_NAME +
                        " ON " + HangmanContract.WordEntry.TABLE_NAME +
                        "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE +
                        " = " + HangmanContract.LanguageEntry.TABLE_NAME +
                        "." + HangmanContract.LanguageEntry._ID +
                " INNER JOIN " + HangmanContract.CategoryEntry.TABLE_NAME +
                        " ON " + HangmanContract.WordEntry.TABLE_NAME +
                        "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY +
                        " = " + HangmanContract.CategoryEntry.TABLE_NAME +
                        "." + HangmanContract.CategoryEntry._ID +
                " INNER JOIN " + HangmanContract.LevelEntry.TABLE_NAME +
                        " ON " + HangmanContract.WordEntry.TABLE_NAME +
                        "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LEVEL +
                        " = " + HangmanContract.LevelEntry.TABLE_NAME +
                        "." + HangmanContract.LevelEntry._ID);

    }

    private static final SQLiteQueryBuilder sTaleScoreCategoryPlayerCategoryQueryBuilder;
    static{
        sTaleScoreCategoryPlayerCategoryQueryBuilder = new SQLiteQueryBuilder();

        //Inner join on tables TALE_SCORE_CATEGORY, PLAYER AND CATEGORY
        sTaleScoreCategoryPlayerCategoryQueryBuilder.setTables(
                HangmanContract.TaleScoreCategoryEntry.TABLE_NAME +
                        " INNER JOIN " + HangmanContract.PlayerEntry.TABLE_NAME +
                        " ON " + HangmanContract.TaleScoreCategoryEntry.TABLE_NAME +
                        "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_LOC_KEY_PLAYER +
                        " = " + HangmanContract.PlayerEntry.TABLE_NAME +
                        "." + HangmanContract.PlayerEntry._ID +
                        " INNER JOIN " + HangmanContract.CategoryEntry.TABLE_NAME +
                        " ON " + HangmanContract.TaleScoreCategoryEntry.TABLE_NAME +
                        "." + HangmanContract.TaleScoreCategoryEntry.COLUMN_LOC_KEY_CATEGORY +
                        " = " + HangmanContract.CategoryEntry.TABLE_NAME +
                        "." + HangmanContract.CategoryEntry._ID +
                        " INNER JOIN " + HangmanContract.LanguageEntry.TABLE_NAME +
                        " ON " + HangmanContract.CategoryEntry.TABLE_NAME +
                        "." + HangmanContract.CategoryEntry.COLUMN_LOC_KEY_LANGUAGE +
                        " = " + HangmanContract.LanguageEntry.TABLE_NAME +
                        "." + HangmanContract.LanguageEntry._ID);

    }

    private static final SQLiteQueryBuilder sPlayerQueryBuilder;
    static {
        sPlayerQueryBuilder = new SQLiteQueryBuilder();
        sPlayerQueryBuilder.setTables(HangmanContract.PlayerEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sCategoryLanguageQueryBuilder;
    static {
        sCategoryLanguageQueryBuilder = new SQLiteQueryBuilder();
        sCategoryLanguageQueryBuilder.setTables(HangmanContract.CategoryEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sLevelLanguageQueryBuilder;
    static {
        sLevelLanguageQueryBuilder = new SQLiteQueryBuilder();
        sLevelLanguageQueryBuilder.setTables(HangmanContract.LevelEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sPlayerLastUsedQueryBuilder;
    static {
        sPlayerLastUsedQueryBuilder = new SQLiteQueryBuilder();
        sPlayerLastUsedQueryBuilder.setTables(HangmanContract.PlayerEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sLanguageLastUsedQueryBuilder;
    static {
        sLanguageLastUsedQueryBuilder = new SQLiteQueryBuilder();
        sLanguageLastUsedQueryBuilder.setTables(HangmanContract.LanguageEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sScoreModelCategoryNumErrorsQueryBuilder;
    static {
        sScoreModelCategoryNumErrorsQueryBuilder = new SQLiteQueryBuilder();
        sScoreModelCategoryNumErrorsQueryBuilder.setTables(HangmanContract.ScoreModelEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sTaleOverallPlayerCategoryQueryBuilder;
    static {
        sTaleOverallPlayerCategoryQueryBuilder = new SQLiteQueryBuilder();
        sTaleOverallPlayerCategoryQueryBuilder.setTables(HangmanContract.TaleOverallEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sTaleOverallPlayerQueryBuilder;
    static {
        sTaleOverallPlayerQueryBuilder = new SQLiteQueryBuilder();
        sTaleOverallPlayerQueryBuilder.setTables(HangmanContract.TaleOverallEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sCountWordsUsageWithLanguageCategoryLevelQueryBuilder;
    static {
        sCountWordsUsageWithLanguageCategoryLevelQueryBuilder = new SQLiteQueryBuilder();
        sCountWordsUsageWithLanguageCategoryLevelQueryBuilder.setTables(HangmanContract.WordEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sImageQueryBuilder;
    static {
        sImageQueryBuilder = new SQLiteQueryBuilder();
        sImageQueryBuilder.setTables(HangmanContract.ImageEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sImageLastUsedQueryBuilder;
    static {
        sImageLastUsedQueryBuilder = new SQLiteQueryBuilder();
        sImageLastUsedQueryBuilder.setTables(HangmanContract.ImageEntry.TABLE_NAME);
    }

    //CATEGORY.LANGUAGE_ID = ?
    private static final String sCategoryLanguageSelection =
            HangmanContract.CategoryEntry.TABLE_NAME +
                    "." + HangmanContract.CategoryEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? ";

    //LEVEL.LANGUAGE_ID = ?
    private static final String sLevelLanguageSelection =
            HangmanContract.LevelEntry.TABLE_NAME +
                    "." + HangmanContract.LevelEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? ";

    //PLAYER.LAST_USED = ?
    private static final String sPlayerLastUsedSelection =
            HangmanContract.PlayerEntry.TABLE_NAME +
                    "." + HangmanContract.PlayerEntry.COLUMN_LAST_USED + " = ? ";

    //LANGUAGE.LAST_USED = ?
    private static final String sLanguageLastUsedSelection =
            HangmanContract.LanguageEntry.TABLE_NAME +
                    "." + HangmanContract.LanguageEntry.COLUMN_LAST_USED + " = ? ";

    //SCORE_MODEL.CATEGORY_ID = ?
    //SCORE_MODEL.NUM_ERRORS = ?
    private static final String sScoreModelCategoryNumErrorsSelection =
            HangmanContract.ScoreModelEntry.TABLE_NAME +
                    "." + HangmanContract.ScoreModelEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
            HangmanContract.ScoreModelEntry.TABLE_NAME +
                    "." + HangmanContract.ScoreModelEntry.COLUMN_NUM_ERRORS + " = ?";

    //WORD.LANGUAGE_ID = ?
    //WORD.CATEGORY_ID = ?
    //WORD.WORD_USED = ?
    //WORD.LEVEL_ID = ?
    private static final String sWordLanguageCategoryLevelSelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_WORD_USED + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LEVEL + " = ?";

    //WORD.LANGUAGE_ID = ?
    //WORD.CATEGORY_ID = ?
    //WORD.WORD_USED = ?
    private static final String sWordLanguageCategorySelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_WORD_USED + " = ?";

    //WORD.LANGUAGE_ID = ?
    //WORD.CATEGORY_ID = ?
    //WORD.LEVEL_ID = ?
    private static final String sCountWordsUsageWithLanguageCategoryLevelSelection =
            HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LANGUAGE + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_CATEGORY + " = ? AND " +
                    HangmanContract.WordEntry.TABLE_NAME +
                    "." + HangmanContract.WordEntry.COLUMN_LOC_KEY_LEVEL + " = ?";

    //PLAYER.TALE_PLAYER = ?
    //LANGUAGE.LAST_USED = ?
    private static final String sTaleScoreCategoryPlayerCategorySelection =
            HangmanContract.PlayerEntry.TABLE_NAME +
                    "." + HangmanContract.PlayerEntry.COLUMN_TALE_PLAYER + " = ? AND " +
                    HangmanContract.LanguageEntry.TABLE_NAME +
                    "." + HangmanContract.LanguageEntry.COLUMN_LAST_USED + " = ?";

    //TALE_OVERALL.PLAYER_ID = ?
    //TALE_OVERALL.CATEGORY_ID = ?
    private static final String sTaleOverallPlayerCategorySelection =
            HangmanContract.TaleOverallEntry.TABLE_NAME +
                    "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_PLAYER + " = ? AND " +
                    HangmanContract.TaleOverallEntry.TABLE_NAME +
                    "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_CATEGORY + " = ?";

    //TALE_OVERALL.PLAYER_ID = ?
    private static final String sTaleOverallPlayerSelection =
            HangmanContract.TaleOverallEntry.TABLE_NAME +
                    "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_PLAYER + " = ?";

    //TALE_OVERALL.PLAYER_ID = ?
    //TALE_OVERALL.CATEGORY_ID = ?
    private static final String sTaleOverallPlayerGroupBy =
            HangmanContract.TaleOverallEntry.TABLE_NAME +
                    "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_PLAYER + ", " +
            HangmanContract.TaleOverallEntry.TABLE_NAME +
                    "." + HangmanContract.TaleOverallEntry.COLUMN_LOC_KEY_CATEGORY;

    //IMAGE.LAST_USED = ?
    private static final String sImageLastUsedSelection =
            HangmanContract.ImageEntry.TABLE_NAME +
                    "." + HangmanContract.ImageEntry.COLUMN_LAST_USED + " = ? ";

    private Cursor getCategoryByLanguage(
            Uri uri, String[] projection, String sortOrder) {
        String language = HangmanContract.CategoryEntry.getLanguageFromUri(uri);

        return sCategoryLanguageQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCategoryLanguageSelection,
                new String[]{language},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getLevelByLanguage(
            Uri uri, String[] projection, String sortOrder) {
        String language = HangmanContract.LevelEntry.getLanguageFromUri(uri);

        return sLevelLanguageQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLevelLanguageSelection,
                new String[]{language},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPlayerByLastUsed(
            Uri uri, String[] projection, String sortOrder) {
        String lastUsed = HangmanContract.PlayerEntry.getLastUsedFromUri(uri);

        return sPlayerLastUsedQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPlayerLastUsedSelection,
                new String[]{lastUsed},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getLanguageByLastUsed(
            Uri uri, String[] projection, String sortOrder) {
        String lastUsed = HangmanContract.LanguageEntry.getLastUsedFromUri(uri);

        return sLanguageLastUsedQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLanguageLastUsedSelection,
                new String[]{lastUsed},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getScoreModelByCategoryNumErrors(
            Uri uri, String[] projection) {
        String category = HangmanContract.ScoreModelEntry.getCategoryFromUri(uri);
        String numErrors = HangmanContract.ScoreModelEntry.getNumErrorsFromUri(uri);

        return sScoreModelCategoryNumErrorsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sScoreModelCategoryNumErrorsSelection,
                new String[]{category, numErrors},
                null,
                null,
                null
        );
    }

    private Cursor getWordByLanguageCategoryLevel(
            Uri uri, String[] projection, String sortOrder) {
        String language = HangmanContract.WordEntry.getLanguageFromUri(uri);
        String category = HangmanContract.WordEntry.getCategoryFromUri(uri);
        String isUsed = HangmanContract.WordEntry.getIsUsedFromUri(uri);
        String level = HangmanContract.WordEntry.getLevelFromUri(uri);

        return sWordLanguageCategoryLevelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sWordLanguageCategoryLevelSelection,
                new String[]{language, category, isUsed, level},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWordByLanguageCategory(
            Uri uri, String[] projection, String sortOrder) {
        String language = HangmanContract.WordEntry.getLanguageFromUri(uri);
        String category = HangmanContract.WordEntry.getCategoryFromUri(uri);
        String isUsed = HangmanContract.WordEntry.getIsUsedFromUri(uri);

        return sWordLanguageCategoryLevelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sWordLanguageCategorySelection,
                new String[]{language, category, isUsed},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCountWordsUsageWithLanguageCategoryLevel(
            Uri uri, String[] projection, String sortOrder) {
        String language = HangmanContract.WordEntry.getLanguageFromUri(uri);
        String category = HangmanContract.WordEntry.getCategoryFromUri(uri);
        String level = HangmanContract.WordEntry.getLevelForWordUsageFromUri(uri);

        return sCountWordsUsageWithLanguageCategoryLevelQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCountWordsUsageWithLanguageCategoryLevelSelection,
                new String[]{language, category, level},
                WORD_GROUP_BY_FIELD,
                null,
                sortOrder
        );
    }

    private Cursor getTaleScoreAndCategoryByPlayerCategory(
            Uri uri, String[] projection, String sortOrder) {
        String talePlayer = HangmanContract.TaleScoreCategoryEntry.getTalePlayerFromUri(uri);
        String languageLastUsed = HangmanContract.TaleScoreCategoryEntry.getLanguageLastUsedFromUri(uri);

        return sTaleScoreCategoryPlayerCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTaleScoreCategoryPlayerCategorySelection,
                new String[]{talePlayer, languageLastUsed},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getNumStarsByPlayerCategory(
            Uri uri, String[] projection, String sortOrder) {
        String player = HangmanContract.TaleOverallEntry.getPlayerFromUri(uri);
        String category = HangmanContract.TaleOverallEntry.getCategoryFromUri(uri);

        return sTaleOverallPlayerCategoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTaleOverallPlayerCategorySelection,
                new String[]{player, category},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getNumStarsByPlayer(
            Uri uri, String[] projection, String sortOrder) {
        String player = HangmanContract.TaleOverallEntry.getPlayerFromUri(uri);

        return sTaleOverallPlayerQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTaleOverallPlayerSelection,
                new String[]{player},
                sTaleOverallPlayerGroupBy,
                null,
                sortOrder
        );
    }

    private Cursor getImageByLastUsed(
            Uri uri, String[] projection, String sortOrder) {
        String lastUsed = HangmanContract.ImageEntry.getLastUsedFromUri(uri);

        return sImageLastUsedQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sImageLastUsedSelection,
                new String[]{lastUsed},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HangmanContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, HangmanContract.PATH_PLAYER, PLAYER);
        matcher.addURI(authority, HangmanContract.PATH_PLAYER + "/#", PLAYER_WITH_LAST_USED);
        matcher.addURI(authority, HangmanContract.PATH_LANGUAGE, LANGUAGE);
        matcher.addURI(authority, HangmanContract.PATH_LANGUAGE + "/#", LANGUAGE_WITH_LAST_USED);
        matcher.addURI(authority, HangmanContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, HangmanContract.PATH_CATEGORY + "/#", CATEGORY_WITH_LANGUAGE);
        matcher.addURI(authority, HangmanContract.PATH_LEVEL, LEVEL);
        matcher.addURI(authority, HangmanContract.PATH_LEVEL + "/#", LEVEL_WITH_LANGUAGE);
        matcher.addURI(authority, HangmanContract.PATH_WORD, WORD);
        matcher.addURI(authority, HangmanContract.PATH_WORD + "/#/#/#/#", WORD_WITH_LANGUAGE_AND_CATEGORY_AND_LEVEL_NOT_USED);
        //matcher.addURI(authority, HangmanContract.PATH_WORD + "/#/#/#", WORD_WITH_LANGUAGE_AND_CATEGORY_NOT_USED);
        matcher.addURI(authority, HangmanContract.PATH_WORD + "/#/#/#", WORD_COUNT_USAGE_WITH_LANGUAGE_CATEGORY_LEVEL);
        matcher.addURI(authority, HangmanContract.PATH_TALE_SCORE_CATEGORY, TALE_SCORE_CATEGORY);
        matcher.addURI(authority, HangmanContract.PATH_TALE_SCORE_CATEGORY + "/#/#", TALE_SCORE_CATEGORY_WITH_PLAYER_AND_CATEGORY);
        matcher.addURI(authority, HangmanContract.PATH_SCORE_MODEL, SCORE_MODEL);
        matcher.addURI(authority, HangmanContract.PATH_SCORE_MODEL + "/#/#", SCORE_MODEL_WITH_CATEGORY_AND_NUM_ERRORS);
        matcher.addURI(authority, HangmanContract.PATH_TALE_OVERALL, TALE_OVERALL);
        matcher.addURI(authority, HangmanContract.PATH_TALE_OVERALL + "/#/#", TALE_OVERALL_WITH_PLAYER_AND_CATEGORY);
        matcher.addURI(authority, HangmanContract.PATH_TALE_OVERALL + "/#", TALE_OVERALL_WITH_PLAYER);
        matcher.addURI(authority, HangmanContract.PATH_IMAGE, IMAGE);
        matcher.addURI(authority, HangmanContract.PATH_IMAGE + "/#", IMAGE_WITH_LAST_USED);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HangmanDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PLAYER:
                return HangmanContract.PlayerEntry.CONTENT_TYPE;
            case PLAYER_WITH_LAST_USED:
                return HangmanContract.PlayerEntry.CONTENT_ITEM_TYPE;
            case LANGUAGE:
                return HangmanContract.LanguageEntry.CONTENT_TYPE;
            case LANGUAGE_WITH_LAST_USED:
                return HangmanContract.LanguageEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return HangmanContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_WITH_LANGUAGE:
                return HangmanContract.CategoryEntry.CONTENT_TYPE;
            case TALE_SCORE_CATEGORY:
                return HangmanContract.TaleScoreCategoryEntry.CONTENT_TYPE;
            case TALE_SCORE_CATEGORY_WITH_PLAYER_AND_CATEGORY:
                return HangmanContract.TaleScoreCategoryEntry.CONTENT_TYPE;
            case LEVEL:
                return HangmanContract.LevelEntry.CONTENT_TYPE;
            case LEVEL_WITH_LANGUAGE:
                return HangmanContract.LevelEntry.CONTENT_TYPE;
            case WORD:
                return HangmanContract.WordEntry.CONTENT_TYPE;
            case WORD_WITH_LANGUAGE_AND_CATEGORY_AND_LEVEL_NOT_USED:
                return HangmanContract.WordEntry.CONTENT_ITEM_TYPE;
            case WORD_WITH_LANGUAGE_AND_CATEGORY_NOT_USED:
                return HangmanContract.WordEntry.CONTENT_ITEM_TYPE;
            case WORD_COUNT_USAGE_WITH_LANGUAGE_CATEGORY_LEVEL:
                return HangmanContract.WordEntry.CONTENT_TYPE;
            case SCORE_MODEL:
                return HangmanContract.ScoreModelEntry.CONTENT_TYPE;
            case SCORE_MODEL_WITH_CATEGORY_AND_NUM_ERRORS:
                return HangmanContract.ScoreModelEntry.CONTENT_ITEM_TYPE;
            case TALE_OVERALL:
                return HangmanContract.TaleOverallEntry.CONTENT_TYPE;
            case TALE_OVERALL_WITH_PLAYER_AND_CATEGORY:
                return HangmanContract.TaleOverallEntry.CONTENT_TYPE;
            case TALE_OVERALL_WITH_PLAYER:
                return HangmanContract.TaleOverallEntry.CONTENT_TYPE;
            case IMAGE:
                return HangmanContract.ImageEntry.CONTENT_TYPE;
            case IMAGE_WITH_LAST_USED:
                return HangmanContract.ImageEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "WORD/#/#/#/#"
            case WORD_WITH_LANGUAGE_AND_CATEGORY_AND_LEVEL_NOT_USED: {
                retCursor = getWordByLanguageCategoryLevel(uri, projection, sortOrder);
                break;
            }
            // "WORD/#/#/#"
            case WORD_WITH_LANGUAGE_AND_CATEGORY_NOT_USED: {
                retCursor = getWordByLanguageCategory(uri, projection, sortOrder);
                break;
            }
            // "WORD/#/#/#"
            case WORD_COUNT_USAGE_WITH_LANGUAGE_CATEGORY_LEVEL: {
                retCursor = getCountWordsUsageWithLanguageCategoryLevel(uri, projection, sortOrder);
                break;
            }
            // "TALE_SCORE_CATEGORY/#/#"
            case TALE_SCORE_CATEGORY_WITH_PLAYER_AND_CATEGORY: {
                retCursor = getTaleScoreAndCategoryByPlayerCategory(uri, projection, sortOrder);
                break;
            }
            // "TALE_OVERALL/#/#"
            case TALE_OVERALL_WITH_PLAYER_AND_CATEGORY: {
                retCursor = getNumStarsByPlayerCategory(uri, projection, sortOrder);
                break;
            }
            // "TALE_OVERALL/#"
            case TALE_OVERALL_WITH_PLAYER: {
                retCursor = getNumStarsByPlayer(uri, projection, sortOrder);
                break;
            }
            // "SCORE_MODEL/#/#"
            case SCORE_MODEL_WITH_CATEGORY_AND_NUM_ERRORS: {
                retCursor = getScoreModelByCategoryNumErrors(uri, projection);
                break;
            }
            // "CATEGORY/#"
            case CATEGORY_WITH_LANGUAGE: {
                retCursor = getCategoryByLanguage(uri, projection, sortOrder);
                break;
            }
            // "LEVEL/#"
            case LEVEL_WITH_LANGUAGE: {
                retCursor = getLevelByLanguage(uri, projection, sortOrder);
                break;
            }
            // "PLAYER/#"
            case PLAYER_WITH_LAST_USED: {
                retCursor = getPlayerByLastUsed(uri, projection, sortOrder);
                break;
            }
            // "LANGUAGE/#"
            case LANGUAGE_WITH_LAST_USED: {
                retCursor = getLanguageByLastUsed(uri, projection, sortOrder);
                break;
            }
            // "IMAGE/#"
            case IMAGE_WITH_LAST_USED: {
                retCursor = getImageByLastUsed(uri, projection, sortOrder);
                break;
            }
            // "PLAYER"
            case PLAYER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.PlayerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "LANGUAGE"
            case LANGUAGE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.LanguageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "CATEGORY"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "TALE_SCORE_CATEGORY"
            case TALE_SCORE_CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.TaleScoreCategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "LEVEL"
            case LEVEL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.LevelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "WORD"
            case WORD: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.WordEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "SCORE_MODEL"
            case SCORE_MODEL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.ScoreModelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "TALE_OVERALL"
            case TALE_OVERALL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.TaleOverallEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "IMAGE"
            case IMAGE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HangmanContract.ImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLAYER: {
                long _id = db.insert(HangmanContract.PlayerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.PlayerEntry.buildPlayerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LANGUAGE: {
                long _id = db.insert(HangmanContract.LanguageEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.LanguageEntry.buildLanguageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(HangmanContract.CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TALE_SCORE_CATEGORY: {
                long _id = db.insert(HangmanContract.TaleScoreCategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.TaleScoreCategoryEntry.buildTaleScoreCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LEVEL: {
                long _id = db.insert(HangmanContract.LevelEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.LevelEntry.buildLevelUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case WORD: {
                long _id = db.insert(HangmanContract.WordEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.WordEntry.buildWordUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SCORE_MODEL: {
                long _id = db.insert(HangmanContract.ScoreModelEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.ScoreModelEntry.buildScoreModelUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TALE_OVERALL: {
                long _id = db.insert(HangmanContract.TaleOverallEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.TaleOverallEntry.buildTaleOverallUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case IMAGE: {
                long _id = db.insert(HangmanContract.ImageEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HangmanContract.ImageEntry.buildImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case WORD:
                rowsDeleted = db.delete(
                        HangmanContract.WordEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LANGUAGE:
                rowsDeleted = db.delete(
                        HangmanContract.LanguageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLAYER:
                rowsDeleted = db.delete(
                        HangmanContract.PlayerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        HangmanContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TALE_SCORE_CATEGORY:
                rowsDeleted = db.delete(
                        HangmanContract.TaleScoreCategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LEVEL:
                rowsDeleted = db.delete(
                        HangmanContract.LevelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCORE_MODEL:
                rowsDeleted = db.delete(
                        HangmanContract.ScoreModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TALE_OVERALL:
                rowsDeleted = db.delete(
                        HangmanContract.TaleOverallEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGE:
                rowsDeleted = db.delete(
                        HangmanContract.ImageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLAYER:
                rowsUpdated = db.update(HangmanContract.PlayerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LANGUAGE:
                rowsUpdated = db.update(HangmanContract.LanguageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CATEGORY:
                rowsUpdated = db.update(HangmanContract.CategoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TALE_SCORE_CATEGORY:
                rowsUpdated = db.update(HangmanContract.TaleScoreCategoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LEVEL:
                rowsUpdated = db.update(HangmanContract.LevelEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case WORD:
                rowsUpdated = db.update(HangmanContract.WordEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SCORE_MODEL:
                rowsUpdated = db.update(HangmanContract.ScoreModelEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TALE_OVERALL:
                rowsUpdated = db.update(HangmanContract.TaleOverallEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case IMAGE:
                rowsUpdated = db.update(HangmanContract.ImageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HangmanContract.WordEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(16)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}

