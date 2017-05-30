package com.apress.gerber.reminders;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        mDbAdapter=new RemindersDbAdapter(this);
        mDbAdapter.open();

        if(savedInstanceState==null){
            mDbAdapter.deleteAllReminders();
            insertSomeReminders("Buy Learn Android Studio", true);
            insertSomeReminders("Send Dad birthday gift", false);
            insertSomeReminders("Dinner at the Gage on Friday", false);
            insertSomeReminders("String squash racket", false);
            insertSomeReminders("Shovel and salt walkways", false);
            insertSomeReminders("Prepare Advanced Android syllabus", true);
            insertSomeReminders("Buy new office chair", false);
            insertSomeReminders("Call Auto-body shop for quote", false);
            insertSomeReminders("Renew membership to club", false);
            insertSomeReminders("Buy new Galaxy Android phone", true);
            insertSomeReminders("Sell old Android phone - auction", false);
            insertSomeReminders("Buy new paddles for kayaks", false);
            insertSomeReminders("Call accountant about tax returns", false);
            insertSomeReminders("Buy 300,000 shares of Google", false);
            insertSomeReminders("Call the Dalai Lama back", true);
        }

        Cursor cursor=mDbAdapter.fetchAllReminders();
        //from columns defined in the db
        String[] from=new String[]{RemindersDbAdapter.COL_CONTENT};
        //to the ids of views in the layout
        int[] to=new int[]{R.id.row_text};
        mCursorAdapter=new RemindersSimpleCursorAdapter(
                RemindersActivity.this, R.layout.reminders_row,cursor,from,to,0);
        // the cursorAdapter (controller) is now updating the listView (view)
        //with data from the db (model)
        mListView.setAdapter(mCursorAdapter);
        //The arrayAdatper is the controller in our
        //model-view-controller relationship. (controller)
    }

    private void insertSomeReminders(String name, boolean important) {
        mDbAdapter.createReminder(name, important);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
