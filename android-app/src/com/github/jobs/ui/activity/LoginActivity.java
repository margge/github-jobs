package com.github.jobs.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.github.jobs.R;
import com.github.jobs.utils.TextWatcherAdapter;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cristian
 */
public class LoginActivity extends TrackActivity {
    private AccountManager accountManager;
    private TextWatcher watcher = validationTextWatcher();

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;
    private AutoCompleteTextView mEmailText;

    @InjectView(R.id.et_password)
    private EditText mPasswordText;
    @InjectView(R.id.btn_signin)
    private Button mSigninButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);
        //String email = intent.getStringExtra(PARAM_USERNAME);
        requestNewAccount = true;// email == null;
        setContentView(R.layout.login_activity);

        mEmailText = (AutoCompleteTextView) findViewById(R.id.et_email);
        mEmailText.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, userEmailAccounts()));

        mPasswordText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && KeyEvent.ACTION_DOWN == event.getAction()
                        && keyCode == KeyEvent.KEYCODE_ENTER && mSigninButton.isEnabled()) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && mSigninButton.isEnabled()) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailText.addTextChangedListener(watcher);
        mPasswordText.addTextChangedListener(watcher);
    }

    private List<String> userEmailAccounts() {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (Account account : accounts)
            emailAddresses.add(account.name);
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        boolean populated = populated(mEmailText) && populated(mPasswordText);
        mSigninButton.setEnabled(populated);
    }

    private boolean populated(EditText editText) {
        return editText.length() > 0;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     */
    public void handleLogin() {
    }
}