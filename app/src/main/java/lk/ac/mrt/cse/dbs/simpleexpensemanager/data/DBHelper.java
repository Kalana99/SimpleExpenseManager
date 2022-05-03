package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHelper extends SQLiteOpenHelper {


    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_NO = ACCOUNT_TABLE + "No";
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_HOLDER_NAME = ACCOUNT_TABLE + "HolderName";
    public static final String BALANCE = "balance";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";
    public static final String TRANSACTION_TABLE = "trans";

    public DBHelper(@Nullable Context context) {
        super(context, "190530H.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String stm1 = "create table " + ACCOUNT_TABLE + "(" + ACCOUNT_NO + " varchar(255) primary key, " + BANK_NAME + " varchar(255), " + ACCOUNT_HOLDER_NAME + " varchar(255), " + BALANCE + " double)";
        String stm2 = "create table " + TRANSACTION_TABLE + "("+ DATE + " Date, " + ACCOUNT_NO + " varchar(255), " + EXPENSE_TYPE + " varchar(20), " + AMOUNT + " double, foreign key(" + ACCOUNT_NO + ") references " + ACCOUNT_TABLE + ")";
        db.execSQL(stm1);
        db.execSQL(stm2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public List<String> getAccountNumbersList() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<String> return_list = new ArrayList<>();
        String qstr = "select " + ACCOUNT_NO + " from " + ACCOUNT_TABLE;
        Cursor cursor = db.rawQuery(qstr, null);

        if(cursor.moveToFirst()){
            do{
                String acc_no = cursor.getString(0);
                return_list.add(acc_no);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return return_list;
    }

    public List<Account> getAccountsList() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<Account> return_list = new ArrayList<>();
        String qstr = "select * from " + ACCOUNT_TABLE;
        Cursor cursor = db.rawQuery(qstr, null);

        if(cursor.moveToFirst()){
            do{
                String acc_no = cursor.getString(0);
                String bank_name = cursor.getString(1);
                String holder_name = cursor.getString(2);
                Double balance = cursor.getDouble(3);

                return_list.add(new Account(acc_no, bank_name, holder_name, balance));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return return_list;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {

        if (checkAccount(accountNo)) {

            SQLiteDatabase db = this.getReadableDatabase();

            String[] cols = {"*"};
            String selection = ACCOUNT_NO + " = ?";
            String[] selectionArgs = {accountNo};
            Cursor cursor = db.query(ACCOUNT_TABLE, cols, selection, selectionArgs, null, null, null, "1");

            if(cursor.moveToFirst()){
                do{
                    String acc_no = cursor.getString(0);
                    String bank_name = cursor.getString(1);
                    String holder_name = cursor.getString(2);
                    Double balance = cursor.getDouble(3);

                    cursor.close();
                    db.close();

                    return new Account(acc_no, bank_name, holder_name, balance);
                }while(cursor.moveToNext());
            }

            cursor.close();
            db.close();
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    public void addAccount(Account account) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ACCOUNT_NO, account.getAccountNo());
        cv.put(BANK_NAME, account.getBankName());
        cv.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        cv.put(BALANCE, account.getBalance());

        long insert = db.insert(ACCOUNT_TABLE, null, cv);

        db.close();
    }

    public void removeAccount(String accountNo) throws InvalidAccountException {

        if (checkAccount(accountNo)) {

            SQLiteDatabase db = this.getWritableDatabase();

            String selection = ACCOUNT_NO + " = ?";
            String[] selectionArgs = {accountNo};

            db.delete(ACCOUNT_TABLE, selection, selectionArgs);

            db.close();
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        if (!checkAccount(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        Account account = getAccount(accountNo);
        double new_balance = account.getBalance();
        boolean update = true;

        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                if(amount <= new_balance){
                    new_balance -= amount;
                }
                else{
                    update = false;
                }
                break;
            case INCOME:
                new_balance += amount;
                break;
        }

        if(update){

            SQLiteDatabase db = this.getWritableDatabase();

            String selection = ACCOUNT_NO + " = ?";
            String[] selectionArgs = {accountNo};
            ContentValues cv = new ContentValues();
            cv.put(BALANCE, new_balance);

            db.update(ACCOUNT_TABLE, cv, selection, selectionArgs);
            db.close();
        }
    }

    public boolean checkAccount(String an){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] cols = {ACCOUNT_NO};
        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = {an};
        Cursor cursor = db.query(ACCOUNT_TABLE, cols, selection, selectionArgs, null, null, null, "1");

        int c_count = cursor.getCount();

        cursor.close();
        db.close();

        if(c_count > 0){
            return true;
        }
        return false;
    }


    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        if(checkAccount(accountNo)){

            Account acc = getAccount(accountNo);

            if(expenseType == ExpenseType.EXPENSE && amount > acc.getBalance()){
                String msg = "Balance insufficient.";
                throw new InvalidAccountException(msg);
            }
            else{

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues cv = new ContentValues();
                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                cv.put(DATE, dateFormat.format(transaction.getDate()));
                cv.put(ACCOUNT_NO, transaction.getAccountNo());
                cv.put(EXPENSE_TYPE, transaction.getExpenseType().name());
                cv.put(AMOUNT, transaction.getAmount());

                long insert = db.insert(TRANSACTION_TABLE, null, cv);

                db.close();
            }
        }
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    public List<Transaction> getAllTransactionLogs(){

        SQLiteDatabase db = this.getReadableDatabase();

        List<Transaction> return_list = new ArrayList<>();
        String qstr = "select * from " + TRANSACTION_TABLE;
        Cursor cursor = db.rawQuery(qstr, null);

        if(cursor.moveToFirst()){
            do{
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String acc_no = cursor.getString(1);
                ExpenseType expense_type = Enum.valueOf(ExpenseType.class, cursor.getString(2));
                Double amount = cursor.getDouble(3);

                return_list.add(new Transaction(date, acc_no, expense_type, amount));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return return_list;
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit){

        List<Transaction> transactions = getAllTransactionLogs();

        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
