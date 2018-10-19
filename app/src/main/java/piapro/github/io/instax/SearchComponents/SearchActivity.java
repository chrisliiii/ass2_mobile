package piapro.github.io.instax.SearchComponents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import piapro.github.io.instax.FirebaseModels.User;
import piapro.github.io.instax.ProfileComponents.ProfileActivity;
import piapro.github.io.instax.R;
import piapro.github.io.instax.Utilities.BottomNavigationViewHelper;
import piapro.github.io.instax.Utilities.UserListAdapter;

public class SearchActivity extends AppCompatActivity{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY = 1;

    private Context sContext = SearchActivity.this;

    //tools
    private EditText sSearchParam;
    private ListView sListView;

    private List<User> sUserList;
    private UserListAdapter sAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: Start");

        sSearchParam = (EditText) findViewById(R.id.search);
        sListView = (ListView) findViewById(R.id.listView);
        btmNaviViewSetup();

        hideSoftKeyboard();
        btmNaviViewSetup();
        initTextListener();
    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initialize");

        sUserList = new ArrayList<>();

        sSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = sSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchMatchName(text);
            }
        });
    }

    private void searchMatchName(String keyword){
        Log.d(TAG, "searchMatchName: searching for a match: " + keyword);
        sUserList.clear();
        //update the users list view
        if(keyword.length() ==0){

        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.db_users))
                    .orderByChild(getString(R.string.fd_username)).startAt(keyword).endAt(keyword + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                        sUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        sAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_list, sUserList);

        sListView.setAdapter(sAdapter);

        sListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + sUserList.get(position).toString());

                //navigate to profile activity
                Intent intent =  new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.activity_call), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), sUserList.get(position));
                startActivity(intent);
            }
        });
    }

    private void hideSoftKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //setup bottom navigation bar
    private void btmNaviViewSetup(){
        Log.d(TAG, "btmNaviViewSetup: Bottom Navigation View Setting up");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNaviBar);
        BottomNavigationViewHelper.bottomNavigationViewSetup(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(sContext, this, bottomNavigationViewEx);//Parse the context
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY);
        menuItem.setChecked(true);
    }
}


