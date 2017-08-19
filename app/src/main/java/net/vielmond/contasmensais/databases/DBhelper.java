/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vielmond.contasmensais.databases;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 *
 * @author vielmond
 */
public class DBhelper extends SQLiteOpenHelper {

    //versao 2.0 em diante
    private Context myContext;
    private String DB_PATH = "";
    private SQLiteDatabase myDataBase;

    private static SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
    private static SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
    private static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
    private static String dia = sdfDay.format(new Date());
    private static String mes = sdfMonth.format(new Date());
    private static String ano = sdfYear.format(new Date());

    // DATABASE DB_NAME
    static final String DB_NAME = "contas.db";
    static final int DB_VERSION = 1;

    // TABLE TABLE_MESES
    public static final String TABLE_MESES = "meses";
    public static final String MESES_ID = "_id";
    public static final String MESES_MES = "mes";
    public static final String MESES_ANO = "ano";

    // TABLE TABLE_LANCAMENTOS
    public static final String TABLE_LANCAMENTOS = "lancamentos";
    public static final String LANCAMENTO_ID = "_id";
    public static final String LANCAMENTO_FK_MES = "id_mes_fk";
    public static final String LANCAMENTO_DESCRICAO = "descricao";
    public static final String LANCAMENTO_SHOW = "show";

    // TABLE TABLE_SETTINGS
    public static final String TABLE_SETTINGS = "settings";
    public static final String PASSWD = "passwd";
    public static final String MAILPASSWD = "emailwd";

    // TABLE CREATE_CONTA
    private static final String CREATE_CONTA = "CREATE TABLE "
            + TABLE_MESES + "("
            + MESES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MESES_MES + " TEXT NOT NULL, "
            + MESES_ANO + " TEXT NOT NULL);";

    // TABLE CREATE_LANCAMENTOS
    private static final String CREATE_LANCAMENTOS = "CREATE TABLE "
            + TABLE_LANCAMENTOS + "(" + LANCAMENTO_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LANCAMENTO_FK_MES + " INTEGER NOT NULL, "
            + LANCAMENTO_DESCRICAO + " TEXT NOT NULL, "
            + LANCAMENTO_SHOW + " BOOLEAN DEFAULT 1 "
            + " FOREIGN KEY(" + LANCAMENTO_FK_MES + ") REFERENCES " + TABLE_MESES + "(" + MESES_ID + "));";

    // TABLE CREATE_SETTINGS
    private static final String CREATE_SETTINGS = "CREATE TABLE "
            + TABLE_SETTINGS + "("
            + MAILPASSWD + " TEXT NOT NULL, "
            + PASSWD + " TEXT NOT NULL);";

    // INSERT INTO SETTINGS
    private static final String INSERT_SETTINGS = "INSERT INTO "
            + TABLE_SETTINGS + "("
            + MAILPASSWD + ", "
            + PASSWD + ") "
            + " VALUES ('R$','email@email.com','');";

    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

        DB_PATH = "/data/data/"
                + context.getApplicationContext().getPackageName()
                + "/databases/";
    }

    public DBhelper open() throws SQLException {
        myDataBase = getWritableDatabase();

        Log.d(TAG, "DbHelper Opening Version: " + this.myDataBase.getVersion());
        return this;
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null) {
            myDataBase.close();
        }

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CONTA);
            db.execSQL(CREATE_LANCAMENTOS);
            db.execSQL(CREATE_SETTINGS);
            db.execSQL(INSERT_SETTINGS);
        } catch (SQLException error) {
            System.out.println(error);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
//            onCreate(db);
        } else {
            Log.d(TAG, "DB Atualizado!");
        }
    }

    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME + "_cp";

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        myDataBase.setVersion(DB_VERSION);
    }

}
