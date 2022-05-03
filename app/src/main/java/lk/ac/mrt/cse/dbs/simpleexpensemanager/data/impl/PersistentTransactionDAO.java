package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    public DBHelper dbHelper;

    public PersistentTransactionDAO(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        this.dbHelper.logTransaction(date, accountNo, expenseType, amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs(){

        return this.dbHelper.getAllTransactionLogs();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit){

        return this.dbHelper.getPaginatedTransactionLogs(limit);
    }
}
