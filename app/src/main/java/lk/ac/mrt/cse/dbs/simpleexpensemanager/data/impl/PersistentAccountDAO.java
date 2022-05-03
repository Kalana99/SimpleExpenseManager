package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    public DBHelper dbHelper;

    public PersistentAccountDAO(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return this.dbHelper.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return this.dbHelper.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return this.dbHelper.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        this.dbHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        this.dbHelper.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        this.dbHelper.updateBalance(accountNo, expenseType, amount);
    }
}
