package com.apress.gerber.reminders;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        if (savedInstanceState == null) {
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

        Cursor cursor = mDbAdapter.fetchAllReminders();
        //from columns defined in the db
        String[] from = new String[]{RemindersDbAdapter.COL_CONTENT};
        //to the ids of views in the layout
        int[] to = new int[]{R.id.row_text};
        mCursorAdapter = new RemindersSimpleCursorAdapter(
                RemindersActivity.this, R.layout.reminders_row, cursor, from, to, 0);
        // the cursorAdapter (controller) is now updating the listView (view)
        //with data from the db (model)
        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String[] modes = new String[]{"Edit Reminder", "Delete Reminder"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(RemindersActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            int nId=getIdFromPosition(masterListPosition);
                            Reminder reminder=mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);
                        } else {
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater=mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete_reminder:
                            for(int nC=mCursorAdapter.getCount()-1;nC>=0;nC--){
                                if(mListView.isItemChecked(nC)){
                                    mDbAdapter.deleteReminderById(getIdFromPosition(nC));
                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }
            });
        }
    }

    private int getIdFromPosition(int nC) {
        return (int) mCursorAdapter.getItemId(nC);
    }

    private void insertSomeReminders(String name, boolean important) {
        mDbAdapter.createReminder(name, important);
    }

    private void fireCustomDialog(final Reminder reminder){
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView titleView=(TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom=(EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton=(Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox=(CheckBox)dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout=(LinearLayout)dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation=(reminder!=null);
        if(isEditOperation){
            titleView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant()==1);
            editCustom.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }
        commitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String reminderText=editCustom.getText().toString();
                if(isEditOperation){
                    Reminder reminderEdited=new Reminder(
                            reminder.getId(),
                            reminderText,
                            checkBox.isChecked()?1:0
                    );
                    mDbAdapter.updateReminder(reminderEdited);
                }else {
                    mDbAdapter.createReminder(reminderText,checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        Button buttonCancel=(Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
                fireCustomDialog(null);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
