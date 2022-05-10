/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Ordering;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentDemoExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {

    private static DBHelper dbHelper;
    private static PersistentDemoExpenseManager expenseManager;

    @BeforeClass
    public static void start(){
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DBHelper(context);
        try {
            expenseManager = new PersistentDemoExpenseManager(dbHelper);
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddAccount(){

        expenseManager.addAccount("22AABB", "PB", "Kalana", 1000.0);

        List<String> acc_no_list = expenseManager.getAccountNumbersList();
        assertTrue(acc_no_list.contains("22AABB"));
    }

    @Test
    public void testIncomeTransaction() throws InvalidAccountException {

        expenseManager.addAccount("22AABB", "PB", "Kalana", 1000.0);
        expenseManager.updateAccountBalance("22AABB", 22, 5, 2022, ExpenseType.INCOME, "2000.0");

        List<Transaction> transactions = expenseManager.getTransactionLogs();
        Transaction last = transactions.get(transactions.size() - 1);
        String acc_no = last.getAccountNo();
        Account acc = dbHelper.getAccount(acc_no);

        assertTrue(acc_no, acc_no.equals("22AABB"));
        assertTrue(String.valueOf(last.getAmount()), last.getAmount() == 2000.0);
        assertTrue(String.valueOf(acc.getBalance()), acc.getBalance() == 3000.0);
    }

//    @Test
//    public void testExpenseTransaction() throws InvalidAccountException {
//
//        expenseManager.updateAccountBalance("22AABB", 22, 5, 2022, ExpenseType.EXPENSE, "250.0");
//
//        List<Transaction> transactions = expenseManager.getTransactionLogs();
//        Transaction last = transactions.get(transactions.size() - 1);
//        String acc_no = last.getAccountNo();
//        Account acc = dbHelper.getAccount(acc_no);
//
//        assertTrue(acc_no.equals("22AABB"));
//        assertTrue(String.valueOf(last.getAmount()), last.getAmount() == 250.0);
//        assertTrue(String.valueOf(acc.getBalance()),acc.getBalance() == 2750.0);
//    }
}