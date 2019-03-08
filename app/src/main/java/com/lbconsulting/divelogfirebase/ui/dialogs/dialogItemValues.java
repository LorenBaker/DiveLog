package com.lbconsulting.divelogfirebase.ui.dialogs;

import android.support.v4.app.DialogFragment;
import android.view.View;


/**
 * Created by Loren on 1/14/2015.
 * This dialog allows the user to select, edit, deleteRecord, and create new items
 */
public class dialogItemValues extends DialogFragment implements View.OnClickListener {

/*    private TextView tvDialogTitle;
    private EditText txtItemName;
    private ListView lvItems;
    private Button btnNewItem;
    private LinearLayout llSave;
    private Button btnCancel;
    private Button btnSave;
    private final int NORMAL_MODE = 10;

    private final int EDIT_MODE = 20;
    private final int NEW_MODE = 30;
    private int mMode = NORMAL_MODE;

    private int mDiveLogDetailButtonID;
    private clsDiveLogDetail mDiveLogRecord;
    private clsItemValue mItemRecord;
    private ArrayList<clsItemValue> mItemsArray = null;
    private String dialogTitle = "";

    public static dialogItemValues newInstance(int buttonID, String diveLogID) {
        dialogItemValues fragment = new dialogItemValues();
        Bundle args = new Bundle();
        args.putInt(MySettings.ARG_BUTTON_ID, buttonID);
        args.putString(MySettings.ARG_DIVE_LOG_ID, diveLogID);
        fragment.setArguments(args);
        return fragment;
    }

    public dialogItemValues() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogItemValues", "onCreate()");
        Bundle args = getArguments();
        if (args.containsKey(MySettings.ARG_BUTTON_ID)) {
            mDiveLogDetailButtonID = args.getInt(MySettings.ARG_BUTTON_ID);
            String diveLogID = args.getString(MySettings.ARG_DIVE_LOG_ID);
            DbxRecord record = MainActivity.getDiveLogsTable().getRecord(diveLogID);
            if (record != null) {
                mDiveLogRecord = new clsDiveLogDetail(record);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("dialogItemValues", "onCreateView()");

        // Inflate view
        View view = inflater.inflate(R.layout.dialog_item_values, container);
        if (view != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            tvDialogTitle = (TextView) view.findViewById(R.id.tvDialogTitle);

            txtItemName = (EditText) view.findViewById(R.id.txtItemName);

            lvItems = (ListView) view.findViewById(R.id.lvItems);

            btnNewItem = (Button) view.findViewById(R.id.btnNewItem);
            btnNewItem.setOnClickListener(this);

            llSave = (LinearLayout) view.findViewById(R.id.llSave);
            btnCancel = (Button) view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(this);

            btnSave = (Button) view.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);

            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Select item
                    mMode = NORMAL_MODE;
                    mItemRecord = mItemsArray.get(position);
                    selectItem();
                }
            });

            lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // Edit selected item
                    mMode = EDIT_MODE;
                    mItemRecord = mItemsArray.get(position);
                    if (!mItemRecord.getID().equals(ItemsTable.DEFAULT_ID)) {
                        tvDialogTitle.setText("Edit " + dialogTitle);
                        txtItemName.setText(mItemRecord.getItemName());
                        txtItemName.setHint("Enter " + dialogTitle);
                        showEditConfiguration();
                    }
                    return true;
                }
            });
        }
        return view;
    }

    private void selectItem() {
        switch (mDiveLogDetailButtonID) {
            case R.id.btnDiveTank:
                mDiveLogRecord.setDiveTankTypeID(mItemRecord.getID(), true);
                break;
            case R.id.btnCurrentConditions:
                mDiveLogRecord.setCurrentConditionsID(mItemRecord.getID(), true);
                break;
            case R.id.btnDiveEntries:
                mDiveLogRecord.setDiveEntriesID(mItemRecord.getID(), true);
                break;
            case R.id.btnDiveStyles:
                mDiveLogRecord.setDiveStylesID(mItemRecord.getID(), true);
                break;
            case R.id.btnDiveTypes:
                mDiveLogRecord.setDiveTypeID(mItemRecord.getID(), true);
                break;
            case R.id.btnSeaConditions:
                mDiveLogRecord.setSeaConditionsID(mItemRecord.getID(), true);
                break;
            case R.id.btnWeatherConditions:
                mDiveLogRecord.setWeatherConditionsID(mItemRecord.getID(), true);
                break;
        }
        // may not need to do this if this becomes a regular fragment.
        EventBus.getDefault().post(new Events.UpdateButtonUI(mDiveLogDetailButtonID, mDiveLogRecord.getID()));
        getDialog().dismiss();
    }

    private void editItem() {

    }

    private void updateUI() {
        // fill the list view
        String newPrefix = "New";
        switch (mDiveLogDetailButtonID) {
            case R.id.btnDiveTank:
                dialogTitle = "Tank Types";
                mItemsArray = MainActivity.getTankTypeTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnCurrentConditions:
                dialogTitle = "Currents";
                mItemsArray = MainActivity.getCurrentConditionsTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnDiveEntries:
                dialogTitle = "Dive Entry";
                mItemsArray = MainActivity.getDiveEntriesTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnDiveStyles:
                dialogTitle = "Dive Style";
                mItemsArray = MainActivity.getDiveStylesTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnDiveTypes:
                dialogTitle = "Dive Type";
                mItemsArray = MainActivity.getDiveTypesTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnSeaConditions:
                dialogTitle = "Sea Conditions";
                mItemsArray = MainActivity.getSeaConditionsTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
            case R.id.btnWeatherConditions:
                dialogTitle = "Weather";
                mItemsArray = MainActivity.getWeatherConditionsTable().getValues(MySettings.SORT_ALPHABETICAL);
                break;
        }

        tvDialogTitle.setText(dialogTitle);
        btnNewItem.setText(newPrefix + " " + dialogTitle);

        if (mItemsArray != null) {
            ArrayAdapter adapter = new ArrayAdapter<clsItemValue>
                    (getActivity(), android.R.layout.simple_list_item_1, mItemsArray);
            if (lvItems != null) {
                lvItems.setAdapter(adapter);
            }
        }
    }

    private void showKeyBoard() {
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(txtItemName, 0);
    }

    private void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(txtItemName.getWindowToken(), 0);
    }

    private void showEditConfiguration() {
        txtItemName.setVisibility(View.VISIBLE);
        llSave.setVisibility(View.VISIBLE);
        btnNewItem.setVisibility(View.GONE);
        lvItems.setVisibility(View.GONE);
        showKeyBoard();
    }

    private void hideEditConfiguration() {
        txtItemName.setVisibility(View.GONE);
        llSave.setVisibility(View.GONE);
        btnNewItem.setVisibility(View.VISIBLE);
        lvItems.setVisibility(View.VISIBLE);
        hideKeyboard();
    }

    @Override
    public void onResume() {
        MyLog.i("dialogItemValues", "onResume()");
        updateUI();
        super.onResume();

    }

    @Override
    public void onPause() {
        MyLog.i("dialogItemValues", "onPause()");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        MyLog.i("dialogItemValues", "onDestroy()");
        super.onDestroy();
    }*/

    @Override
    public void onClick(View v) {
 /*       String prefix = "";
        switch (v.getId()) {
            case R.id.btnNewItem:
                mMode = NEW_MODE;
                prefix = "New ";
                switch (mDiveLogDetailButtonID) {
                    case R.id.btnDiveTank:
                        mItemRecord = MainActivity.getTankTypeTable().CreateNewItem();
                        break;
                    case R.id.btnCurrentConditions:
                        mItemRecord = MainActivity.getCurrentConditionsTable().CreateNewItem();
                        break;
                    case R.id.btnDiveEntries:
                        mItemRecord = MainActivity.getDiveEntriesTable().CreateNewItem();
                        break;
                    case R.id.btnDiveStyles:
                        mItemRecord = MainActivity.getDiveStylesTable().CreateNewItem();
                        break;
                    case R.id.btnDiveTypes:
                        mItemRecord = MainActivity.getDiveTypesTable().CreateNewItem();
                        break;
                    case R.id.btnSeaConditions:
                        mItemRecord = MainActivity.getSeaConditionsTable().CreateNewItem();
                        break;
                    case R.id.btnWeatherConditions:
                        mItemRecord = MainActivity.getWeatherConditionsTable().CreateNewItem();
                        break;
                }

                txtItemName.setText("");
                tvDialogTitle.setText(prefix + dialogTitle);
                txtItemName.setHint("Enter " + prefix + dialogTitle);
                showEditConfiguration();

                // Toast.makeText(getActivity(), "TO COME: btnNewItem", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCancel:
                switch (mMode) {

                    case EDIT_MODE:
                        hideEditConfiguration();
                        mMode = NORMAL_MODE;
                        break;

                    case NEW_MODE:
                        // deleteRecord the newly created item
                        mItemRecord.delete();
                        mItemRecord = null;
                        hideEditConfiguration();
                        mMode = NORMAL_MODE;
                        break;
                }
                //Toast.makeText(getActivity(), "TO COME: btnCancel", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnSave:
                switch (mMode) {

                    case EDIT_MODE:
                    case NEW_MODE:
                        mItemRecord.UpdateItemName(txtItemName.getText().toString());
                        mMode = NORMAL_MODE;
                        selectItem();
                        break;
                }
                //Toast.makeText(getActivity(), "TO COME: btnSave", Toast.LENGTH_SHORT).show();
                break;
        }*/
    }
}
